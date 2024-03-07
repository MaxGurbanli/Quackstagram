import java.io.*;
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
            likesMap = null; // Consider appropriate error handling
        }
    }

    public void addLike(String imageId, String username) {
        Set<String> users = likesMap.computeIfAbsent(imageId, k -> new HashSet<>());
        if (!users.contains(username)) {
            users.add(username);
            saveLikes();
        }
    }

    public void removeLike(String imageId, String username) {
        if (likesMap.containsKey(imageId)) {
            likesMap.get(imageId).remove(username);
            saveLikes();
        }
    }

    public boolean hasLiked(String imageId, String username) {
        Set<String> users = likesMap.getOrDefault(imageId, Collections.emptySet());
        return users.contains(username);
    }

    public int getLikesCount(String imageId) {
        return likesMap.getOrDefault(imageId, Collections.emptySet()).size();
    }

    private void saveLikes() {
        try {
            fileHandler.saveLikes(likesMap);
        } catch (IOException e) {
            e.printStackTrace(); // Consider appropriate error handling
        }
    }

}
