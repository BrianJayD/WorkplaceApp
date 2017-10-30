package ca.uoit.csci4100u.workplace_app;

/**
 * A simple chat class to store chat related data
 */
public class Chat {

    private String chatName;
    private boolean chatPermission;

    /**
     * Constructor to easily create a chat
     * @param chatName The name of the chat
     * @param chatPermission The permissions of the chat
     */
    public Chat(String chatName, boolean chatPermission) {
        setChatName(chatName);
        setChatPermission(chatPermission);
    }

    /**
     * Getter for the chat permission
     * @return The permission level of the chat
     */
    public boolean getChatPermission() {
        return chatPermission;
    }

    /**
     * Setter for the chat permission
     * @param chatPermission The permission level of the chat
     */
    public void setChatPermission(boolean chatPermission) {
        this.chatPermission = chatPermission;
    }

    /**
     * Getter for the chat name
     * @return The chat name as a string
     */
    public String getChatName() {
        return chatName;
    }

    /**
     * Setter for the chat name
     * @param chatName A string representation of the chat name
     */
    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    /**
     * Overridden toString() method to use for the Spinner's ArrayAdapter
     * @return The chat name as a string
     */
    @Override
    public String toString() {
        return getChatName();
    }
}
