package com.schneiderelectric.dces.semtech.security;

import com.schneiderelectric.dces.semtech.security.errorhandler.SimpleAccessDeniedHandler;
import com.schneiderelectric.dces.semtech.security.errorhandler.SimpleAuthenticationEntryPoint;
import com.schneiderelectric.dces.semtech.security.filter.CustomOpaqueTokenIntrospector;
import com.schneiderelectric.dces.semtech.security.ums.UmAuthenticationService;
import com.schneiderelectric.dces.semtech.security.ums.UmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

@Import(UmsSecurityConfig.class)
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "ums.application", name = "tokenUrl")
@EnableConfigurationProperties(UmsSettingProperties.class)
@Slf4j
public class UmsConfiguration {

    @Bean
    OkHttpClient okHttpClient() {

        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };

        OkHttpClient.Builder newBuilder = new OkHttpClient.Builder();
        try {
            SSLContext sslContext  = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            newBuilder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0]);
            newBuilder.hostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
            log.error("Exception occurred while configuring Http client to invoke UMS url");
        }

        return newBuilder.build();
    }


    @Bean
    SimpleAuthenticationEntryPoint simpleAuthenticationEntryPoint() {
        return new SimpleAuthenticationEntryPoint();
    }

    @Bean
    SimpleAccessDeniedHandler simpleAccessDeniedHandler() {
        return new SimpleAccessDeniedHandler();
    }

    @Bean
    UmsService umsService(UmsSettingProperties umsSettingProperties, OkHttpClient okHttpClient) {
        return new UmsService(okHttpClient,umsSettingProperties);
    }


    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    UmAuthenticationService umAuthenticationService(UmsService umsService) {
        return new UmAuthenticationService(umsService);
    }
    @Bean
    CustomOpaqueTokenIntrospector customOpaqueTokenIntrospector(UmAuthenticationService umAuthenticationService) {
        return new CustomOpaqueTokenIntrospector(umAuthenticationService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

}
