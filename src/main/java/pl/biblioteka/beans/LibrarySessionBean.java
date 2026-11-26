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

import pl.biblioteka.dao.BookDAO;
import pl.biblioteka.to.BookTO;
import java.util.Collections;
import java.util.Comparator;


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

    private List<BookTO> dbBooks = new ArrayList<>();
    private BookTO newDbBook = new BookTO();

    @PostConstruct
    public void init() {
        refreshCollectionData();
        refreshAjaxData();
        refreshDbBooks();
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
    private List<BookTO> ajaxBooks = new ArrayList<>();
    private long nextAjaxId = 5L;



    public List<BookTO> getAjaxBooks() {
        return ajaxBooks;
    }

    public void setAjaxBooks(List<BookTO> ajaxBooks) {
        this.ajaxBooks = ajaxBooks;
    }

    public void refreshAjaxData() {
        ajaxBooks.clear();
        ajaxBooks.add(new BookTO(1L, "Władca Pierścieni", "J.R.R. Tolkien", 1954, true, false));
        ajaxBooks.add(new BookTO(2L, "Mistrz i Małgorzata", "Michaił Bułhakow", 1967, true, false));
        ajaxBooks.add(new BookTO(3L, "Zbrodnia i kara", "Fiodor Dostojewski", 1866, false, false));
        ajaxBooks.add(new BookTO(4L, "Mały Książę", "Antoine de Saint-Exupéry", 1943, true, false));
        nextAjaxId = 5L;
    }

    public void ajaxToggleEdit(BookTO book) {
        int idx = ajaxBooks.indexOf(book);
        if (idx >= 0) {
            ajaxBooks.set(idx, book);
            applicationBean.incrementTotalActions();
        }
    }

    public void ajaxAddRow(BookTO afterBook) {
        int idx = ajaxBooks.indexOf(afterBook);
        BookTO newBook = new BookTO(nextAjaxId++, "", "", 2026, true, true);
        if (idx >= 0) {
            ajaxBooks.add(idx + 1, newBook);
        } else {
            ajaxBooks.add(newBook);
        }
        applicationBean.incrementTotalActions();
    }

    public void ajaxAddRowAtEnd() {
        ajaxBooks.add(new BookTO(nextAjaxId++, "", "", 2026, true, true));
        applicationBean.incrementTotalActions();
    }

    public void ajaxDeleteRow(BookTO book) {
        ajaxBooks.remove(book);
        applicationBean.incrementTotalActions();
    }

    public void ajaxSaveAll() {
        for (BookTO b : ajaxBooks) {
            b.setEdited(false);
        }
        applicationBean.incrementTotalActions();
    }

    public void ajaxSortByTitle(String dir) {
        Comparator<BookTO> comparator = (a, b) -> {
            String t1 = a.getTitle() == null ? "" : a.getTitle();
            String t2 = b.getTitle() == null ? "" : b.getTitle();
            return "asc".equals(dir) ? t1.compareToIgnoreCase(t2) : t2.compareToIgnoreCase(t1);
        };
        Collections.sort(ajaxBooks, comparator);
        applicationBean.incrementTotalActions();
    }

    public List<BookTO> getDbBooks() {
        return dbBooks;
    }
    public void setDbBooks(List<BookTO> dbBooks) {
        this.dbBooks = dbBooks;
    }
    public BookTO getNewDbBook() {
        return newDbBook;
    }
    public void setNewDbBook(BookTO newDbBook) {
        this.newDbBook = newDbBook;
    }
    public void refreshDbBooks() {
        dbBooks = new BookDAO().findAll();
    }
    public void dbAddBook() {
        if (newDbBook.getTitle() != null && !newDbBook.getTitle().trim().isEmpty()) {
            new BookDAO().create(newDbBook);
            newDbBook = new BookTO();
            refreshDbBooks();
            applicationBean.incrementTotalActions();
        }
    }
    public void dbToggleEdit(BookTO book) {
        // Gdy użytkownik odznacza checkbox edycji -> automatyczny zapis (UPDATE) do MSSQL
        if (!book.isEdited()) {
            new BookDAO().update(book);
            refreshDbBooks();
            applicationBean.incrementTotalActions();
        }
    }
    public void dbSaveAll() {
        BookDAO dao = new BookDAO();
        for (BookTO book : dbBooks) {
            if (book.isEdited()) {
                dao.update(book);
                book.setEdited(false);
            }
        }
        refreshDbBooks();
        applicationBean.incrementTotalActions();
    }
    public void dbDeleteBook(BookTO book) {
        new BookDAO().delete(book.getId());
        refreshDbBooks();
        applicationBean.incrementTotalActions();
    }
}