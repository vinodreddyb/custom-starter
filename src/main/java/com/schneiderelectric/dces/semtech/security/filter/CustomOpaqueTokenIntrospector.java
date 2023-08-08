package com.schneiderelectric.dces.semtech.security.filter;

import com.schneiderelectric.dces.semtech.security.model.UserInfoDTO;
import com.schneiderelectric.dces.semtech.security.ums.UmAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


@RequiredArgsConstructor
public class CustomOpaqueTokenIntrospector implements OpaqueTokenIntrospector {



    private final UmAuthenticationService umAuthenticationService;


    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        UserInfoDTO userInfoDTO = umAuthenticationService.introspect(token);

        Collection<GrantedAuthority> authorities = userInfoDTO
                .getAuthorities()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("USER_INFO_DTO", userInfoDTO);

        return new OAuth2IntrospectionAuthenticatedPrincipal(attributes, authorities);
    }
}