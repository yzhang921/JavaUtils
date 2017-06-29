import static es.query.QueryBuilder.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created by Peter on 2017/5/7.
 */
public class DslApp {

    private static final Logger LOG = LoggerFactory.getLogger(DslApp.class);

    public static void main(String[] args) {
        LOG.info("Term query: \n{}", termQuery("name", "peter"));
        LOG.info("Terms query: \n{}", termsQuery("name", Arrays.asList("peter", "karen")).put("boost", 1.0));
        LOG.info("Range query: \n{}", rangeQuery("age", 15, 30, false, false));
        LOG.info("Range query: \n{}", rangeQuery("age", 15, 30, false, true));
        LOG.info("Range query: \n{}", rangeQuery("age", 15, null, true, false));
        LOG.info("Range query: \n{}", rangeQuery("age", "2017-05-01", null, true, false));
        LOG.info("Term: \n{}", termQuery("name", "peter").getJSONObject("term"));
    }
}
