package com.gfa.otocyonknowledgebase.security.services;

import com.gfa.otocyonknowledgebase.security.config.JwtService;
import com.gfa.otocyonknowledgebase.security.models.RefreshToken;
import com.gfa.otocyonknowledgebase.security.repositories.RefreshTokenRepository;
import com.gfa.otocyonknowledgebase.security.repositories.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
  @Autowired
  private RefreshTokenRepository refreshTokenRepository;
  @Autowired
  private UserRepository userRepository;
  private JwtService jwtService;
  private UserService userService;
  @SuppressWarnings("checkstyle:LineLength")
  private static final String SECRET_KEY =
      "wgf4U3xTOXeoebCTRws1Xxy90eJCZlSgNyspPPqotp4rpbxW71PqXdqD4Ee7Nn+PPnk6mM5jSvdKyqR/fdYfBQ/JeMXr/uxOzWhMgNHBAHiYjhIKWFNM+J7IGV7rpaLVtZpErJJNJ+eYnjUklUbPnR7GG87+kb3PglVpchjGmzIZFGDL3FAHkFrm7jL8PZjnyM9O2BCHwyh2EQiytbmZveu2ku3OkDHKHqlktDSCP2X2HAtB7kTj0LkRMg3TNJy6WlzG9vhfS/bB2SeZfbKKeRB7zaEe89R1qYUiHmnbBOwTCdfh4FpVRafgmYxaHWlXt1z37mOloIzPBa/41M4+ZA==\n";

  @Override
  public RefreshToken createRefreshToken(String username) {
    RefreshToken refreshToken = RefreshToken.builder()
        .user(userRepository.findByUserName(username).get())
        .token(UUID.randomUUID().toString())
        .expiryDate(Instant.now().plusSeconds(60))
        .build();
    return refreshTokenRepository.save(refreshToken);
  }

  @Override
  public Optional<RefreshToken> findByToken(String token) {
    return refreshTokenRepository.findByToken(token);
  }

  @Override
  public String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    return Jwts
        .builder()
        .setClaims(extraClaims)
        .setSubject(userDetails.getUsername())
        .setId(UUID.randomUUID().toString())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 5))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  @Override
  public String generateRefreshToken(UserDetails userDetails) {
    return generateRefreshToken(new HashMap<>(), userDetails);
  }

  @Override
  public void saveRefreshToken(String refreshToken) {
    RefreshToken toSave = RefreshToken.builder()
        .user(userRepository.findByUserName(jwtService.extractUserName(refreshToken)).get())
        .token(refreshToken)
        .build();

    refreshTokenRepository.save(toSave);
  }

  private Key getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
    return Keys.hmacShaKeyFor(keyBytes);
  }


  @Override
  public void deleteRefreshToken(Long userId) {
    refreshTokenRepository.deleteById(
        userRepository.findById(userId).get().getRefreshToken().getTokenId());
  }

  @Override
  public String getTokenFromDb(Long userId) {
    return userRepository.findById(userId).get().getRefreshToken().getToken();
  }
}
