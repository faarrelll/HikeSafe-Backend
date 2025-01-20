package com.haven.app.haven.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haven.app.haven.dto.response.ErrorResponse;
import com.haven.app.haven.exception.AuthenticationException;
import com.haven.app.haven.service.JwtService;
import com.haven.app.haven.service.UsersService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UsersService usersService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private void handleAuthenticationException(HttpServletResponse response, String message) throws IOException {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(message)
                .error("credentials error")
                .build();

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");
            String jwtToken = null;
            String email = null;

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                jwtToken = authHeader.substring(7);
                try {
                    email = jwtService.extractEmail(jwtToken);
                } catch (ExpiredJwtException e) {
                    handleAuthenticationException(response, "JWT Token expired");
                    return;
                } catch (JwtException e) {
                    handleAuthenticationException(response, "JWT Token invalid");
                    return;
                }
            }

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = usersService.loadUserByUsername(email);
                if (jwtService.validateToken(jwtToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    throw new AuthenticationException("Jwt Token expired");
                }
            }
            filterChain.doFilter(request, response);
        } catch (AuthenticationException e){
            handleAuthenticationException(response, e.getMessage());
        } catch (Exception e) {
            log.error("Unknown Error: {}", e.getMessage());
            handleAuthenticationException(response,"Authentication Failed");
        }
    }
}
