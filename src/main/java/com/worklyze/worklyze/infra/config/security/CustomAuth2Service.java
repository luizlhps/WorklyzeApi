package com.worklyze.worklyze.infra.config.security;

import com.worklyze.worklyze.domain.entity.TypeProvider;
import com.worklyze.worklyze.domain.entity.User;
import com.worklyze.worklyze.domain.enums.TypeProviderEnum;
import com.worklyze.worklyze.infra.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class CustomAuth2Service extends DefaultOAuth2UserService  {
    private final UserRepository userRepository;

    public CustomAuth2Service(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);

        String email = user.getAttribute("email");
        String username = user.getAttribute("username");

        User existing = userRepository.findByEmailOrUsername(email, username).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(user.getAttribute("name"));

            TypeProvider typeProvider = new TypeProvider();
            typeProvider.setId(TypeProviderEnum.GOOGLE.getValue());

            newUser.setTypeProvider(typeProvider);
            return userRepository.save(newUser);
        });
        return user;
    }

}
