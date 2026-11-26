package pl.biblioteka.beans;

import java.io.Serializable;

public class AppInfoXmlBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String student = "";
    private String projectTitle = "";
    private String appVersion = "";

    public AppInfoXmlBean() {
    }

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }
}