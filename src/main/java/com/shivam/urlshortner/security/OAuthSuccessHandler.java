package com.shivam.urlshortner.security;

import com.shivam.urlshortner.repository.UserRepository;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class OAuthSuccessHandler
        implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;

    public OAuthSuccessHandler(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(
            jakarta.servlet.http.HttpServletRequest request,
            jakarta.servlet.http.HttpServletResponse response,
            org.springframework.security.core.Authentication authentication
    ) {

        var oauthUser =
                (org.springframework.security.oauth2.core.user.OAuth2User)
                        authentication.getPrincipal();

        String email =
                oauthUser.getAttribute("email");

        System.out.println(
                "GOOGLE EMAIL: " + email
        );
    }
}