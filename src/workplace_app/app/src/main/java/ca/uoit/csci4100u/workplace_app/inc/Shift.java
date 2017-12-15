package ca.uoit.csci4100u.workplace_app.inc;

/**
 * Created by brianjayd on 2017-11-30.
 */

public class Shift {

    private String shiftId;
    private String name;
    private String memberId;
    private String date; // Date format "MM/DD/YYYY"
    private String time; // Time format 12h clock
    private int vacant;

    public Shift(String name, String memberId, String date, String time, int vacant) {
        this.name = name;
        this.memberId = memberId;
        this.date = date;
        this.time = time;
        this.vacant = vacant;
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

    public int getVacant() {
        return vacant;
    }

    public void setVacant(int vacant) {
        this.vacant = vacant;
    }
}
