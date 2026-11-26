package pl.biblioteka.to;

import java.io.Serializable;

public class BookTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id = 0L;
    private String title = "";
    private String author = "";
    private int year = 2024;
    private boolean available = true;
    private boolean edited = false;

    public BookTO() {
    }

    public BookTO(Long id, String title, String author, int year, boolean available, boolean edited) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.available = available;
        this.edited = edited;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public boolean isEdited() { return edited; }
    public void setEdited(boolean edited) { this.edited = edited; }
}