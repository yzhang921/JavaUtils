package util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.StandardCharsets;

public class DigestUtil {
    private static String SEC_KEY = "b9926b837da1";

    public static String sha256(String text) {
        String strDes = null;

        try {
            strDes = DigestUtils.sha256Hex(text);
        } catch (Exception e) {
            return null;
        }
        return strDes;
    }

    public static String base64Encode(String data) {
        String strRet = null;

        try {
            strRet = Base64.encodeBase64String((SEC_KEY + data).getBytes());
        } catch (Exception e) {
            return null;
        }
        return strRet.replaceAll("\r|\n", "");
    }

    public static String base64Decode(String data) {
        String strRet = null;

        try {
            strRet = new String(Base64.decodeBase64(data.getBytes()), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
        return strRet.substring(SEC_KEY.length());
    }
}
