package com.shivam.urlshortner.security;

import com.shivam.urlshortner.repository.UserRepository;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import com.shivam.urlshortner.entity.User;
import java.io.IOException;
import java.util.UUID;

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
    ) throws java.io.IOException {

        var oauthUser =
                (org.springframework.security.oauth2.core.user.OAuth2User)
                        authentication.getPrincipal();

        String email =
                oauthUser.getAttribute("email");

        String name =
                oauthUser.getAttribute("name");

        System.out.println("GOOGLE EMAIL: " + email);

        com.shivam.urlshortner.entity.User user =
                userRepository.findByEmail(email)
                        .orElse(null);

        if (user == null) {

            user = new com.shivam.urlshortner.entity.User();

            user.setEmail(email);

            user.setUsername(
                    email.split("@")[0]
            );

            // temporary password
            user.setPassword(
                    java.util.UUID.randomUUID().toString()
            );

            user.setRole("USER");

            userRepository.save(user);

            System.out.println(
                    "NEW GOOGLE USER CREATED: " + email
            );

        } else {

            System.out.println(
                    "EXISTING USER FOUND: " + email
            );
        }

        String token =
                com.shivam.urlshortner.util.JwtUtil.generateToken(
                        user.getUsername(),
                        user.getRole()
                );

        response.sendRedirect(
                "https://snip--ly.vercel.app/?oauthToken="
                        + java.net.URLEncoder.encode(
                        token,
                        java.nio.charset.StandardCharsets.UTF_8
                )
        );
    }
}