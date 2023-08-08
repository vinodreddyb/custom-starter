package com.schneiderelectric.dces.semtech.security.ums;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.schneiderelectric.dces.semtech.security.UmsSettingProperties;
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

@RequiredArgsConstructor
@Slf4j
public class UmsService {
    private final OkHttpClient okHttpClient;
    private final UmsSettingProperties umsSettingProperties;

    public List<String> parseAndGetAuth(UMSUserDetails umsUserDetails) {
        var authorities = new ArrayList<String>();
        for(var umsUser : umsUserDetails.getApplications()) {
            for(var group : umsUser.getGroups()) {
                String groupName = null;
                for(var att : group.getAttributes()) {
                    if(att.getType().equals("GROUP")) {
                        groupName = att.getValue();
                        authorities.add(groupName);
                    } else if(groupName != null) {
                        authorities.add(groupName + "|" + att.getValue());
                    }
                }
            }
        }

        return authorities;

    }
    public UMSUserDetails getResponseFromUms(String token) {
        HttpUrl.Builder urlBuilder
                = HttpUrl.parse(umsSettingProperties.getTokenUrl()).newBuilder();
        urlBuilder.addQueryParameter("appId", umsSettingProperties.getAppId());

        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("token",token)
                .addHeader("isPingId","true")
                .addHeader("Accept", "application/json")
                .build();
        Call call = okHttpClient.newCall(request);
        Response response = null;
        UMSUserDetails umsUserDetails = null;
        try {
            response = call.execute();
            if(response.code() == 401){
                log.error("Not a valid token, please use the valid token");
                return umsUserDetails;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(response.body().byteStream()));
            StringBuilder builder = new StringBuilder();
            String input;
            while((input = br.readLine()) != null) {
                builder.append(input);
            }

            br.close();
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            umsUserDetails = mapper.readValue(builder.toString(), UMSUserDetails.class);
        } catch (IOException e) {
            log.error("Unable to create POJO for the UMSUserDetails object");
        }

        return umsUserDetails;
    }
}
