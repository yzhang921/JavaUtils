package util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * <p>
 * 类说明:提取身份证相关信息
 * </p>
 */
public class IdcardInfoExtractor {

    public static final String K_ISVALID = "isvalid", K_PROVINCE = "provn", K_CITY = "city", K_GENDER = "gender", K_BIRTHDAY = "birth";
    private static SimpleDateFormat defaultDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat idDateFormat = new SimpleDateFormat("yyyyMMdd");

    public static InputStreamReader propFile = null;
    public static Properties prop = new Properties();
    static {
        try {
            propFile = new InputStreamReader(IdcardInfoExtractor.class.getClassLoader().getResourceAsStream("city_decode.properties"),
                    "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            prop.load(propFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> extractIdCardInfo(String idcard, int type) {

        // 出生日期
        Date birthday;

        Map<String, String> result = new HashMap<String, String>();
        result.put(K_ISVALID, "0");
        result.put(K_PROVINCE, "-");
        result.put(K_CITY, "-");
        result.put(K_GENDER, "-");
        result.put(K_BIRTHDAY, "-");

        if (!IdcardValidator.isValidatedAllIdcard(idcard)) {
            return result;
        }

        result.put(K_ISVALID, "1");

        try {
            // 15位转为18位
            if (idcard.length() == 15) {
                idcard = IdcardValidator.convertIdcarBy15bit(idcard);
            }

            // 获取省份
            String provinceId = idcard.substring(0, 2);
            String prov = prop.getProperty(provinceId);
            if (type == 0 & prov != null) {
                prov = (prov.indexOf("省") == prov.length() - 1) ? prov.substring(0, prov.length() - 1) : prov;
            }
            result.put(K_PROVINCE, (prov == null) ? "-" : prov);

            // 获取城市
            String cityId = idcard.substring(0, 4);
            String city = prop.getProperty(cityId);
            if (type == 0 & city != null) {
                city = (city.indexOf("市") == city.length() - 1) ? city.substring(0, city.length() - 1) : city;
            }
            result.put(K_CITY, (city == null) ? "-" : city);

            // 获取性别
            String id17 = idcard.substring(16, 17);
            result.put(K_GENDER, Integer.parseInt(id17) % 2 != 0 ? "男" : "女");

            // 获取出生日期
            String birthdayStr = idcard.substring(6, 14);

            birthday = idDateFormat.parse(birthdayStr);
            result.put(K_BIRTHDAY, defaultDateFormat.format(birthday));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }

}