package com.gfa.otocyonknowledgebase.security.auth;

import com.gfa.otocyonknowledgebase.security.config.JwtService;
import com.gfa.otocyonknowledgebase.security.models.JwtResponseDto;
import com.gfa.otocyonknowledgebase.security.models.User;
import com.gfa.otocyonknowledgebase.security.models.dto.RefreshTokenRequest;
import com.gfa.otocyonknowledgebase.security.services.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
  @Autowired
  private final AuthService authService;
  @Autowired
  private final RefreshTokenService refreshTokenService;
  @Autowired
  private final JwtService jwtService;

  @PostMapping("/register")
  public ResponseEntity<JwtResponseDto> register(
      @RequestBody RegisterRequest request) {

    return ResponseEntity.ok(authService.register(request));
  }

  @PostMapping("/login")
  public ResponseEntity<JwtResponseDto> login(
      @RequestBody AuthenticationRequest request) {

    return ResponseEntity.ok(authService.authenticate(request));
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest receivedToken) {
    if (jwtService.extractUserName(receivedToken.getToken()) == null) {
      return ResponseEntity.badRequest()
          .body("Invalid refresh token, please log in again!"); // to handle it globally
    }
    User userToValidate = new User();
    userToValidate.setUserName(jwtService.extractUserName(receivedToken.getToken()));
    if (jwtService.isTokenValid(receivedToken.getToken(), userToValidate)
        && refreshTokenService.findByToken(receivedToken.getToken()).get().getToken()
        .equals(receivedToken.getToken())) {
      JwtResponseDto positiveResponse = new JwtResponseDto();
      positiveResponse.setAccessToken(jwtService.generateToken(userToValidate));
      positiveResponse.setRefreshToken(receivedToken.getToken());
      return ResponseEntity.ok().body(positiveResponse);
    } else {
      return ResponseEntity.badRequest()
          .body("Invalid refresh token, please log in again!"); // to handle it globally
    }
  }

  // /login???? nebo je to defaultní (týká se loginpage springu)
}
