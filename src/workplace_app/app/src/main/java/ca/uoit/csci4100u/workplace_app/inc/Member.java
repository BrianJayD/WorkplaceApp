package ca.uoit.csci4100u.workplace_app.inc;

/**
 * Created by brianjayd on 2017-12-04.
 */

public class Member {

    private String memberId;
    private String memberName;
    private String email;

    public Member(String memberId, String memberName, String email) {
        setMemberId(memberId);
        setMemberName(memberName);
        setEmail(email);
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() { return getMemberName(); }

}
