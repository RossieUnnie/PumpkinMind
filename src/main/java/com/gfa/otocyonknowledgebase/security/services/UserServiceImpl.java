package com.gfa.otocyonknowledgebase.security.services;

import com.gfa.otocyonknowledgebase.security.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;

  @Autowired
  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public Long getUserIdByName(String name) {
    return userRepository.findByUserName(name).get().getUserId();
  }

  @Override
  public boolean existsTokenWithThisUerIdInDb(Long userId) {
    if (userRepository.findById(userId).get().getRefreshToken() != null) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean existsTokenWithThisUserName(String userName) {
    if (existsTokenWithThisUerIdInDb(getUserIdByName(userName))) {
      return true;
    }
    return false;
  }

  @Override
  public Long getCurrentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String name = authentication.getName();
    return userRepository.findByUserName(name).get().getUserId();
  }

  @Override
  public String getCurrentUserName() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication.getName();
  }

}
