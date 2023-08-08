package com.schneiderelectric.dces.semtech.security.errorhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.io.PrintWriter;

@RequiredArgsConstructor
public class SimpleAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        final var err = APIResponse.Error.builder().errorCode(String.valueOf(HttpStatus.FORBIDDEN.value())).detailedMessage("Forbidden access of resources").uri(request.getRequestURI()).build();
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setCharacterEncoding("utf-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String resBody = new ObjectMapper().writeValueAsString(err);
        try (PrintWriter printWriter = response.getWriter()) {
            printWriter.print(resBody);
            printWriter.flush();
        }
    }
}
