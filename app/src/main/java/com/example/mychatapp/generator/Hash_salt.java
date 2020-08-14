package com.example.mychatapp.generator;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash_salt {

    /**
     * Based on Java interview point(https://www.javainterviewpoint.com/java-salted-password-hashing/)
     * Code is derived from Java Salted Password Hashing algorithm
     * @since 2020/06/19
     */
    //add salt into a hashed message.
    public static String Hash_addSalt(String hash, String salt){
        String generateHash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] s = salt.getBytes();
            digest.update(s);
            byte[] bytes = digest.digest(hash.getBytes(StandardCharsets.UTF_8));
            StringBuilder string = new StringBuilder();
            for (int i = 0; i < bytes.length; i++){
                string.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generateHash = string.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generateHash;
    }
}
