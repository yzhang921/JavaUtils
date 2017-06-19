package util.encryption;


import java.io.*;
import java.math.BigInteger;

/**
 * Byte处理工具
 *
 * @author S.Violet
 */

public class ByteUtils {

    private static final String HEX_STRING_MAPPING = "0123456789ABCDEF";

    /**
     * 把两个byte[]前后拼接成一个byte[]
     *
     * @param left  left bytes
     * @param right right bytes
     * @return jointed bytes
     */
    public static byte[] joint(byte[] left, byte[] right) {
        byte[] result = new byte[left.length + right.length];
        System.arraycopy(left, 0, result, 0, left.length);
        System.arraycopy(right, 0, result, left.length, right.length);
        return result;
    }

    /**
     * bytes转为hexString
     *
     * @param bytes bytes
     * @return lower case hex string
     */
    public static String bytesToHex(byte[] bytes) {
        return bytesToHex(bytes, false);
    }

    /**
     * bytes转为hexString
     *
     * @param bytes     bytes
     * @param upperCase true:upper case
     * @return hex string
     */
    public static String bytesToHex(byte[] bytes, boolean upperCase) {
        if (bytes == null) {
            return null;
        }
        if (bytes.length <= 0) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder("");
        for (byte unit : bytes) {
            int unitInt = unit & 0xFF;
            String unitHex = Integer.toHexString(unitInt);
            if (unitHex.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(unitHex);
        }
        if (upperCase)
            return stringBuilder.toString().toUpperCase();
        else
            return stringBuilder.toString();
    }

    /**
     * hexString转为bytes
     *
     * @param hexString hexString
     * @return bytes
     */
    public static byte[] hexToBytes(String hexString) {
        if (hexString == null) {
            return null;
        }
        if (hexString.length() <= 0) {
            return new byte[0];
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            int step = i * 2;
            result[i] = (byte) (charToByte(hexChars[step]) << 4 | charToByte(hexChars[step + 1]));
        }
        return result;
    }

    private static byte charToByte(char c) {
        return (byte) HEX_STRING_MAPPING.indexOf(c);
    }

    /**
     * 对象转数组
     *
     * @param obj object
     * @return bytes
     */
    public static byte[] objectToByte(Object obj) throws IOException {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        oos.flush();
        bytes = bos.toByteArray();
        oos.close();
        bos.close();
        return bytes;
    }

    /**
     * 数组转对象
     *
     * @param bytes bytes
     * @return object
     */
    public static Object byteToObject(byte[] bytes) throws IOException, ClassNotFoundException {
        Object obj = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bis);
        obj = ois.readObject();
        ois.close();
        bis.close();
        return obj;
    }

    public static String string2Hex(String arg) throws UnsupportedEncodingException {
        return String.format("%040x", new BigInteger(1, arg.getBytes("utf-8")));
    }

    public static String string2Hex(String arg, String charSet) {
        return String.format("%040x", new BigInteger(1, arg.getBytes(/*YOUR_CHARSET?*/)));
    }

    public static byte[] string2HexByte(String arg) {
        byte[] result = null;
        try {
            result = ByteUtils.hexToBytes(string2Hex(arg));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
}
