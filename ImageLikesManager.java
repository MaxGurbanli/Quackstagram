import java.io.*;
import java.io.IOException;
import java.util.*;


public class ImageLikesManager implements Subject {

    private List<Observer> observers = new ArrayList<>();
    private Map<String, Set<String>> likesMap;
    private LikesFileHandler fileHandler; // Define fileHandler as a field
    private NotificationsUI notificationsUI;
    

    public ImageLikesManager(String filePath) {
        this.fileHandler = new LikesFileHandler(filePath);
        this.likesMap = loadLikes();
    }

    public ImageLikesManager(String filePath, NotificationsUI notificationsUI) {
        this.fileHandler = new LikesFileHandler(filePath);
        this.likesMap = loadLikes();
        if (notificationsUI != null) { // Register NotificationsUI if provided
            registerObserver(notificationsUI);
        }
        this.notificationsUI = notificationsUI;
    }
   


    private Map<String, Set<String>> loadLikes() {
        try {
            likesMap = fileHandler.readLikes();
        } catch (IOException e) {
            e.printStackTrace();
            likesMap = null;
        }
        return likesMap;
    }

    public void addLike(String imageId, String username) {
        Set<String> users = likesMap.computeIfAbsent(imageId, k -> new HashSet<>());
        if (!users.contains(username)) {
            users.add(username);
            notifyObservers(imageId, username, true);
            saveLikes();
            if (notificationsUI != null) {
                notificationsUI.handleLikeEvent(username, imageId);
            } else {
                System.out.println("NotificationsUI is null");
            }
        }
    }
    public void removeLike(String imageId, String username) {
        if (likesMap.containsKey(imageId)) {
            likesMap.get(imageId).remove(username);
            notifyObservers(imageId, username, false); // Notify observers about the unlike
            saveLikes(); // Save likes after removing a like
        }
    }

    private void saveLikes() {
        // Save likes to file
        try {
            fileHandler.writeLikes(likesMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String imageId, String username, boolean liked) {
        String notification = liked ? username + " liked your picture: " + imageId
                                     : username + " unliked your picture: " + imageId;
        for (Observer observer : observers) {
            observer.update(notification);
        }
    }

    public boolean hasLiked(String imageId, String username) {
        Set<String> users = likesMap.getOrDefault(imageId, Collections.emptySet());
        return users.contains(username);
    }

    public int getLikesCount(String imageId) {
        return likesMap.getOrDefault(imageId, Collections.emptySet()).size();
    }
}
