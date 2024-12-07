package com.klef.JobPortal.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtils {

//    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-url}")
    private String jwksUrl; // JWK URL injected from application.yml

    // Method to extract the username (subject) from the JWT token
    public String extractUsername(String token) throws Exception {
        return extractClaim(token, Claims::getSubject);
    }

    // Generic method to extract any claim from the token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws Exception {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extracts all claims from the token using the public key
    private Claims extractAllClaims(String token) throws Exception {
        PublicKey publicKey = getCognitoPublicKey(token);
        Jws<Claims> jwsClaims = Jwts.parser()
                .setSigningKey(publicKey)
                .parseClaimsJws(token);
        return jwsClaims.getBody();
    }

    // Retrieve the public key for AWS Cognito based on the "kid" in the JWT header
    private PublicKey getCognitoPublicKey(String token) throws Exception {
        String kid = extractKidFromToken(token);
        String publicKeyString = fetchPublicKeyString(kid);

        if (publicKeyString == null) {
            throw new RuntimeException("Public key not found for kid: " + kid);
        }

        byte[] decoded = Base64.getDecoder().decode(publicKeyString);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

    // Extract "kid" from the JWT header
    private String extractKidFromToken(String token) throws Exception {
        String[] chunks = token.split("\\.");
        String header = new String(Base64.getDecoder().decode(chunks[0]));
        return new JSONObject(header).getString("kid");
    }

    // Fetch the public key string from JWKS for a given kid
    private String fetchPublicKeyString(String kid) throws Exception {
        JSONObject jwks = fetchJWKS();
        JSONArray keys = jwks.getJSONArray("keys");

        for (int i = 0; i < keys.length(); i++) {
            JSONObject key = keys.getJSONObject(i);
            if (key.getString("kid").equals(kid)) {
                return key.getJSONArray("x5c").getString(0);
            }
        }
        return null;
    }

    // Fetch JWKS (JSON Web Key Set) from AWS Cognito
    private JSONObject fetchJWKS() throws Exception {
        java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
        java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create(jwksUrl))
                .build();

        java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
        return new JSONObject(response.body());
    }

    // Validate the token against the provided username
    public boolean validateToken(String token, String username) {
        try {
            final String extractedUsername = extractUsername(token);
            return (extractedUsername.equals(username) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    // Check if the token has expired
    private boolean isTokenExpired(String token) throws Exception {
        return extractExpiration(token).before(new Date());
    }

    // Extract expiration date from the token
    public Date extractExpiration(String token) throws Exception {
        return extractClaim(token, Claims::getExpiration);
    }
}
