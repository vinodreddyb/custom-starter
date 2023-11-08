package com.schneiderelectric.dces.semtech.security.errorhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.io.PrintWriter;

@RequiredArgsConstructor
public class SimpleAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        if(authException instanceof InvalidBearerTokenException ex) {

            final var err = APIResponse.Error.builder().errorCode(String.valueOf(HttpStatus.UNAUTHORIZED.value())).errorMessage(ex.getMessage()).uri(request.getRequestURI()).build();

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setCharacterEncoding("utf-8");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            writeResponse(response, err);

        } else  {
            final var err = APIResponse.Error.builder().errorCode(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())).errorMessage(authException.getMessage()).uri(request.getRequestURI()).build();

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setCharacterEncoding("utf-8");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            writeResponse(response, err);
        }
    }
    private  void writeResponse(HttpServletResponse response, APIResponse.Error err) throws IOException {
        String resBody = new ObjectMapper().writeValueAsString(err);
        try (PrintWriter printWriter = response.getWriter()) {
            printWriter.print(resBody);
            printWriter.flush();
        }
    }
}