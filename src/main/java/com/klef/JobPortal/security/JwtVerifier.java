package com.klef.JobPortal.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

import java.io.InputStream;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Map;

public class JwtVerifier {
    private static  String jwksUrl = "https://cognito-idp.ap-south-1.amazonaws.com/ap-south-1_8Ihq3vqZU/.well-known/jwks.json";

    private static PublicKey getPublicKeyFromJwks(String kid) throws Exception {
        // Fetch JWKS JSON
        InputStream inputStream = new URL(jwksUrl).openStream();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> jwks = mapper.readValue(inputStream, Map.class);

        // Find the key matching the 'kid' in the token
        for (Map<String, Object> key : (Iterable<Map<String, Object>>) jwks.get("keys")) {
            if (key.get("kid").equals(kid)) {
                String modulus = (String) key.get("n");
                String exponent = (String) key.get("e");

                // Convert modulus and exponent to a PublicKey
                byte[] nBytes = Base64.getUrlDecoder().decode(modulus);
                byte[] eBytes = Base64.getUrlDecoder().decode(exponent);
                java.security.spec.RSAPublicKeySpec spec = new java.security.spec.RSAPublicKeySpec(
                        new java.math.BigInteger(1, nBytes),
                        new java.math.BigInteger(1, eBytes)
                );
                KeyFactory factory = KeyFactory.getInstance("RSA");
                return factory.generatePublic(spec);
            }
        }
        throw new Exception("Key not found in JWKS for kid: " + kid);
    }

    // Verify the token and extract claims
    public static Claims verifyJwt(String token) throws Exception {
        // Parse the token header to extract 'kid'
        String[] parts = token.split("\\.");
        String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]));
        Map<String, String> header = new ObjectMapper().readValue(headerJson, Map.class);
        String kid = header.get("kid");

        // Get the public key from JWKS
        PublicKey publicKey = getPublicKeyFromJwks(kid);

        // Verify and parse the token
        Jws<Claims> jws = Jwts.parser()
                .setSigningKey(publicKey)
                .parseClaimsJws(token);

        return jws.getBody();
    }

    public static void main(String[] args) {
        try {
            // Your Cognito JWKS URL
            // String jwksUrl = "https://cognito-idp.ap-south-1.amazonaws.com/ap-south-1_8Ihq3vqZU/.well-known/jwks.json";

            // Example token (replace with your actual token)
            String token = "eyJraWQiOiJmMWhJMFc2dmxxQzZxZGROeGl5QWRlY1ZEcVd6R1laWVNFblUwc09EVFN3PSIsImFsZyI6IlJTMjU2In0.eyJhdF9oYXNoIjoiYUQtY3VsSTZpTDJjV2pDV2xJTTNXdyIsInN1YiI6IjcxZTNhZDJhLTcwMjEtNzBhMy1kYTQxLTk5ZTQ2ZDM0ZTA0NCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJpc3MiOiJodHRwczpcL1wvY29nbml0by1pZHAuYXAtc291dGgtMS5hbWF6b25hd3MuY29tXC9hcC1zb3V0aC0xXzhJaHEzdnFaVSIsImNvZ25pdG86dXNlcm5hbWUiOiJzaGFzaGFuayIsImF1ZCI6IjV2Z2h2cDlvaGE3bTZra2drdmhqYzMwZjR0IiwiZXZlbnRfaWQiOiJmMTAyMzUwYS04ZTE4LTQ5OTktOWVlMy04N2FlMDllOTkwNGUiLCJ0b2tlbl91c2UiOiJpZCIsImF1dGhfdGltZSI6MTczMjAyNDc5MywiZXhwIjoxNzMyMDI4MzkzLCJpYXQiOjE3MzIwMjQ3OTMsImp0aSI6IjM2MDdlOWVjLTFmNDEtNDVkZC04YzU4LWM4NGZmMTZkZmMzMiIsImVtYWlsIjoiMjIwMDA5MDA1M0BrbHVuaXZlcnNpdHkuaW4ifQ.BPKmHiByr1qxaUOcT89_b3-QrI3Oy5W_nitas6EtYYrEaDNLdoT7iwJFaYOI7V2vOVYP2WkLoOyNUc9-C5aY_M75JWINfzdpMoOmhXhBVByzd7lu08gyXcv8UzY2TV_-brcEuwP9T6FjyMjLH6aKJo9ZJrtJa0AtrX6G9qMQrNCbHhbKxOxbS1fAPOa4EnWGTUCtxwcdqhKY-fVpPp0Rqjq1diw1LnxKA90AmUmlK8vJ2zEEgBj2aNqSqyiMjaA5KgRCnMjX2h4nk20nLYtrVaPnyLbuWBB1-sB8NhyVCGTzd2C0ZzLOQWgGa91hidM8teD6n84S98gcyNl7RVt2WQ";

            // Verify and decode the token
            Claims claims = verifyJwt(token);

            // Extract specific claims
            String username = claims.get("cognito:username", String.class);
            String email = claims.get("email", String.class);

            System.out.println("Username: " + username);
            System.out.println("Email: " + email);

        } catch (Exception e) {
            System.err.println("Error verifying token: " + e.getMessage());
        }
    }
}
