package ch.rra.rprj;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import java.util.LinkedHashMap;

public class HttpRequestTest {
    private static final Logger log = LogManager.getLogger(HttpRequest.class);

    @Test
    void test_doget() {
        HttpRequest req = new HttpRequest();

        LinkedHashMap<String,String> params = new LinkedHashMap<>();

        LinkedHashMap<String,String> headers = new LinkedHashMap<>();

        String body = "{method:'ping',params:[]}";

        LinkedHashMap<String,Object> hm = new LinkedHashMap<>();
        hm.put("url","https://www.roccoangeloni.it/rproject/jsonserver.php");
        hm.put("method","GET");
        hm.put("params",params);
        hm.put("headers",headers);
        hm.put("contentType","application/json");
        hm.put("body",body);

        req.stepHttpRequest(hm);

        log.info(req.toString());

    }

}
