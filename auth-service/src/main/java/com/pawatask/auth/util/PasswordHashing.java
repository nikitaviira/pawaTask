package com.pawatask.auth.util;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

/**
 * Java – Create a Secure Password Hash
 * Source: <a href="https://howtodoinjava.com/java/java-security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/">link</a>
 */
@Component
public class PasswordHashing {
  private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";

  private static final int SALT_BYTE_SIZE = 24;
  private static final int HASH_BYTE_SIZE = 24;
  private static final int PBKDF2_ITERATIONS = 1000;

  private static final int ITERATION_INDEX = 0;
  private static final int SALT_INDEX = 1;
  private static final int PBKDF2_INDEX = 2;

  @SneakyThrows
  public String createHash(String password) {
    SecureRandom random = new SecureRandom();
    byte[] salt = new byte[SALT_BYTE_SIZE];
    random.nextBytes(salt);

    byte[] hash;
    hash = pbkdf2(password.toCharArray(), salt, PBKDF2_ITERATIONS, HASH_BYTE_SIZE);
    return PBKDF2_ITERATIONS + ":" + toHex(salt) + ":" + toHex(hash);
  }

  @SneakyThrows
  public boolean validatePassword(String password, String correctHash) {
    String[] params = correctHash.split(":");
    int iterations = Integer.parseInt(params[ITERATION_INDEX]);
    byte[] salt = fromHex(params[SALT_INDEX]);
    byte[] hash = fromHex(params[PBKDF2_INDEX]);
    byte[] testHash;
    testHash = pbkdf2(password.toCharArray(), salt, iterations, hash.length);
    return slowEquals(hash, testHash);
  }

  private static boolean slowEquals(byte[] a, byte[] b) {
    int diff = a.length ^ b.length;
    for (int i = 0; i < a.length && i < b.length; i++) diff |= a[i] ^ b[i];
    return diff == 0;
  }

  private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int bytes)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, bytes * 8);
    SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
    return skf.generateSecret(spec).getEncoded();
  }

  private static byte[] fromHex(String hex) {
    byte[] binary = new byte[hex.length() / 2];
    for (int i = 0; i < binary.length; i++) {
      binary[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
    }
    return binary;
  }

  private static String toHex(byte[] array) {
    BigInteger bi = new BigInteger(1, array);
    String hex = bi.toString(16);
    int paddingLength = (array.length * 2) - hex.length();
    if (paddingLength > 0) {
      return String.format("%0" + paddingLength + "d", 0) + hex;
    } else {
      return hex;
    }
  }
}