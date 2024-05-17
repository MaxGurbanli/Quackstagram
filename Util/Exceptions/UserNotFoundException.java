package Util.Exceptions;

public class UserNotFoundException extends DatabaseException {
    public UserNotFoundException(int userId) {
        super("User with ID " + userId + " not found.", null);
    }
}