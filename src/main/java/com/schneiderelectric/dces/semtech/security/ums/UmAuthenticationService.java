package com.schneiderelectric.dces.semtech.security.ums;

import com.schneiderelectric.dces.semtech.security.model.UserInfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionException;

@RequiredArgsConstructor
public class UmAuthenticationService  {

    private final UmsService umsService;


    public UserInfoDTO introspect(String token) {

        var user = umsService.getResponseFromUms(token);

        try {
            UserInfoDTO userInfoDTO = new UserInfoDTO();
            userInfoDTO.setAuthorities(umsService.parseAndGetAuth(user));
            userInfoDTO.setEmail(user.getEmail());
            userInfoDTO.setFirstName(user.getFirstName());
            userInfoDTO.setLastName(userInfoDTO.getLastName());
            return userInfoDTO;
        } catch (Exception e) {
            throw new OAuth2IntrospectionException("Unauthorized");
        }
    }
}