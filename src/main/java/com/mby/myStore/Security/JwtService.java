package com.mby.myStore.Security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {
    private final String SECRET_KEY = "clave_super_secreta_y_larga_para_la_barberia";

    /**
     * Crea un nuevo Token JWT firmado para un usuario autenticado.
     * @param email Identificador del usuario (habitualmente el email).
     * @return String con el token generado en formato Base64.
     */
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hora
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Proceso inverso: decodifica el token y extrae el nombre del usuario.
     * @param token Cadena JWT recibida en la cabecera HTTP.
     * @return El nombre de usuario (subject) si el token es válido.
     */
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}