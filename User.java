import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

// Represents a user on Quackstagram
class User {
    private String username;
    private String bio;
    private String password;
    private int postsCount;
    private int followersCount;
    private int followingCount;
    private List<Picture> pictures;

    public User(String username, String bio, String password) {
        this.username = username;
        this.bio = bio;
        this.password = password;
        this.pictures = new ArrayList<>();
        // Initialize counts to 0
        this.postsCount = 0;
        this.followersCount = 0;
        this.followingCount = 0;
    }

    public User(String username) {
        this.username = username;
    }

    // Add a picture to the user's profile
    public void addPicture(Picture picture) {
        pictures.add(picture);
        postsCount++;
    }

    // Getter methods for user details
    public String getUsername() {
        return username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public int getPostsCount() {
        return postsCount;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public List<Picture> getPictures() {
        return pictures;
    }

    // Setter methods for followers and following counts
    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    public void setPostCount(int postCount) {
        this.postsCount = postCount;
    }
    // Implement the toString method for saving user information

    @Override
    public String toString() {
        return username + ":" + bio + ":" + password; // Format as needed
    }

    public static User getLoggedInUser() {
        String loggedInUsername = "";
        try (BufferedReader reader = Files.newBufferedReader(Paths.get("data", "users.txt"))) {
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
        Path usersFilePath = Paths.get("data", "users.txt");
        try (BufferedReader reader = Files.newBufferedReader(usersFilePath)) {
            String line = reader.readLine();
            if (line != null) {
                String currentUsername = line.split(":")[0];
                return this.username.equals(currentUsername);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static User getUserByImageId (String imageId) {
        // read image_details.txt to find the current image by imageId and the person to whom the image belongs
        // Sample: ImageID: Lorin_1, Username: Lorin, Bio: In the cookie jar my hand was not., Timestamp: 2023-12-17 19:07:43
        Path imageDetailsFilePath = Paths.get("img", "image_details.txt");
        try (BufferedReader reader = Files.newBufferedReader(imageDetailsFilePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String currentImageId = parts[0].split(":")[1].trim();
                if (currentImageId.equals(imageId)) {
                    String username = parts[1].split(":")[1].trim();
                    return new User(username);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}