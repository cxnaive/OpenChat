package cn.handyplus.lib.core;

import cn.handyplus.lib.InitApi;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Optional;
import java.util.logging.Level;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public final class SecureUtil {
   private static final String AES_SECRET_KEY = "HANDY_LIB_SECRET";

   private SecureUtil() {
   }

   public static String md5Str(String str) {
      return md5(str).orElse(null);
   }

   public static Optional<String> md5(String str) {
      try {
         MessageDigest md = MessageDigest.getInstance("MD5");
         byte[] buff = md.digest(str.getBytes());
         return Optional.of(toHex(buff));
      } catch (NoSuchAlgorithmException var3) {
         InitApi.PLUGIN.getLogger().log(Level.SEVERE, "md5 发生异常", (Throwable)var3);
         return Optional.empty();
      }
   }

   private static String toHex(byte[] bytes) {
      StringBuilder md5str = new StringBuilder();

      for (int aByte : bytes) {
         int temp = aByte;
         if (aByte < 0) {
            temp = aByte + 256;
         }

         if (temp < 16) {
            md5str.append("0");
         }

         md5str.append(Integer.toHexString(temp));
      }

      return md5str.toString();
   }

   public static String encrypt(String input) {
      return encrypt("HANDY_LIB_SECRET", input);
   }

   public static String decrypt(String input) {
      return decrypt("HANDY_LIB_SECRET", input);
   }

   public static String encrypt(String aesSecretKey, String input) {
      try {
         SecretKeySpec key = new SecretKeySpec(aesSecretKey.getBytes(), "AES");
         Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
         cipher.init(1, key);
         byte[] encryptedBytes = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
         return Base64.getEncoder().encodeToString(encryptedBytes);
      } catch (Throwable var5) {
         throw new RuntimeException(var5);
      }
   }

   public static String decrypt(String aesSecretKey, String input) {
      try {
         SecretKeySpec key = new SecretKeySpec(aesSecretKey.getBytes(), "AES");
         Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
         cipher.init(2, key);
         byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(input));
         return new String(decryptedBytes, StandardCharsets.UTF_8);
      } catch (Throwable var5) {
         throw new RuntimeException(var5);
      }
   }

   public static String encryptWithPrivateKey(String plainText, String privateKeyStr) {
      try {
         byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr);
         PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
         KeyFactory keyFactory = KeyFactory.getInstance("RSA");
         PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
         Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
         cipher.init(1, privateKey);
         byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
         return Base64.getEncoder().encodeToString(encryptedBytes);
      } catch (Throwable var8) {
         throw new RuntimeException(var8);
      }
   }

   public static String decryptWithPublicKey(String encryptedText, String publicKeyStr) {
      try {
         byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
         X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
         KeyFactory keyFactory = KeyFactory.getInstance("RSA");
         PublicKey publicKey = keyFactory.generatePublic(keySpec);
         Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
         cipher.init(2, publicKey);
         byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
         byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
         return new String(decryptedBytes, StandardCharsets.UTF_8);
      } catch (Throwable var9) {
         throw new RuntimeException(var9);
      }
   }
}
