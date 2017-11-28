package ca.uoit.csci4100u.workplace_app.inc;

/**
 * A simple chat class to store chat related data
 */
public class Chat {

    private String chatId;
    private String chatName;
    private int chatPermission;

    /**
     * Constructor to easily create a chat
     * @param chatName The name of the chat
     * @param chatPermission The permissions of the chat
     */
    public Chat(String chatId, String chatName, int chatPermission) {
        setChatId(chatId);
        setChatName(chatName);
        setChatPermission(chatPermission);
    }

    /**
     * Getter for the chat id
     * @return The chat id
     */
    public String getChatId() {
        return chatId;
    }

    /**
     * Setter for the chat id
     * @param chatId The chat id
     */
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    /**
     * Getter for the chat permission
     * @return The permission level of the chat
     */
    public int getChatPermission() {
        return chatPermission;
    }

    /**
     * Setter for the chat permission
     * @param chatPermission The permission level of the chat
     */
    public void setChatPermission(int chatPermission) {
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
