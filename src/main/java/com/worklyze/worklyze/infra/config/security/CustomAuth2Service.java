package com.worklyze.worklyze.infra.config.security;

import com.worklyze.worklyze.domain.entity.TypeProvider;
import com.worklyze.worklyze.domain.entity.User;
import com.worklyze.worklyze.domain.enums.TypeProviderEnum;
import com.worklyze.worklyze.infra.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomAuth2Service extends DefaultOAuth2UserService  {
    private final UserRepository userRepository;

    public CustomAuth2Service(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauthUser  = super.loadUser(userRequest);

        String email = oauthUser.getAttribute("email");
        String username = oauthUser.getAttribute("username");

        User existing = userRepository.findByEmailOrUsername(email, username).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(oauthUser.getAttribute("name"));

            TypeProvider typeProvider = new TypeProvider();
            typeProvider.setId(TypeProviderEnum.GOOGLE.getValue());

            newUser.setTypeProvider(typeProvider);
            return userRepository.save(newUser);
        });

        return new DefaultOAuth2User(
                Collections.singleton(() -> "ROLE_USER"),
                oauthUser.getAttributes(),
                "email"
        );
    }

}
