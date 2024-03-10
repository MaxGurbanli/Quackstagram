import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ImageLikesManager {

    private final LikesFileHandler fileHandler;
    private Map<String, Set<String>> likesMap;

    public ImageLikesManager(String filePath) {
        this.fileHandler = new LikesFileHandler(filePath);
        loadLikes();
    }

    private void loadLikes() {
        try {
            likesMap = fileHandler.readLikes();
        } catch (IOException e) {
            e.printStackTrace();
            likesMap = null;
        }
    }

    public void addLike(String imageId, String username) {
        Set<String> users = likesMap.computeIfAbsent(imageId, k -> new HashSet<>());
        if (!users.contains(username)) {
            users.add(username);
            updateImageDetailsFile(imageId);
            System.out.println("User liked the image");
        }
        else {
            System.out.println("User already liked the image");
        }
    }

    public void removeLike(String imageId, String username) {
        if (likesMap.containsKey(imageId)) {
            likesMap.get(imageId).remove(username);
            updateImageDetailsFile(imageId);
        }
    }

    public boolean hasLiked(String imageId, String username) {
        Set<String> users = likesMap.getOrDefault(imageId, Collections.emptySet());
        return users.contains(username);
    }

    public int getLikesCount(String imageId) {
        return likesMap.getOrDefault(imageId, Collections.emptySet()).size();
    }

    private void updateImageDetailsFile(String imageId) {
        Path path = Paths.get("img", "image_details.txt");
        try {
            List<String> lines = Files.readAllLines(path);
            List<String> updatedLines = new ArrayList<>();
            for (String line : lines) {
                if (line.contains("ImageID: " + imageId)) {
                    String[] parts = line.split(", ");
                    parts[4] = "Likes: " + getLikesCount(imageId);
                    updatedLines.add(String.join(", ", parts));
                } else {
                    updatedLines.add(line);
                }
            }
            Files.write(path, updatedLines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
