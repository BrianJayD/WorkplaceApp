package ca.uoit.csci4100u.workplace_app.inc;

/**
 * A simple message class to store message related data
 */
public class Message {

    private String messageId;
    private String timeStamp;
    private String userId;
    private String userName;
    private String message;

    /**
     * Constructor to easily create a message
     * @param userId The id associated with the user
     * @param userName The userName associated with the user
     * @param timeStamp The time the message was sent
     * @param message The message information
     */
    public Message(String messageId, String userId, String userName, String timeStamp, String message) {
        setMessageId(messageId);
        setUserId(userId);
        setUserName(userName);
        setTimeStamp(timeStamp);
        setMessage(message);
    }

    /**
     * Getter for the message id
     * @return The message id
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Setter for the message id
     * @param messageId The message id
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /**
     * Setter for the user name of the poster of the message
     * @param userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Getter for the user name of the poster of the message
     * @return
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     * Getter for the time stamp of the message
     * @return The timestamp for the message
     */
    public String getTimeStamp() {
        return timeStamp;
    }

    /**
     * Setter for the time stamp of the message
     * @param timeStamp The time the message was sent
     */
    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * Getter for the user's id
     * @return The user's id for the message
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Setter for the user's id
     * @param userId The user's id for the message
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Getter for the message's information
     * @return The message information
     */
    public String getMessage() {
        return message;
    }

    /**
     * Setter for the message's information
     * @param message The message information
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
