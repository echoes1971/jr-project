package ch.rra.rprj;

import java.util.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONString;
import org.json.JSONWriter;
import org.testng.annotations.Test;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.List;

public class HttpRequestTest {
    private static final Logger log = LogManager.getLogger(HttpRequest.class);

    @Test
    void test_doget() {
        HttpRequest req = new HttpRequest();

        LinkedHashMap<String,String> params = new LinkedHashMap<>();

        LinkedHashMap<String,String> headers = new LinkedHashMap<>();

        String body = "{method:'ping',params:[]}";

        LinkedHashMap<String,Object> hm = new LinkedHashMap<>();
//        hm.put("url","http://localhost:8080/jsonserver.php");
        hm.put("url","http://voyager:8080/jsonserver.php");
//        hm.put("url","https://www.roccoangeloni.it/rproject/jsonserver.php");
        hm.put("method","POST");
        hm.put("params",params);
        hm.put("headers",headers);
        hm.put("contentType","application/json");
        hm.put("body",body);

        req.stepHttpRequest(hm);

        String response = req.toString();
        log.info("'{}'",response);

        log.info("responseContent: '{}'",req.responseContent);

        String responseContent = req.responseContent;

        JSONArray jo = new JSONArray(responseContent);
        log.info(jo.toString(2));

        List<Object> l = jo.toList();
        // Decode error messages
//        byte[] decodedBytes = Base64.getDecoder().decode((String) l.get(0));
//        String decodedString = new String(decodedBytes);
//        log.info(decodedString);
//        jo.put(0, decodedString);
        l.set(0, new String(Base64.getDecoder().decode((String) l.get(0))));

        log.info(JSONWriter.valueToString(l));

        l.forEach(o -> {
            log.info(o);
        });
    }

}
