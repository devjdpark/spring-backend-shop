package com.example.backend;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenTest {
  @Test
  void printHash() {
    var enc = new BCryptPasswordEncoder();
    String hash = enc.encode("1234");
    System.out.println("ENC(1234)=" + hash);
  }
}
