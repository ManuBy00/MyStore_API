package com.mby.myStore.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority; // IMPORTANTE
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.lang.NonNull;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // 1. Si no hay token o no empieza por Bearer, seguimos la cadena (para rutas públicas)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.substring(7);
            String username = jwtService.extractEmail(token);
            String role = jwtService.extractRole(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Creamos la autoridad con el prefijo ROLE_
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(username, null, List.of(authority));

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

            // Si todo va bien, continuamos
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            handleException(response, "El token ha expirado", e.getMessage(), HttpServletResponse.SC_UNAUTHORIZED);
        } catch (SignatureException | MalformedJwtException e) {
            handleException(response, "Token inválido o firma corrupta", e.getMessage(), HttpServletResponse.SC_UNAUTHORIZED);
        } catch (Exception e) {
            handleException(response, "Error procesando el token", e.getMessage(), HttpServletResponse.SC_FORBIDDEN);
        }
    }

    /**
     * Método auxiliar para enviar una respuesta JSON limpia en caso de error de seguridad
     */
    private void handleException(HttpServletResponse response, String error, String message, int status) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Creamos un JSON manual para no complicar el filtro con librerías extra
        String jsonResponse = String.format(
                "{\"status\": %d, \"error\": \"%s\", \"message\": \"%s\"}",
                status, error, message
        );

        response.getWriter().write(jsonResponse);
    }
}