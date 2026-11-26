package pl.biblioteka.beans;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named(value = "libraryViewBean")
@ViewScoped
public class LibraryViewBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private LibraryApplicationBean applicationBean;

    @Inject
    private LibrarySessionBean sessionBean;

    private int viewClicks = 0;

    public LibraryViewBean() {
    }

    public void recordViewAction() {
        this.viewClicks++;
        applicationBean.incrementTotalActions();
    }

    public String getCombinedSummary() {
        return "Kliknięcia na widoku (View): " + viewClicks
                + " | Język (Session): " + sessionBean.getLocale().toUpperCase()
                + " | Akcje globalne (Application): " + applicationBean.getTotalActions();
    }

    public int getViewClicks() {
        return viewClicks;
    }

    public void setViewClicks(int viewClicks) {
        this.viewClicks = viewClicks;
    }
}