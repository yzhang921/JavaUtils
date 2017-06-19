package util;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

/**
 * 
 * ASE-256加解密算法
 * 
 * @author cao_zy
 * 
 */
public class CryptionUtils {
    
    //private static final Logger logger = LoggerFactory.getLogger(CryptionUtils.class);

    private static byte[] key = "78B31088-F0E4-4A86-B749-429D9F77".getBytes();
    private static byte[] iv = new byte[16];

    /**
     * @param cipher
     * @param data
     * @return
     * @throws Exception
     */
    private static byte[] cipherData(PaddedBufferedBlockCipher cipher, byte[] data) throws Exception {
        int minSize = cipher.getOutputSize(data.length);
        byte[] outBuf = new byte[minSize];
        int length1 = cipher.processBytes(data, 0, data.length, outBuf, 0);
        int length2 = cipher.doFinal(outBuf, length1);
        int actualLength = length1 + length2;
        byte[] result = new byte[actualLength];
        System.arraycopy(outBuf, 0, result, 0, result.length);
        return result;
    }

    /**
     * @param cipher
     * @param key
     * @param iv
     * @return
     * @throws Exception
     */
    private static byte[] decrypt(byte[] cipher, byte[] key, byte[] iv) throws Exception {
        PaddedBufferedBlockCipher aes = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()));
        CipherParameters ivAndKey = new ParametersWithIV(new KeyParameter(key), iv);
        aes.init(false, ivAndKey);
        return cipherData(aes, cipher);
    }

    /**
     * @param plain
     * @param key
     * @param iv
     * @return
     * @throws Exception
     */
    private static byte[] encrypt(byte[] plain, byte[] key, byte[] iv) throws Exception {
        PaddedBufferedBlockCipher aes = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()));
        CipherParameters ivAndKey = new ParametersWithIV(new KeyParameter(key), iv);
        aes.init(true, ivAndKey);
        return cipherData(aes, plain);
    }

    /**
     * @param plainText
     * @return
     * @throws Exception
     */
    public static String encode(String plainText) throws Exception {
        return new String(Base64.encodeBase64(encrypt(plainText.getBytes(), key, iv)));
    }

    /**
     * 
     * @param plainText
     * @param key
     * @return
     * @throws Exception
     */
    public static String encode(String plainText, String key) throws Exception {
        return new String(Base64.encodeBase64(encrypt(plainText.getBytes(), key.getBytes(), iv)));
    }
    /**
     * ASE解密
     * 
     * @param encodedText
     * @return
     * @throws Exception
     */
    public static String decode(String encodedText) throws Exception {
        return new String(decrypt(Base64.decodeBase64(encodedText.getBytes()), key, iv));
    }

    /**
     * 
     * @param encodedText
     * @param key
     * @return
     * @throws Exception
     */
    public static String decode(String encodedText, String key) throws Exception {
        return new String(decrypt(Base64.decodeBase64(encodedText.getBytes()), key.getBytes(), iv));
    }
}
