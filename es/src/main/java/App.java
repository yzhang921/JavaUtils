
import org.elasticsearch.index.query.QueryBuilder;

import static org.elasticsearch.index.query.QueryBuilders.*;

import org.elasticsearch.search.aggregations.AggregationBuilders;

/**
 * Created by Peter on 2017/5/7.
 */
public class App {
    public static void main(String[] args) {
        QueryBuilder qb = matchAllQuery();
        System.out.println(qb);
        System.out.println(termQuery("name", "kimchy"));
        System.out.println(termsQuery("tags", "blue", "pill"));
        System.out.println(rangeQuery("price")
                .from(5)
                .to(10)
                .includeLower(true)
                .includeUpper(false));
    }
}
