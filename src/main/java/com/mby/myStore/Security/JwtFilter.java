package com.mby.myStore.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException; // IMPORTANTE: java.io
import java.util.ArrayList;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    /**
     * Filtro para validar tokens JWT en cada petición entrante.
     * Se ejecuta una vez por cada solicitud HTTP al servidor.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Extraemos el encabezado 'Authorization' de la petición HTTP
        String authHeader = request.getHeader("Authorization");

        // Comprobamos si el encabezado existe y sigue el esquema 'Bearer Token'
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Extraemos el String del token (quitando los primeros 7 caracteres: "Bearer ")
            String token = authHeader.substring(7);
            // Utilizamos el servicio JWT para decodificar el token y obtener el nombre de usuario (subject)
            String username = jwtService.extractUsername(token);

            // Si el usuario es válido y NO existe ya una autenticación en el contexto de Spring
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Creamos un objeto de autenticación con el nombre de usuario y una lista vacía de roles/permisos
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());

                // Adjuntamos detalles de la petición (como IP o ID de sesión) al objeto de autenticación
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Establecemos la autenticación  de seguridad de Spring.
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}