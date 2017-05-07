package es.aggregation;

import json.JSONBuilder;
import static json.JSONBuilder.*;

/**
 * Created by Peter on 2017/5/7.
 */
public class AggregationBuilder {

    public static JSONBuilder termsAggregation(String fieldName, int size) {
        return json("terms",
                json("field", fieldName).put("size", size));
    }
}
