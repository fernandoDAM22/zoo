package com.proyectozoo.zoo.components;

import com.proyectozoo.zoo.entity.Usuario;
import com.proyectozoo.zoo.service.IUsuarioService;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JWTUtil {
    @Value("${security.jwt.secret}")
    private String key;

    @Value("${security.jwt.issuer}")
    private String issuer;

    @Value("${security.jwt.ttlMillis}")
    private long ttlMillis;
    /**
     * Servicio del usuario
     */
    @Autowired
    private IUsuarioService usuarioService;
    /**
     * Comoponente que nos permite acceder al fichero de mensajes
     */
    @Autowired
    private MessageComponent message;

    private final Logger log = LoggerFactory.getLogger(JWTUtil.class);

    /**
     * Este metodo permite crear un token de autenticacion
     *
     * @param id      es el id del usuario para el que se va a crear el token
     * @param subject es el email del usuario para el que se va a crear el token
     * @return el token
     */
    public String create(String id, String subject) {
        // The JWT signature algorithm used to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        // Sign JWT with our ApiKey secret
        byte[] apiKeySecretBytes = key.getBytes(StandardCharsets.UTF_8);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        // Set the JWT Claims
        JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build();

        JwtBuilder builder = Jwts.builder()
                .setId(id)
                .setIssuedAt(now)
                .setSubject(subject)
                .setIssuer(issuer)
                .signWith(signingKey, signatureAlgorithm); // Specify the signing key and algorithm

        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        // Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }

    /**
     * Este metodo permite obtener el email del usuario a partir de su token
     *
     * @param jwt es el token del que queremos extrear el usuario
     * @return el email del usuario
     */
    public String getValue(String jwt) {
        // This line will throw an exception if it is not a signed JWS (as expected)
        Claims claims = Jwts.parser().setSigningKey(key.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(jwt).getBody();

        return claims.getSubject();
    }

    /**
     * Este metodo permite obtener el id del usuario a partir de su token
     *
     * @param jwt es el token del que queremos extraer el usuario
     * @return el id del usuario
     */
    public String getKey(String jwt) {
        // This line will throw an exception if it is not a signed JWS (as expected)
        Claims claims = Jwts.parser().setSigningKey(key.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(jwt).getBody();

        return claims.getId();
    }

    /**
     * Este metodo permite comprobar si un token es correcto
     *
     * @param token es el token que queremos validar
     * @return true si es correcto, false si no
     */
    public boolean validarToken(String token) {
        String userID = getKey(token);
        return userID != null;


    }

    /**
     * Este metodo permite validar si un usuario es administrador a partir de su token
     *
     * @param token es el token del usuario que queremos comprobar
     * @return true si el usuario es administrador, false si no
     */
    public boolean validarAdmin(String token) {
        String userID = getKey(token);
        Usuario usuario = usuarioService.buscarPorId(Long.parseLong(userID));
        return usuario != null && usuario.getTipo().equals("ADMIN");
    }
}
