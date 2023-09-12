package com.gfa.otocyonknowledgebase.security.services;

import com.gfa.otocyonknowledgebase.security.models.RefreshToken;
import java.util.Map;
import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public interface RefreshTokenService {
  RefreshToken createRefreshToken(String username);

  Optional<RefreshToken> findByToken(String token);

  String generateRefreshToken(UserDetails userDetails);

  String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails);


  void saveRefreshToken(String refreshToken);

  void deleteRefreshToken(Long userId);

  String getTokenFromDb(Long userId);

}
