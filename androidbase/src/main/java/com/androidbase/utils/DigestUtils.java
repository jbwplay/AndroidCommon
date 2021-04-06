package com.androidbase.utils;


import android.util.Base64;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class DigestUtils {

    private static final int STREAM_BUFFER_LENGTH = 1024 * 8;

    public static MessageDigest getDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException var2) {
            throw new IllegalArgumentException(var2);
        }
    }

    private static byte[] digest(MessageDigest digest, InputStream data) throws
            IOException {
        return updateDigest(digest, data).digest();
    }

    public static MessageDigest getMd5Digest() {
        return getDigest("MD5");
    }

    public static MessageDigest getSha1Digest() {
        return getDigest("SHA-1");
    }

    public static MessageDigest getSha256Digest() {
        return getDigest("SHA-256");
    }

    public static MessageDigest getSha384Digest() {
        return getDigest("SHA-384");
    }

    public static MessageDigest getSha512Digest() {
        return getDigest("SHA-512");
    }

    public static byte[] md5(byte[] data) {
        return getMd5Digest().digest(data);
    }

    public static byte[] md5(InputStream data) throws IOException {
        return digest(getMd5Digest(), data);
    }

    public static byte[] md5(String data) {
        return md5(StringUtils.getBytesUtf8(data));
    }

    public static String md5Hex(byte[] data) {
        return HexUtils.encodeHexStr(md5(data));
    }

    public static String md5Hex(InputStream data) throws IOException {
        return HexUtils.encodeHexStr(md5(data));
    }

    public static String md5Hex(String data) {
        return HexUtils.encodeHexStr(md5(data));
    }

    public static byte[] sha1(byte[] data) {
        return getSha1Digest().digest(data);
    }

    public static byte[] sha1(InputStream data) throws IOException {
        return digest(getSha1Digest(), data);
    }

    public static byte[] sha1(String data) {
        return sha1(StringUtils.getBytesUtf8(data));
    }

    public static String sha1Hex(byte[] data) {
        return HexUtils.encodeHexStr(sha1(data));
    }

    public static String sha1Hex(InputStream data) throws IOException {
        return HexUtils.encodeHexStr(sha1(data));
    }

    public static String sha1Hex(String data) {
        return HexUtils.encodeHexStr(sha1(data));
    }

    public static byte[] sha256(byte[] data) {
        return getSha256Digest().digest(data);
    }

    public static byte[] sha256(InputStream data) throws IOException {
        return digest(getSha256Digest(), data);
    }

    public static byte[] sha256(String data) {
        return sha256(StringUtils.getBytesUtf8(data));
    }

    public static String sha256Hex(byte[] data) {
        return HexUtils.encodeHexStr(sha256(data));
    }

    public static String sha256Hex(InputStream data) throws IOException {
        return HexUtils.encodeHexStr(sha256(data));
    }

    public static String sha256Hex(String data) {
        return HexUtils.encodeHexStr(sha256(data));
    }

    public static byte[] sha384(byte[] data) {
        return getSha384Digest().digest(data);
    }

    public static byte[] sha384(InputStream data) throws IOException {
        return digest(getSha384Digest(), data);
    }

    public static byte[] sha384(String data) {
        return sha384(StringUtils.getBytesUtf8(data));
    }

    public static String sha384Hex(byte[] data) {
        return HexUtils.encodeHexStr(sha384(data));
    }

    public static String sha384Hex(InputStream data) throws IOException {
        return HexUtils.encodeHexStr(sha384(data));
    }

    public static String sha384Hex(String data) {
        return HexUtils.encodeHexStr(sha384(data));
    }

    public static byte[] sha512(byte[] data) {
        return getSha512Digest().digest(data);
    }

    public static byte[] sha512(InputStream data) throws IOException {
        return digest(getSha512Digest(), data);
    }

    public static byte[] sha512(String data) {
        return sha512(StringUtils.getBytesUtf8(data));
    }

    public static String sha512Hex(byte[] data) {
        return HexUtils.encodeHexStr(sha512(data));
    }

    public static String sha512Hex(InputStream data) throws IOException {
        return HexUtils.encodeHexStr(sha512(data));
    }

    public static String sha512Hex(String data) {
        return HexUtils.encodeHexStr(sha512(data));
    }

    public static MessageDigest updateDigest(MessageDigest messageDigest, byte[] valueToDigest) {
        messageDigest.update(valueToDigest);
        return messageDigest;
    }

    public static MessageDigest updateDigest(MessageDigest digest, InputStream data) throws
            IOException {
        byte[] buffer = new byte[STREAM_BUFFER_LENGTH];

        for (int read = data.read(buffer, 0, STREAM_BUFFER_LENGTH); read > -1; read = data
                .read(buffer, 0, STREAM_BUFFER_LENGTH)) {
            digest.update(buffer, 0, read);
        }

        return digest;
    }

    public static MessageDigest updateDigest(MessageDigest messageDigest, String valueToDigest) {
        messageDigest.update(StringUtils.getBytesUtf8(valueToDigest));
        return messageDigest;
    }

    public static byte[] base64Decode(String text) {
        return Base64.decode(text, Base64.NO_WRAP);
    }

    public static String base64Encode(byte[] data) {
        return Base64.encodeToString(data, Base64.NO_WRAP);
    }

    public static class AESCrypto {
        private static final int ITERATION_COUNT_DEFAULT = 100;
        private static final int ITERATION_COUNT_MIN = 10;
        private static final int ITERATION_COUNT_MAX = 5000;
        private static final int KEY_SIZE_DEFAULT = 256;
        private static final int KEY_SIZE_MIN = 64;
        private static final int KEY_SIZE_MAX = 1024;
        private static final int IV_SIZE = 16;
        private String password;
        private byte[] salt;
        private byte[] iv;
        private int keySize;
        private int iterCount;

        public AESCrypto(String password) {
            initialize(password, AES.getSimpleSalt(), AES.getSimpleIV(), KEY_SIZE_DEFAULT, ITERATION_COUNT_DEFAULT);
        }

        public AESCrypto(String password, byte[] salt) {
            initialize(password, salt, AES.getSimpleIV(), KEY_SIZE_DEFAULT, ITERATION_COUNT_DEFAULT);
        }

        public AESCrypto(String password, int keySize, byte[] salt, byte[] iv) {
            initialize(password, salt, iv, keySize, ITERATION_COUNT_DEFAULT);
        }

        private void initialize(String password, byte[] salt, byte[] iv, int keySize, int iterCount) {
            MiscUtils.notEmpty(password, "password must not be null or empty");
            MiscUtils.notNull(salt, "salt must bot be null");
            MiscUtils.notNull(iv, "iv must not be null");
            MiscUtils.isTrue(keySize >= KEY_SIZE_MIN && keySize <= KEY_SIZE_MAX, "keySize " + "must between " + KEY_SIZE_MIN + " and " + KEY_SIZE_MAX);
            MiscUtils.isTrue(iterCount >= ITERATION_COUNT_MIN && iterCount <= ITERATION_COUNT_MAX, "iterCount must between " + ITERATION_COUNT_MIN + " and " + ITERATION_COUNT_MAX);
            this.password = password;
            this.salt = salt;
            this.iv = iv;
            this.keySize = keySize;
            this.iterCount = iterCount;
        }

        public String encrypt(String text) {
            byte[] data = getRawBytes(text);
            byte[] encryptedData = encrypt(data);
            return base64Encode(encryptedData);
        }

        public byte[] encrypt(byte[] data) {
            return process(data, Cipher.ENCRYPT_MODE);
        }

        public String decrypt(String text) {
            byte[] encryptedData = base64Decode(text);
            byte[] data = decrypt(encryptedData);
            return getString(data);
        }

        public byte[] decrypt(byte[] encryptedData) {
            return process(encryptedData, Cipher.DECRYPT_MODE);
        }

        private byte[] process(byte[] data, int mode) {
            return AES.process(data, mode, password, salt, iv, keySize, iterCount);
        }
    }

    /**
     * AES转变
     * <p>法算法名称/加密模式/填充方式</p>
     * <p>加密模式有：电子密码本模式ECB、加密块链模式CBC、加密反馈模式CFB、输出反馈模式OFB</p>
     * <p>填充方式有：NoPadding、ZerosPadding、PKCS5Padding</p>
     * <p>
     * DES转变
     * <p>法算法名称/加密模式/填充方式</p>
     * <p>加密模式有：电子密码本模式ECB、加密块链模式CBC、加密反馈模式CFB、输出反馈模式OFB</p>
     * <p>填充方式有：NoPadding、ZerosPadding、PKCS5Padding</p>
     */
    public static final class AES {
        static final int ITERATION_COUNT_DEFAULT = 100;
        static final int KEY_SIZE_DEFAULT = 256;
        static final int IV_SIZE_DEFAULT = 16;
        static final String KEY_AES_SPEC = "AES/CBC/PKCS7Padding";

        public static String encrypt(String text) {
            return encrypt(text, getSimplePassword(), getSimpleSalt(), getSimpleIV());
        }

        public static String decrypt(String text) {
            return decrypt(text, getSimplePassword(), getSimpleSalt(), getSimpleIV());
        }

        public static byte[] encrypt(byte[] data) {
            return encrypt(data, getSimplePassword(), getSimpleSalt(), getSimpleIV(), KEY_SIZE_DEFAULT, ITERATION_COUNT_DEFAULT);
        }

        public static byte[] decrypt(byte[] data) {
            return decrypt(data, getSimplePassword(), getSimpleSalt(), getSimpleIV(), KEY_SIZE_DEFAULT, ITERATION_COUNT_DEFAULT);
        }

        public static String encrypt(String text, String password) {
            return encrypt(text, password, getSimpleSalt(), getSimpleIV());
        }

        public static String decrypt(String text, String password) {
            return decrypt(text, password, getSimpleSalt(), getSimpleIV());
        }

        public static byte[] encrypt(byte[] data, String password) {
            return encrypt(data, password, getSimpleSalt(), getSimpleIV(), KEY_SIZE_DEFAULT, ITERATION_COUNT_DEFAULT);
        }

        public static byte[] decrypt(byte[] data, String password) {
            return decrypt(data, password, getSimpleSalt(), getSimpleIV(), KEY_SIZE_DEFAULT, ITERATION_COUNT_DEFAULT);
        }

        public static String encrypt(String text, String password, byte[] salt) {
            return encrypt(text, password, salt, getSimpleIV());
        }

        public static String decrypt(String text, String password, byte[] salt) {
            return decrypt(text, password, salt, getSimpleIV());
        }

        public static byte[] encrypt(byte[] data, String password, byte[] salt) {
            return encrypt(data, password, salt, getSimpleIV(), KEY_SIZE_DEFAULT, ITERATION_COUNT_DEFAULT);
        }

        public static byte[] decrypt(byte[] data, String password, byte[] salt) {
            return decrypt(data, password, salt, getSimpleIV(), KEY_SIZE_DEFAULT, ITERATION_COUNT_DEFAULT);
        }

        public static String encrypt(String text, String password, byte[] salt, byte[] iv) {
            byte[] data = getRawBytes(text);
            byte[] encryptedData = encrypt(data, password, salt, iv, KEY_SIZE_DEFAULT, ITERATION_COUNT_DEFAULT);
            return base64Encode(encryptedData);
        }

        public static String decrypt(String text, String password, byte[] salt, byte[] iv) {
            byte[] encryptedData = base64Decode(text);
            byte[] data = decrypt(encryptedData, password, salt, iv, KEY_SIZE_DEFAULT, ITERATION_COUNT_DEFAULT);
            return getString(data);
        }

        public static byte[] encrypt(byte[] data, String password, byte[] salt, byte[] iv) {
            return encrypt(data, password, salt, iv, KEY_SIZE_DEFAULT, ITERATION_COUNT_DEFAULT);
        }

        public static byte[] decrypt(byte[] data, String password, byte[] salt, byte[] iv) {
            return decrypt(data, password, salt, iv, KEY_SIZE_DEFAULT, ITERATION_COUNT_DEFAULT);
        }

        public static byte[] encrypt(byte[] data, String password, byte[] salt, byte[] iv, int keySize) {
            return encrypt(data, password, salt, iv, keySize, ITERATION_COUNT_DEFAULT);
        }

        public static byte[] decrypt(byte[] data, String password, byte[] salt, byte[] iv, int keySize) {
            return decrypt(data, password, salt, iv, keySize, ITERATION_COUNT_DEFAULT);
        }

        public static byte[] encrypt(byte[] data, String password, byte[] salt, byte[] iv, int keySize, int iterationCount) {
            return process(data, Cipher.ENCRYPT_MODE, password, salt, iv, keySize, iterationCount);
        }

        public static byte[] decrypt(byte[] data, String password, byte[] salt, byte[] iv, int keySize, int iterationCount) {
            return process(data, Cipher.DECRYPT_MODE, password, salt, iv, keySize, iterationCount);
        }

        /**
         * AES encrypt function
         *
         * @param original
         * @param key      16, 24, 32 bytes available
         * @param iv       initial vector (16 bytes) - if null: ECB mode, otherwise: CBC
         *                 mode
         * @return
         */
        public static byte[] encrypt(byte[] original, byte[] key, byte[] iv) {
            if (key == null || (key.length != 16 && key.length != 24 && key.length != 32)) {
                return null;
            }
            if (iv != null && iv.length != 16) {
                return null;
            }

            try {
                SecretKeySpec keySpec = null;
                Cipher cipher = null;
                if (iv != null) {
                    keySpec = new SecretKeySpec(key, KEY_AES_SPEC);
                    cipher = Cipher.getInstance(KEY_AES_SPEC);
                    cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv));
                } else // if(iv == null)
                {
                    keySpec = new SecretKeySpec(key, KEY_AES_SPEC);
                    cipher = Cipher.getInstance(KEY_AES_SPEC);
                    cipher.init(Cipher.ENCRYPT_MODE, keySpec);
                }

                return cipher.doFinal(original);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * AES decrypt function
         *
         * @param encrypted
         * @param key       16, 24, 32 bytes available
         * @param iv        initial vector (16 bytes) - if null: ECB mode, otherwise: CBC
         *                  mode
         * @return
         */
        public static byte[] decrypt(byte[] encrypted, byte[] key, byte[] iv) {
            if (key == null || (key.length != 16 && key.length != 24 && key.length != 32)) {
                return null;
            }
            if (iv != null && iv.length != 16) {
                return null;
            }

            try {
                SecretKeySpec keySpec = null;
                Cipher cipher = null;
                if (iv != null) {
                    keySpec = new SecretKeySpec(key, "AES/CBC/PKCS7Padding");//
                    // AES/ECB/PKCS5Padding
                    cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
                    cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv));
                } else // if(iv == null)
                {
                    keySpec = new SecretKeySpec(key, "AES/ECB/PKCS7Padding");
                    cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
                    cipher.init(Cipher.DECRYPT_MODE, keySpec);
                }

                return cipher.doFinal(encrypted);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        static byte[] process(byte[] data, int mode, String password, byte[] salt, byte[] iv, int keySize, int iterationCount) {
            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, iterationCount, keySize);
            try {
                SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
                byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
                SecretKey key = new SecretKeySpec(keyBytes, "AES");
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                IvParameterSpec ivParams = new IvParameterSpec(iv);
                cipher.init(mode, key, ivParams);
                return cipher.doFinal(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        static String getSimplePassword() {
            return "GZ9Gn2U5nhpea8hw";
        }

        static byte[] getSimpleSalt() {
            return "rUiey8D2GNzV69Mp".getBytes();
        }

        static byte[] getSimpleIV() {
            byte[] iv = new byte[AES.IV_SIZE_DEFAULT];
            Arrays.fill(iv, (byte) 5);
            return iv;
        }
    }

    static byte[] getRawBytes(String text) {
        try {
            return text.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            return text.getBytes();
        }
    }

    static String getString(byte[] data) {
        try {
            return new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return new String(data);
        }
    }

}