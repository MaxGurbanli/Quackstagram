package Util;
import java.util.*;
import UI.NotificationsUI;

public class ImageLikesManager implements Subject {

    private List<Observer> observers = new ArrayList<>();
    private NotificationsUI notificationsUI;

    public ImageLikesManager() {
        this.notificationsUI = new NotificationsUI();
        registerObserver(notificationsUI);
    }

    public ImageLikesManager(NotificationsUI notificationsUI) {
        if (notificationsUI != null) { // Register NotificationsUI if provided
            registerObserver(notificationsUI);
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
    public void notifyObservers(String imagePath, String username, boolean liked) {
        Picture picture = Picture.getPictureByPath(imagePath);
        User author = picture.getAuthor();
        String notification = liked ? username + " liked " + author.getUsername() + "'s picture: " + imagePath
                : username + " unliked " + author.getUsername() + "'s picture: " + imagePath;
        for (Observer observer : observers) {
            observer.update(notification);
        }
    }
}
