package com.gfa.otocyonknowledgebase.security.auth;

import com.gfa.otocyonknowledgebase.security.config.JwtService;
import com.gfa.otocyonknowledgebase.security.models.JwtResponseDto;
import com.gfa.otocyonknowledgebase.security.models.Role;
import com.gfa.otocyonknowledgebase.security.models.User;
import com.gfa.otocyonknowledgebase.security.repositories.UserRepository;
import com.gfa.otocyonknowledgebase.security.services.RefreshTokenService;
import com.gfa.otocyonknowledgebase.security.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final UserService userService;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final RefreshTokenService refreshTokenService;

  public JwtResponseDto register(RegisterRequest request) {
    var user = User.builder()
        .userName(request.getUserName())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .role(Role.USER)
        .build();
    userRepository.save(user);

    return JwtResponseDto.builder()
        .accessToken(jwtService.generateToken(user))
        .build();
  }

  public JwtResponseDto authenticate(AuthenticationRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getUserName(),
            request.getPassword()
        ));
    var user = userRepository.findByUserName(request.getUserName())
        .orElseThrow();
    JwtResponseDto responseDto = new JwtResponseDto();
    if (userService.existsTokenWithThisUserName(
        user.getUsername())) {
      String toVerify = refreshTokenService.getTokenFromDb(user.getUserId());
      if (jwtService.isTokenValid(toVerify, user)) {
        responseDto.setAccessToken(jwtService.generateToken(user));
        responseDto.setRefreshToken(toVerify);
        return responseDto;
      } else {
        refreshTokenService.deleteRefreshToken(user.getUserId());
        responseDto.setAccessToken(jwtService.generateToken(user));
        responseDto.setRefreshToken(refreshTokenService.generateRefreshToken(user));
        refreshTokenService.saveRefreshToken(responseDto.getRefreshToken());
        return responseDto;
      }
    } else {
      responseDto.setAccessToken(jwtService.generateToken(user));
      responseDto.setRefreshToken(refreshTokenService.generateRefreshToken(user));
      refreshTokenService.saveRefreshToken(responseDto.getRefreshToken());
      return responseDto;
    }
  }
}

