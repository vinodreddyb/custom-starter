package com.schneiderelectric.dces.semtech.security;


import com.schneiderelectric.dces.semtech.security.errorhandler.SimpleAccessDeniedHandler;
import com.schneiderelectric.dces.semtech.security.errorhandler.SimpleAuthenticationEntryPoint;
import com.schneiderelectric.dces.semtech.security.filter.CustomOpaqueTokenIntrospector;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class UmsSecurityConfig {

    private final SimpleAccessDeniedHandler simpleAccessDeniedHandler;
    private final SimpleAuthenticationEntryPoint simpleAuthenticationEntryPoint;
    private final CustomOpaqueTokenIntrospector customOpaqueTokenIntrospector;
    private final UmsSettingProperties umsSettingProperties;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(c-> c.configurationSource(corsConfigurationSource()))
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(umsSettingProperties.getAllowedUrls()).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .accessDeniedHandler(simpleAccessDeniedHandler)
                        .authenticationEntryPoint(simpleAuthenticationEntryPoint)
                        .opaqueToken(opaqueToken -> opaqueToken
                                .introspector(customOpaqueTokenIntrospector)
                        )
                );


        return http.build();
    }

    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("POST","GET","PUT","OPTIONS","DELETE","PATCH"));
        configuration.setAllowedHeaders(List.of("Access-Control-Allow-Origin","Content-Type","X-Requested-With"
                ,"accept","Origin","Access-Control-Request-Method","Access-Control-Request-Headers", "Authorization","token"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


}
