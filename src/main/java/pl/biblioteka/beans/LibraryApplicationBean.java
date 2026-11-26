package pl.biblioteka.beans;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.io.Serializable;

@Named(value = "libraryApplicationBean")
@ApplicationScoped
public class LibraryApplicationBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String libraryName = "Biblioteka Główna TAI";
    private String applicationVersion = "2026.1.0";
    private int totalActions = 0;

    public LibraryApplicationBean() {
    }

    public void incrementTotalActions() {
        this.totalActions++;
    }

    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }

    public String getApplicationVersion() {
        return applicationVersion;
    }

    public void setApplicationVersion(String applicationVersion) {
        this.applicationVersion = applicationVersion;
    }

    public int getTotalActions() {
        return totalActions;
    }

    public void setTotalActions(int totalActions) {
        this.totalActions = totalActions;
    }
}