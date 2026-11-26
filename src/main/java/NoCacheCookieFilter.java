package pl.biblioteka.filters;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

public class NoCacheCookieFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Blokowanie cache w przeglądarce
        httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        httpResponse.setHeader("Pragma", "no-cache");
        httpResponse.setDateHeader("Expires", 0);

        // Wrapper blokujący ustawianie ciasteczek (Set-Cookie / addCookie)
        HttpServletResponseWrapper noCookieResponse = new HttpServletResponseWrapper(httpResponse) {
            @Override
            public void addCookie(Cookie cookie) {
                // Blokada dodawania ciasteczek
            }

            @Override
            public void addHeader(String name, String value) {
                if (!"Set-Cookie".equalsIgnoreCase(name)) {
                    super.addHeader(name, value);
                }
            }

            @Override
            public void setHeader(String name, String value) {
                if (!"Set-Cookie".equalsIgnoreCase(name)) {
                    super.setHeader(name, value);
                }
            }
        };

        chain.doFilter(request, noCookieResponse);
    }

    @Override
    public void destroy() {
    }
}