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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UsersService usersService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String[] AUTH_WHITELIST = {
            "/api/v1/auth/login",
            "/api/v1/auth/register-customer",
            "/api/v1/auth/register-admin",
            "/api/v1/coordinate",
            "/api/v1/payments/notification",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/webjars/**",
            "/v3/api-docs/**",
            "/v3/api-docs",
            "/v3/api-docs/swagger-config",
    };

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return Arrays.stream(AUTH_WHITELIST)
                .anyMatch(endpoint ->
                        path.startsWith(endpoint) ||
                                path.matches(endpoint.replace("**", ".*"))
                );
    }

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

//            System.out.println(request.getRequestURI());
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new AuthenticationException("Authorization header not found or invalid endpoint url");
            }

            jwtToken = authHeader.substring(7);
            try {
                email = jwtService.extractEmail(jwtToken);
            } catch (ExpiredJwtException e) {
                throw new AuthenticationException("JWT token expired");
            } catch (JwtException e) {
                throw new AuthenticationException("JWT token invalid");
            }

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = usersService.loadUserByUsername(email);
                if (jwtService.validateToken(jwtToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    throw new AuthenticationException("JWT Token expired");
                }
            }
            filterChain.doFilter(request, response);
        } catch (AuthenticationException e){
            handleAuthenticationException(response, e.getMessage());
        }

    }
}
