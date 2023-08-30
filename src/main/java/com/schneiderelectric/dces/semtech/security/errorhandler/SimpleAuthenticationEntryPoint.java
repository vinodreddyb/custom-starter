package com.schneiderelectric.dces.semtech.security.errorhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.io.PrintWriter;

@RequiredArgsConstructor
public class SimpleAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        final var err = APIResponse.Error.builder().errorCode(String.valueOf(HttpStatus.UNAUTHORIZED.value())).errorMessage("Unauthorized user").uri(request.getRequestURI()).build();
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("utf-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String resBody = new ObjectMapper().writeValueAsString(err);
        try (PrintWriter printWriter = response.getWriter()) {
            printWriter.print(resBody);
            printWriter.flush();
        }
    }
}