package com.innovationandtrust.utils.encryption;


import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Utility class of AES Encryption and Decryption.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AESUtils {
    public static final String CBC_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String AES_ALGORITHM = "AES";
    private static final String SECRET_KEY_FACTORY_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int PASSWORD_ITERATION_COUNT = 65536;
    private static final int KEY_LENGTH_BYTES = 256;
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    /**
     * Generates a random {@link SecretKey} from a Cryptographically Secure(Pseudo-)Random Number.
     *
     * @param keySize size of the secret key in n(128, 192, and 256) bits
     * @return {@link SecretKey}
     */
    public static SecretKey generateRandomKey(int keySize) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
        keyGenerator.init(keySize);
        return keyGenerator.generateKey();
    }

    /**
     * Generates a random {@link SecretKey} from a given password using a password-based key.
     *
     * @param password given password to generate
     * @param salt     value for turning a password into a secret key
     * @return {@link SecretKey}
     */
    public static SecretKey generateKeyFromPassword(String password, String salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(SECRET_KEY_FACTORY_ALGORITHM);
        KeySpec keySpec =
                new PBEKeySpec(
                        password.toCharArray(), salt.getBytes(), PASSWORD_ITERATION_COUNT, KEY_LENGTH_BYTES);
        return new SecretKeySpec(secretKeyFactory.generateSecret(keySpec).getEncoded(), AES_ALGORITHM);
    }

    /**
     * Generate a random {@link IvParameterSpec}
     *
     * @return {@link IvParameterSpec}
     */
    public static IvParameterSpec generateIvSpec(String refValue) throws NoSuchAlgorithmException {
        var md5 = MessageDigest.getInstance("MD5");
        var rawHash = md5.digest(refValue.getBytes());
        var iv = ArrayUtils.subarray(rawHash, 0, 16);
        return new IvParameterSpec(iv);
    }

    /**
     * Encrypt a given string.
     *
     * @return base64 encoded string.
     */
    public static byte[] encrypt(
            String input, String algorithm, SecretKey secretKey, IvParameterSpec ivSpec)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        return cipher.doFinal(input.getBytes());
    }

    /**
     * Decrypt a given base64 encoded string to the content.
     *
     * @return content string;
     */
    public static byte[] decrypt(
            String algorithm, byte[] cipherValue, SecretKey secretKey, IvParameterSpec ivSpec)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        return cipher.doFinal(cipherValue);
    }

}
