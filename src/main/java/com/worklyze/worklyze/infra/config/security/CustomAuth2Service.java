package com.worklyze.worklyze.infra.config.security;

import com.worklyze.worklyze.domain.entity.TypeProvider;
import com.worklyze.worklyze.domain.entity.User;
import com.worklyze.worklyze.domain.enums.TypeProviderEnum;
import com.worklyze.worklyze.domain.interfaces.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomAuth2Service extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    public CustomAuth2Service(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauthUser = super.loadUser(userRequest);

        String email = oauthUser.getAttribute("email");

        User existing = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(oauthUser.getAttribute("name"));

            TypeProvider typeProvider = new TypeProvider();
            typeProvider.setId(TypeProviderEnum.GOOGLE.getValue());

            newUser.setTypeProvider(typeProvider);
            return userRepository.create(newUser);
        });


        return new CustomUserPrincipal(
                existing
        );
    }

}
