package cz.hatoff.bbn.security;

import org.apache.commons.codec.binary.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Encrypter {

    private static final String ALGO = "AES";
    private static final String SALT = "s0m3_s3Cr3T_S4lT";
    private static final String KEY_STRING = "pCP04ae8";


    public String encrypt(String toEncrypt) throws EncrypterException {
        Key key = generateKey(KEY_STRING);
        Cipher c = null;
        try {
            c = Cipher.getInstance(ALGO);
            c.init(Cipher.ENCRYPT_MODE, key);
            byte[] encVal = c.doFinal(toEncrypt.getBytes("UTF-8"));
            String encryptedValue = new String(Base64.encodeBase64(encVal));
            return encryptedValue;
        } catch (NoSuchAlgorithmException e) {
            throw new EncrypterException(e);
        } catch (NoSuchPaddingException e) {
            throw new EncrypterException(e);
        } catch (IllegalBlockSizeException e) {
            throw new EncrypterException(e);
        } catch (BadPaddingException e) {
            throw new EncrypterException(e);
        } catch (InvalidKeyException e) {
            throw new EncrypterException(e);
        } catch (UnsupportedEncodingException e) {
            throw new EncrypterException(e);
        }
    }

    public String decrypt(String encryptedData) throws EncrypterException {
        Key key = generateKey(KEY_STRING);
        Cipher c = null;
        try {
            c = Cipher.getInstance(ALGO);
            c.init(Cipher.DECRYPT_MODE, key);
            byte[] decordedValue = Base64.decodeBase64(encryptedData);
            byte[] decValue = c.doFinal(decordedValue);
            String decryptedValue = new String(decValue, "UTF-8");
            return decryptedValue;
        } catch (NoSuchAlgorithmException e) {
            throw new EncrypterException(e);
        } catch (NoSuchPaddingException e) {
            throw new EncrypterException(e);
        } catch (IllegalBlockSizeException e) {
            throw new EncrypterException(e);
        } catch (BadPaddingException e) {
            throw new EncrypterException(e);
        } catch (InvalidKeyException e) {
            throw new EncrypterException(e);
        } catch (IOException e) {
            throw new EncrypterException(e);
        }
    }

    private byte[] hashIt(String toHash) throws EncrypterException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.reset();
            md.update(SALT.getBytes());
            byte[] digest = md.digest(toHash.getBytes("UTF-8"));
            return Arrays.copyOfRange(digest, 0, 16);
        } catch (NoSuchAlgorithmException e) {
            throw new EncrypterException(e);
        } catch (UnsupportedEncodingException e) {
            throw new EncrypterException(e);
        }
    }

    private Key generateKey(String keyString) throws EncrypterException {
        if (keyString == null || keyString.equals("")) {
            throw new EncrypterException("Secret key cannot be empty value.");
        }
        byte[] bytes = hashIt(keyString);
        Key key = new SecretKeySpec(bytes, ALGO);
        return key;
    }
}
