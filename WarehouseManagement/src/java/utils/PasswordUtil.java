package utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;


public class PasswordUtil {

    private PasswordUtil() {
    }

    public static String sha256(String rawPassword) {
        if (rawPassword == null) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                String part = Integer.toHexString(0xff & b);
                if (part.length() == 1) {
                    hex.append('0');
                }
                hex.append(part);
            }
            return hex.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Cannot hash password.", ex);
        }
    }
}
