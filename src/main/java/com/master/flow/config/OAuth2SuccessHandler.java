package com.master.flow.config;

import com.master.flow.model.dao.UserDAO;
import com.master.flow.model.vo.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private UserDAO dao;

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User u = (OAuth2User) authentication.getPrincipal();

        String userEmail = (String) u.getAttributes().get("userEmail");
        String userPlatform = (String) u.getAttributes().get("userPlatform");

        User user = dao.duplicateCheck(userEmail, userPlatform).get();

        String token = tokenProvider.create(user);

        response.sendRedirect("http://localhost:3000/loginSuccess?token=" + token);
    }
}
