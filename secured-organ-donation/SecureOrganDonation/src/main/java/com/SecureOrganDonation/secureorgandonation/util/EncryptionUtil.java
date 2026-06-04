package com.SecureOrganDonation.secureorgandonation.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;


/**
 * AES helper. Made tolerant: decrypt(...) will NOT throw on bad input,
 * it will return the original value or null if input null/empty.
 *
 * IMPORTANT: replace the KEY with a securely generated key in production
 * and do not hardcode it in source code.
 */
public class EncryptionUtil {

    private static final String ALGO = "AES";
    // 16 bytes key (for AES-128). Replace with secure key stored in env / vault.
    private static final String KEY = "abcdefghijklmnop";

    public static String encrypt(String data) {
        if (data == null) return null;
        try {
            Key key = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), ALGO);
            Cipher c = Cipher.getInstance(ALGO);
            c.init(Cipher.ENCRYPT_MODE, key);
            byte[] enc = c.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(enc);
        } catch (Exception e) {
            // Do not leak internal exception to caller - wrap as runtime if needed.
            throw new RuntimeException("Error encrypting data", e);
        }
    }

    /**
     * Decrypt but be tolerant:
     * - if input is null/empty -> return null
     * - if decryption fails (bad format / not base64 / wrong key) -> return the original string
     * This prevents the entire endpoint from failing because a single DB value is unexpected.
     */
    public static String decrypt(String encryptedData) {
        if (encryptedData == null) return null;
        if (encryptedData.isEmpty()) return "";

        try {
            Key key = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), ALGO);
            Cipher c = Cipher.getInstance(ALGO);
            c.init(Cipher.DECRYPT_MODE, key);

            // If the value is not valid Base64 this will throw -> caught below
            byte[] decodedValue = Base64.getDecoder().decode(encryptedData);
            byte[] dec = c.doFinal(decodedValue);
            return new String(dec, StandardCharsets.UTF_8);
        } catch (Exception e) {
            // Tolerate invalid/unencrypted values by returning the original string.
            // This prevents "Error decrypting data" crashes when DB contains legacy/plain values.
            return encryptedData;
        }
    }
}