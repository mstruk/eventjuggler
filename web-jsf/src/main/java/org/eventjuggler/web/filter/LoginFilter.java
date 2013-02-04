package org.eventjuggler.web.filter;

import org.eventjuggler.web.controller.Login;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Matej Lazar
 */
@WebFilter(urlPatterns = {"/*"})
public class LoginFilter implements Filter {

    @Inject
    Login login;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        if (!isAuthRequired(req) || isLoggedIn()) {
            chain.doFilter(request, response);
        } else {
            rememberLastView(req);
            res.sendRedirect(req.getContextPath() + "/login.jsf");
        }
    }

    private void rememberLastView(HttpServletRequest req) {
        login.setRedirectedUri(req);
    }

    private boolean isLoggedIn() {
        return login.isLoggedIn();
    }

    private boolean isAuthRequired(HttpServletRequest req) {
        return login.isAuthRequired(req);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
