package json;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        JSONBuilder jb = JSONBuilder.json()
                .put("name", "peter")
                .put("age", 31)
                .put("friend", JSONBuilder.json().put("kk", 11));
        System.out.println(jb.toJSONString());

        JSONBuilder jb2 = JSONBuilder.json()
                .put("agg", JSONBuilder.json().put("max_ts",
                        JSONBuilder.json().put("terms",
                                JSONBuilder.json().put("field", "k1")))
                );
        System.out.println(jb2);


        JSONBuilder jb3 =
                JSONBuilder.json("agg",
                        JSONBuilder.json("max_ts",
                                JSONBuilder.json("terms",
                                        JSONBuilder.json("field", 1)
                                )
                        )
                );
        System.out.println(jb3);
    }
}
