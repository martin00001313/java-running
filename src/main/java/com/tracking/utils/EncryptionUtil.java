package com.tracking.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

/**
 * An interface responsible to encrypt the provided text/passwords.
 *
 * @author martin
 */
public class EncryptionUtil {

    public static enum EncryptionTypes {

        MD_5("MD5"),//32
        SHA_512("SHA-512"); // 128

        private final String value;

        EncryptionTypes(final String value) {
            this.value = value;
        }
    }

    public static Optional<String> hashKey(String text) {

        return hashPassword(text, EncryptionTypes.SHA_512);
    }

    private static Optional<String> hashPassword(final String text, final EncryptionTypes encryptionType) {

        if (text == null) {
            return Optional.empty();
        }
        final byte[] encryptedBytes;

        try {
            encryptedBytes = encrypt(text, encryptionType);
        } catch (NoSuchAlgorithmException ex) {
            return Optional.empty();
        }

        if (encryptedBytes == null) {
            return Optional.empty();
        }

        return Optional.of(toHexString(encryptedBytes));
    }

    private static byte[] encrypt(final String text, final EncryptionTypes encryptionType) throws NoSuchAlgorithmException {

        final MessageDigest md = MessageDigest.getInstance(encryptionType.value);
        md.update(text.getBytes());
        return md.digest();
    }

    private static String toHexString(byte[] bytes) {

        final StringBuilder hex = new StringBuilder(bytes.length * 2);

        for (int i = 0; i < bytes.length; ++i) {
            hex.append(Character.forDigit((bytes[i] & 0XF0) >> 4, 16));
            hex.append(Character.forDigit((bytes[i] & 0X0F), 16));
        }
        return hex.toString();
    }
}
