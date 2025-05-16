package Main;

public class UserSession {
    private static String currentUsername;
    private static String currentUserRole;
    private static User currentUser;

    public static void setCurrentUser(String username, String role) {
        currentUsername = username;
        currentUserRole = role;
    }

    public static void setCurrentUsername(String username) { 
        currentUsername = username;
    }

    public static String getCurrentUsername() {
        return currentUsername;
    }

    public static String getCurrentUserRole() {
        return currentUserRole;
    }

    public static void clearSession() {
        currentUsername = null;
        currentUserRole = null;
        currentUser = null;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }
   
}
