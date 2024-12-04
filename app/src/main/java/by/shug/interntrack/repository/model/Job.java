package by.shug.interntrack.repository.model;

public class Job {
    private String companyName;
    private String position;
    private String address;
    private String phoneNumber;

    public Job() {}

    public Job(String companyName, String position, String address, String phone) {
        this.companyName = companyName;
        this.position = position;
        this.address = address;
        this.phoneNumber = phone;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phone) {
        this.phoneNumber = phone;
    }
}
