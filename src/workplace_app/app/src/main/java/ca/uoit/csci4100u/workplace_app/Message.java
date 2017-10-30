package ca.uoit.csci4100u.workplace_app;

import com.google.firebase.database.DataSnapshot;

/**
 * A simple message class to store message related data
 */
public class Message {

    private String timeStamp;
    private String userId;
    private String message;

    /**
     * Empty default constructor for the Firebase API to use
     */
    public Message(){}

    /**
     * Constructor to easily create a message
     * @param userId The id associated with the user
     * @param timeStamp The time the message was sent
     * @param message The message information
     */
    public Message(String userId, String timeStamp, String message) {
        setUserId(userId);
        setTimeStamp(timeStamp);
        setMessage(message);
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

    /**
     * The toString function to easily print the full message information
     * @param dataSnapshot The snapshot of the database for converting the userId to display name
     * @return A string containing all of the message information
     */
    public String toString(DataSnapshot dataSnapshot) {
        String displayName = DbHelper.convertUidToDispName(dataSnapshot, getUserId());
        return getTimeStamp() + ": " + displayName + " - " + getMessage();
    }
}
