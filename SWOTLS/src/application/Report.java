package application;

public class Report {
    private String title;
    private String report;

    public Report(String title, String report) {
        this.title = title;
        this.report = report;
    }

    public String getTitle() {
        return title;
    }

    public String getReport() {
        return report;
    }
}
