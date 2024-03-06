package ch.rra.rprj;

import org.testng.annotations.Test;

import java.util.LinkedHashMap;

public class HttpRequestTest {

    @Test
    void test_doget() {
        HttpRequest req = new HttpRequest();

        LinkedHashMap params = new LinkedHashMap();

        LinkedHashMap headers = new LinkedHashMap();

        String body = "{method:'ping',params:[]}";

        LinkedHashMap hm = new LinkedHashMap();
        hm.put("url","https://www.roccoangeloni.it/jsonserver.php");
        hm.put("method","GET");
        hm.put("params",params);
        hm.put("headers",headers);
        hm.put("contentType","application/json");
        hm.put("body",body);

        req.stepHttpRequest(hm);

    }

}
