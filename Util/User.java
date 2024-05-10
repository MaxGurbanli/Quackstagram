package Util;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

// Represents a user on Quackstagram
public class User {
    private String username;
    private String bio;
    private String password;

    public User(String username, String bio, String password) {
        this.username = username;
        this.bio = bio;
        this.password = password;
    }

    public User(String username) {
        this.username = username;
    }

    // Getter methods for user details
    public String getUsername() {
        return username;
    }

    public String getBio() {
        return bio;
    }

    public String getUserId() {
        Connection conn = DatabaseConnection.getConnection();
        try (Statement stmt = conn.createStatement()) {
            String query = "SELECT * FROM User WHERE Username = '" + username + "'";
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return rs.getString("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setBio(String bio) {
        this.bio = bio;
        // Update the bio in the database
        Connection conn = DatabaseConnection.getConnection();
        try (Statement stmt = conn.createStatement()) {
            String query = "UPDATE User SET Bio = '" + bio + "' WHERE Username = '" + username + "'";
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getPostsCount() {
        Connection conn = DatabaseConnection.getConnection();
        try (Statement stmt = conn.createStatement()) {
            String query = "SELECT COUNT(*) FROM Picture WHERE authorId = " + getUserId();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getFollowersCount() {
        Connection conn = DatabaseConnection.getConnection();
        try (Statement stmt = conn.createStatement()) {
            String query = "SELECT COUNT(*) FROM Follow WHERE targetId = " + getUserId();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getFollowingCount() {
        Connection conn = DatabaseConnection.getConnection();
        try (Statement stmt = conn.createStatement()) {
            String query = "SELECT COUNT(*) FROM Follow WHERE followerId = " + getUserId();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Picture> getPictures() {
        List<Picture> pictures = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        try (Statement stmt = conn.createStatement()) {
            String query = "SELECT * FROM Picture WHERE UserId = " + getUserId();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String imagePath = rs.getString("imagePath");
                String caption = rs.getString("caption");
                Picture picture = new Picture(imagePath, caption);
                pictures.add(picture);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pictures;
    }

    @Override
    public String toString() {
        return username + ":" + bio + ":" + password;
    }

    public static void setLoggedInUser(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/user.txt", false))) {
            writer.write(user.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static User getLoggedInUser() {
        String loggedInUsername = "";
        try (BufferedReader reader = Files.newBufferedReader(Paths.get("data", "user.txt"))) {
            String line = reader.readLine();
            if (line != null) {
                loggedInUsername = line.split(":")[0].trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new User(loggedInUsername);
    }

    public boolean isCurrentUser() {
        String currentUsername = getLoggedInUser().getUsername();
        return this.username.equals(currentUsername);
    }

    public static User getUserByUserId(int UserId) {
        Connection conn = DatabaseConnection.getConnection();
        try (Statement stmt = conn.createStatement()) {
            String query = "SELECT * FROM User WHERE id = " + UserId;
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                String username = rs.getString("Username");
                String bio = rs.getString("Bio");
                String password = rs.getString("Password");
                return new User(username, bio, password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

    }

}