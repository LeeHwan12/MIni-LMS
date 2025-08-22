package org.minilms.minilms.Controller;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalAuthAttributes {
    @ModelAttribute("isLoggedIn")
    public boolean isLoggedIn(HttpServletRequest req) {
        return req.getUserPrincipal() != null;
    }

    @ModelAttribute("username")
    public String username(HttpServletRequest req) {
        return req.getUserPrincipal() != null ? req.getUserPrincipal().getName() : null;
    }
}
