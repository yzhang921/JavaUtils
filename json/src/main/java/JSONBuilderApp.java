import com.alibaba.fastjson.JSONArray;
import json.JSONBuilder;
import static json.JSONBuilder.*;

/**
 * Hello world!
 */
public class JSONBuilderApp {
    public static void main(String[] args) {
        JSONBuilder jb = json()
                .put("name", "peter")
                .put("age", 31)
                .put("friend", json().put("kk", 11));
        System.out.println(jb.toJSONString());

        JSONBuilder jb2 = json()
                .put("agg", json().put("max_ts",
                        json().put("terms",
                                json().put("field", "k1")))
                );
        System.out.println(jb2);


        JSONBuilder jb3 =
                json("agg",
                        json("max_ts",
                                json("terms",
                                        json("field", 1)
                                )
                        )
                );
        System.out.println(jb3);

        JSONArray ja = new JSONArray();
        ja.add("aa");
        ja.add("bb");
        System.out.println(json("filter", ja));

        System.out.println(jb3.getJSONObject("agg").put("append", "aa"));
    }
}
