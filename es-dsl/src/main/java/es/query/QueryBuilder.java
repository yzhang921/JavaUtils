package es.query;

import com.alibaba.fastjson.JSONArray;
import json.JSONBuilder;

/**
 * Created by Peter on 2017/5/7.
 */
public class QueryBuilder extends JSONBuilder {

    public static QueryBuilder queryContent() {
        return new QueryBuilder();
    }

    @Override
    public QueryBuilder put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public static QueryBuilder termQuery(String fieldName, Object value) {
        return queryContent().put("term", json(fieldName, value));
    }

    public static QueryBuilder termsQuery(String fieldName, Object... values) {
        return queryContent().put("terms", json(fieldName, values));
    }

    public static QueryBuilder rangeQuery(String fieldName, Object from, Object to, boolean includeLower, boolean includeUpper) {
        JSONBuilder content = json();
        if (from != null) {
            content.put(includeLower ? "gte" : "gt", from);
        }
        if (to != null) {
            content.put(includeLower ? "lte" : "lt", to);
        }
        return queryContent().put("range", json(fieldName, content));
    }

    public static QueryBuilder rangeQuery(String fieldName, Object from, Object to) {
        return rangeQuery(fieldName, from, to, false, false);
    }

    public static QueryBuilder boolFilter(JSONArray filterArray) {
        return queryContent()
                .put("query",
                        json("bool",
                                json("filter", filterArray)
                        ))
                ;
    }
}
