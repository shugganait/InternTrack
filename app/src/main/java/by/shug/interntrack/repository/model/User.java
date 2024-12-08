package by.shug.interntrack.repository.model;

public class User {
    private String fullName;
    private String group;
    private String phoneNumber;
    private String email;
    private String uid;

    public User() {
    }

    public User(String fullName, String group, String phone, String email, String uid) {
        this.fullName = fullName;
        this.group = group;
        this.phoneNumber = phone;
        this.email = email;
        this.uid = uid;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phone) {
        this.phoneNumber = phone;
    }

    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

