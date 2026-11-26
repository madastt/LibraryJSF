package pl.biblioteka.dao;

import pl.biblioteka.to.BookTO;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    private static final String SQL_SELECT_ALL =
            "SELECT id, tytul, autor, rok_wydania, dostepna FROM t_ksiazki ORDER BY id";
    private static final String SQL_INSERT =
            "INSERT INTO t_ksiazki (tytul, autor, rok_wydania, dostepna) VALUES (?, ?, ?, ?)";
    private static final String SQL_UPDATE =
            "UPDATE t_ksiazki SET tytul = ?, autor = ?, rok_wydania = ?, dostepna = ? WHERE id = ?";
    private static final String SQL_DELETE =
            "DELETE FROM t_ksiazki WHERE id = ?";

    private Connection getConnection() throws Exception {
        Context ctx = new InitialContext();
        DataSource dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/bibliotekaData");
        return dataSource.getConnection();
    }

    public List<BookTO> findAll() {
        List<BookTO> books = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                books.add(new BookTO(
                        rs.getLong("id"),
                        rs.getString("tytul"),
                        rs.getString("autor"),
                        rs.getInt("rok_wydania"),
                        rs.getBoolean("dostepna"),
                        false
                ));
            }
        } catch (Exception ex) {
            logAndNotifyError(ex);
        }
        return books;
    }

    public void create(BookTO book) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {

            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setInt(3, book.getYear());
            ps.setBoolean(4, book.isAvailable());
            ps.executeUpdate();
        } catch (Exception ex) {
            logAndNotifyError(ex);
        }
    }

    public void update(BookTO book) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setInt(3, book.getYear());
            ps.setBoolean(4, book.isAvailable());
            ps.setLong(5, book.getId());
            ps.executeUpdate();
        } catch (Exception ex) {
            logAndNotifyError(ex);
        }
    }

    public void delete(Long id) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (Exception ex) {
            logAndNotifyError(ex);
        }
    }

    private void logAndNotifyError(Exception ex) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null) {
            ServletContext servletContext = (ServletContext) facesContext.getExternalContext().getContext();
            servletContext.log(servletContext.getContextPath() + ": " + ex.toString());
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.toString(), null));
        }
    }
}