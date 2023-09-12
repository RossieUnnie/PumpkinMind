package com.gfa.otocyonknowledgebase.security.services;

public interface UserService {

  Long getUserIdByName(String name);

  boolean existsTokenWithThisUerIdInDb(Long userId);

  boolean existsTokenWithThisUserName(String userName);

  Long getCurrentUserId();

  String getCurrentUserName();
}
