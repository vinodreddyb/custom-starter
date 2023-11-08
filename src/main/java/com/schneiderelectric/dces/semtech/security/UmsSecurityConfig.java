package com.schneiderelectric.dces.semtech.security;


import com.schneiderelectric.dces.semtech.security.errorhandler.SimpleAccessDeniedHandler;
import com.schneiderelectric.dces.semtech.security.errorhandler.SimpleAuthenticationEntryPoint;
import com.schneiderelectric.dces.semtech.security.filter.CustomOpaqueTokenIntrospector;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

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
                .cors(Customizer.withDefaults())
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
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

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return webSecurity -> webSecurity.ignoring().requestMatchers(umsSettingProperties.getAllowedUrls());
    }


}
