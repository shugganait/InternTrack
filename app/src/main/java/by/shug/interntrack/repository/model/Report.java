package by.shug.interntrack.repository.model;

public class Report {
    private String date;
    private String content;

    public Report(String date, String content) {
        this.date = date;
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }
}

