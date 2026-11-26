package pl.biblioteka.beans;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Locale;

@Named(value = "librarySessionBean")
@SessionScoped
public class LibrarySessionBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private LibraryApplicationBean applicationBean;

    private String locale = "pl";
    private String sessionNote = "Sesja aktywna";
    private int navigationChoice = 1;

    public LibrarySessionBean() {
    }

    public String getAppInfoFromInjectedBean() {
        return applicationBean.getLibraryName() + " [v" + applicationBean.getApplicationVersion() + " - DI]";
    }

    public String changeLanguage(String lang) {
        this.locale = lang;
        FacesContext.getCurrentInstance().getViewRoot().setLocale(new Locale(lang));
        return null;
    }

    // Nawigacja dynamiczna przez metodę kontrolera
    public String getDynamicTarget() {
        if (navigationChoice == 2) {
            return "goAjax";
        } else if (navigationChoice == 3) {
            return "goDatabase";
        }
        return "goCollection";
    }

    public String logout() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        externalContext.invalidateSession();
        return "/logoutWindow.xhtml?faces-redirect=true";
    }

    public LibraryApplicationBean getApplicationBean() {
        return applicationBean;
    }

    public void setApplicationBean(LibraryApplicationBean applicationBean) {
        this.applicationBean = applicationBean;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getSessionNote() {
        return sessionNote;
    }

    public void setSessionNote(String sessionNote) {
        this.sessionNote = sessionNote;
    }

    public int getNavigationChoice() {
        return navigationChoice;
    }

    public void setNavigationChoice(int navigationChoice) {
        this.navigationChoice = navigationChoice;
    }
}