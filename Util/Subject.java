package Util;
public interface Subject {
    void registerObserver(Observer observer);

    void removeObserver(Observer observer);

    void notifyObservers(String imagePath, String username, boolean liked);
}