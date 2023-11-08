package com.schneiderelectric.dces.semtech.security.ums;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.schneiderelectric.dces.semtech.security.UmsSettingProperties;
import com.schneiderelectric.dces.semtech.security.model.UMSError;
import com.schneiderelectric.dces.semtech.security.model.UMSUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.http.HttpStatus;

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
                .addHeader("token","Bearer " + token)
                .addHeader("isPingId","true")
                .addHeader("Accept", "application/json")
                .build();
        Call call = okHttpClient.newCall(request);

        UMSUserDetails umsUserDetails  = new UMSUserDetails();;

        try {
            try (Response response = call.execute();
                 var responseBody = response.body();
                 var bodyStream = responseBody.byteStream()) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                if (response.code() == 401) {
                    log.error("Not a valid token, please use the valid token");
                    var error = mapper.readValue(bodyStream, UMSError.class);
                    umsUserDetails.setUmsError(error);
                    return umsUserDetails;
                }
                umsUserDetails = mapper.readValue(bodyStream, UMSUserDetails.class);
            }

        } catch (Exception e) {
            log.error("Unable to create POJO for the UMSUserDetails object");
            var umsError = new UMSError();
            umsError.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.name());
            umsError.setStatus(500);
            umsError.setResponseStatus("UMS service invocation error " + e.getMessage());
            umsUserDetails.setUmsError(umsError);
        }

        return umsUserDetails;
    }
}
