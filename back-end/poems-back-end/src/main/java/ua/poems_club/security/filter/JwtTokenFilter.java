package ua.poems_club.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import ua.poems_club.security.exception.JwtAuthenticationException;
import ua.poems_club.security.model.JwtTokenProvider;

import java.io.IOException;

@Component
public class JwtTokenFilter extends GenericFilterBean {
    private final JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String token = jwtTokenProvider.resolveToken((HttpServletRequest)servletRequest);
        try {
            if (isValid(token))
                setAuthenticationToSecurityContext(token);
        }catch (JwtAuthenticationException e){
            clearSecurityContext();
            sendError(servletResponse,e);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private void setAuthenticationToSecurityContext(String token){
        var authentication = jwtTokenProvider.getAuthentication(token);
        if (authentication != null) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    private boolean isValid(String token) {
        return token != null && jwtTokenProvider.validateToken(token);
    }

    private void clearSecurityContext(){
        SecurityContextHolder.clearContext();
    }

    private void sendError(ServletResponse servletResponse, JwtAuthenticationException e) throws IOException {
        ((HttpServletResponse)servletResponse).sendError(e.getStatus().value());
        throw new JwtAuthenticationException("JWT token is expired or invalid");
    }
}
