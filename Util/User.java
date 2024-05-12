package Util;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

// Represents a user on Quackstagram
public class User {
    private String username;
    private String bio;
    @SuppressWarnings("unused")
    private String password;

    public User(String username, String bio, String password) {
        this.username = username;
        this.bio = bio;
        this.password = password;
    }

    public User(String username) {
        Connection conn = DatabaseConnection.getConnection();
        try (Statement stmt = conn.createStatement()) {
            String query = "SELECT * FROM User WHERE username = '" + username + "'";
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                this.username = rs.getString("Username");
                this.bio = rs.getString("Bio");
                this.password = rs.getString("Password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User(int userId) {
        Connection conn = DatabaseConnection.getConnection();
        try (Statement stmt = conn.createStatement()) {
            String query = "SELECT * FROM User WHERE id = " + userId;
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                this.username = rs.getString("Username");
                this.bio = rs.getString("Bio");
                this.password = rs.getString("Password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Getter methods for user details
    public String getUsername() {
        return username;
    }

    public String getBio() {
        return bio;
    }

    public String getPassword() {
        return password;
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

    public void setUsername(String username) {
        this.username = username;
        // Update the username in the database
        Connection conn = DatabaseConnection.getConnection();
        try (Statement stmt = conn.createStatement()) {
            String query = "UPDATE User SET Username = '" + username + "' WHERE Username = '" + this.username + "'";
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setPassword(String password) {
        this.password = password;
        // Update the password in the database
        Connection conn = DatabaseConnection.getConnection();
        try (Statement stmt = conn.createStatement()) {
            String query = "UPDATE User SET Password = '" + password + "' WHERE Username = '" + username + "'";
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    public static void setLoggedInUser(User user) {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "INSERT INTO UserSession (sessionId, userId) VALUES (?, ?) ON DUPLICATE KEY UPDATE userId = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Since quackstagram is a single user application, we can use a fixed session id
            // This infrastructure is made in case we want to expand the application later
            pstmt.setInt(1, 1); 
            pstmt.setInt(2, user.getId());
            pstmt.setInt(3, user.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static User getLoggedInUser() {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT userId FROM UserSession WHERE sessionId = ?";
    
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, 1);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("userId");
                return new User(userId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; 
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

    public int getId() {
        Connection conn = DatabaseConnection.getConnection();
        try (Statement stmt = conn.createStatement()) {
            String query = "SELECT * FROM User WHERE Username = '" + username + "'";
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean doesUsernameExist(String username) {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT 1 FROM User WHERE username = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Returns the next available user ID
    public static int getNextId() {
        Connection conn = DatabaseConnection.getConnection();
        try (Statement stmt = conn.createStatement()) {
            String query = "SELECT MAX(id) FROM User";
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


}