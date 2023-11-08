package com.schneiderelectric.dces.semtech.security.ums;

import com.schneiderelectric.dces.semtech.security.exception.UmsServiceException;
import com.schneiderelectric.dces.semtech.security.model.UMSError;
import com.schneiderelectric.dces.semtech.security.model.UserInfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;

@RequiredArgsConstructor
public class UmAuthenticationService  {

    private final UmsService umsService;


    public UserInfoDTO introspect(String token) {


        var user = umsService.getResponseFromUms(token);
        UMSError umsError = user.getUmsError();
        if(umsError != null && umsError.getStatus() == 500) {
            throw new UmsServiceException(umsError.getDeveloperMessage());
        } else if(umsError != null && umsError.getStatus() == 401) {
            throw new InvalidBearerTokenException(umsError.getDeveloperMessage());
        }
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setAuthorities(umsService.parseAndGetAuth(user));
        userInfoDTO.setEmail(user.getEmail());
        userInfoDTO.setFirstName(user.getFirstName());
        userInfoDTO.setLastName(userInfoDTO.getLastName());
        return userInfoDTO;
    }
}