package com.gfa.otocyonknowledgebase.security.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
  @SuppressWarnings("checkstyle:LineLength")
  private static final String SECRET_KEY =
      "wgf4U3xTOXeoebCTRws1Xxy90eJCZlSgNyspPPqotp4rpbxW71PqXdqD4Ee7Nn+PPnk6mM5jSvdKyqR/fdYfBQ/JeMXr/uxOzWhMgNHBAHiYjhIKWFNM+J7IGV7rpaLVtZpErJJNJ+eYnjUklUbPnR7GG87+kb3PglVpchjGmzIZFGDL3FAHkFrm7jL8PZjnyM9O2BCHwyh2EQiytbmZveu2ku3OkDHKHqlktDSCP2X2HAtB7kTj0LkRMg3TNJy6WlzG9vhfS/bB2SeZfbKKeRB7zaEe89R1qYUiHmnbBOwTCdfh4FpVRafgmYxaHWlXt1z37mOloIzPBa/41M4+ZA==\n";

  public String extractUserName(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    return Jwts
        .builder()
        .setClaims(extraClaims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 2))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public String generateToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails);
  }

  private Claims extractAllClaims(String token) {
    try {
      return Jwts
          .parserBuilder()
          .setSigningKey(getSigningKey())
          .build()
          .parseClaimsJws(token)
          .getBody();
    } catch (ExpiredJwtException exception) {
      return Jwts.claims();
    }
  }

  private Key getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    try {
      final String userName = extractUserName(token);
      if (extractUserName(token) != null) {
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
      } else {
        return false;
      }
    } catch (ExpiredJwtException exception) {
      return false;
    }
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }
}
