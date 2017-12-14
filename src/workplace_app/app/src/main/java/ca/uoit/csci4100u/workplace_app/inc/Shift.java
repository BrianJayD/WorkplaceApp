package ca.uoit.csci4100u.workplace_app.inc;

/**
 * Created by brianjayd on 2017-11-30.
 */

public class Shift {

    private String name;
    private String memberId;
    private String date; // Date format "MM/DD/YYYY"
    private String time; //

    public Shift(String name, String memberId, String date, String time) {
        this.name = name;
        this.memberId = memberId;
        this.date = date;
        this.time = time;
    }

    public Shift() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
}
