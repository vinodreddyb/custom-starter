package com.schneiderelectric.dces.semtech.security.ums;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.schneiderelectric.dces.semtech.security.UmsSettingProperties;
import com.schneiderelectric.dces.semtech.security.exception.UMSUnAuthorizedException;
import com.schneiderelectric.dces.semtech.security.exception.UmsServiceException;
import com.schneiderelectric.dces.semtech.security.model.UMSUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
public class UmsService {
    private final OkHttpClient okHttpClient;
    private final UmsSettingProperties umsSettingProperties;

    public List<String> parseAndGetAuth(UMSUserDetails umsUserDetails) {
        var authorities = new ArrayList<String>();
        for (var umsUser : umsUserDetails.getApplications()) {
            for (var group : umsUser.getGroups()) {
                String groupName = null;
                for (var att : group.getAttributes()) {
                    if (att.getType().equals("GROUP")) {
                        groupName = att.getValue();
                        authorities.add(groupName);
                    } else if (groupName != null) {
                        authorities.add(groupName + "|" + att.getValue());
                    }
                }
            }
        }

        return authorities;

    }

    public UMSUserDetails getResponseFromUms(String token) {
        HttpUrl.Builder urlBuilder
                = Objects.requireNonNull(HttpUrl.parse(umsSettingProperties.getTokenUrl())).newBuilder();
        urlBuilder.addQueryParameter("appId", umsSettingProperties.getAppId());

        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("token", token)
                .addHeader("isPingId", "true")
                .addHeader("Accept", "application/json")
                .build();

        UMSUserDetails umsUserDetails = null;
        try {
            Call call = okHttpClient.newCall(request);
            try (Response response = call.execute();
                 var responseBody = response.body();
                 var bodyStream = responseBody.byteStream())  {

                if (response.code() == 401) {
                    log.error("Not a valid token, please use the valid token");
                    throw new UMSUnAuthorizedException("Not a valid token, please use the valid token. Response from UMS " + response.code());
                }
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                umsUserDetails = mapper.readValue(bodyStream, UMSUserDetails.class);
            }
        } catch (Exception e) {
            log.error("Exception occurred while validating token from UMS", e);
            throw new UmsServiceException("Exception occurred while validating token from UMS", e);
        }

        return umsUserDetails;
    }
}
