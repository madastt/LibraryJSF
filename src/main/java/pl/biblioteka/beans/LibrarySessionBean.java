package pl.biblioteka.beans;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import pl.biblioteka.to.BookTO;

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

    private List<BookTO> collectionBooks = new ArrayList<>();
    private long nextCollectionId = 4L;
    @PostConstruct
    public void init() {
        refreshCollectionData();
    }
    public List<BookTO> getCollectionBooks() {
        return collectionBooks;
    }
    public void setCollectionBooks(List<BookTO> collectionBooks) {
        this.collectionBooks = collectionBooks;
    }
    public String addCollectionRow(BookTO afterBook) {
        int idx = collectionBooks.indexOf(afterBook);
        BookTO newBook = new BookTO(nextCollectionId++, "", "", 2026, true, true);
        if (idx >= 0) {
            collectionBooks.add(idx + 1, newBook);
        } else {
            collectionBooks.add(newBook);
        }
        return "";
    }
    public String addCollectionRowAtEnd() {
        collectionBooks.add(new BookTO(nextCollectionId++, "", "", 2026, true, true));
        return "";
    }
    public String deleteCollectionRow(BookTO book) {
        collectionBooks.remove(book);
        return "";
    }
    public String modifyCollectionRow(BookTO book) {
        book.setEdited(true);
        return "";
    }
    public String saveCollectionData() {
        for (BookTO b : collectionBooks) {
            b.setEdited(false);
        }
        applicationBean.incrementTotalActions();
        return "";
    }
    public String refreshCollectionData() {
        collectionBooks.clear();
        collectionBooks.add(new BookTO(1L, "Diuna", "Frank Herbert", 1965, true, false));
        collectionBooks.add(new BookTO(2L, "Hobbit", "J.R.R. Tolkien", 1937, true, false));
        collectionBooks.add(new BookTO(3L, "Rok 1984", "George Orwell", 1949, false, false));
        nextCollectionId = 4L;
        return "";
    }
}