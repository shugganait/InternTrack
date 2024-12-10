package by.shug.interntrack.repository.model;

public class Grade {
    private String companyName;
    private String position;
    private String gradeValue;

    // Конструктор
    public Grade(String companyName, String position, String gradeValue) {
        this.companyName = companyName;
        this.position = position;
        this.gradeValue = gradeValue;
    }

    // Геттеры
    public String getCompanyName() {
        return companyName;
    }

    public String getPosition() {
        return position;
    }

    public String getGradeValue() {
        return gradeValue;
    }
}
