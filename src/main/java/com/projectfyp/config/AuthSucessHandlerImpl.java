package com.projectfyp.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

@Service
public class AuthSucessHandlerImpl implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        Set<String> role = AuthorityUtils.authorityListToSet(authorities);

        if (role.contains("ROLE_ADMIN"))
        {
            response.sendRedirect("/admin/");
        }
        else if(role.contains("ROLE_USER"))
        {
                response.sendRedirect("/home");
        }
        else
        {
            response.sendRedirect("/home");
        }
    }
}
