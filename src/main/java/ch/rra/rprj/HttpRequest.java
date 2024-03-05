package ch.rra.rprj;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class HttpRequest {
    private static final Logger log = LogManager.getLogger(HttpRequest.class);

    private static final List<String> _postPutDelete = Arrays.asList("POST", "PUT", "DELETE");

    String endpoint = "TODO";
    String resource = "TODO";
    String status;
    String requestMethod;
    String url;
    LinkedHashMap requestParameters;
    LinkedHashMap requestHeaders;
    LinkedHashMap responseHeaders;
    /** application/json , multipart/formdata , text/plain */
    String contentType;
    String requestContent;
    String responseContent;

    private List<String> validStatusCodes;

    // TODO
    String rawRequestData = "TODO";
    String rawResponseData;

    private HttpURLConnection conn;
    private String queryString;
    private String _url;

    HttpRequest stepHttpRequest(LinkedHashMap<String, Object> _args) {
        // **** Input parameters: start.
        requestMethod = _args.containsKey("method") ? (String) _args.get("method") : "GET";
        url = _args.containsKey("url") ? (String) _args.get("url") : "";
        requestParameters = _args.containsKey("params") ? (LinkedHashMap) _args.get("params") : new LinkedHashMap();
        requestHeaders = _args.containsKey("headers") ? (LinkedHashMap) _args.get("headers") : new LinkedHashMap();
        contentType = _args.containsKey("contentType") ? (String) _args.get("contentType") : "application/json";
        requestContent = _args.containsKey("body") ? (String) _args.get("body") : "";
        validStatusCodes = _args.containsKey("valid_status_codes") ? _args.get("valid_status_codes") : new List<String>();
        // **** Input parameters: end.

        // Moved below
//        if (_postPutDelete.contains(requestMethod)) {
//            requestHeaders.put("Content-Type", contentType);
//            requestHeaders.put("Content-Length", ""+requestContent.length()); // Number in string format is needed
//        }

        log.debug("url: $url => " +url);

        log.debug("params: " + requestParameters.entrySet().stream().map(
                x -> ((Map.Entry) x).getKey()+"="+((Map.Entry) x).getValue()
        ).collect(Collectors.joining("&")));
        queryString = requestMethod.toUpperCase() == "GET" && requestParameters.size() > 0 ?
                "?" + requestParameters.entrySet().stream().map(
                        x -> ((Map.Entry) x).getKey()+"="+((Map.Entry) x).getValue()
                ).collect(Collectors.joining("&"))
                : "";
        log.debug("queryString: $queryString");

        _url = url + queryString;
        log.debug("_url: $_url");


        conn = (HttpURLConnection) new URL(_url).openConnection();
        conn.setRequestMethod(requestMethod);

        // IF requestMethod in ['POST', 'PUT', 'DELETE']
        if (_postPutDelete.contains(requestMethod)) {
            conn.setDoOutput(true);
            conn.setDoInput(true);

            requestHeaders.put("Content-Type", contentType);
            requestHeaders.put("Content-Length", ""+requestContent.length()); // Number in string format is needed
        }
        requestHeaders.forEach({ x ->
                conn.setRequestProperty(((Map.Entry) x).getKey(), ((Map.Entry) x).getValue())
        });
        rawRequestData = this.getRawRequestData();

        // IF requestMethod in ['POST', 'PUT', 'DELETE']
        if (_postPutDelete.contains(requestMethod)) {
            try {
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(requestContent);
                wr.flush();
            } catch(IOException ioe) {
                ioe.printStackTrace();
            }
        }

        try {
            responseContent = conn.getResponseCode()>=200 && conn.getResponseCode()<300 ?
                    conn.getInputStream().getText() :
                    conn.getErrorStream().getText();
            rawResponseData = getRawResponseData();
        } catch (java.io.IOException ioe) {
            ioe.printStackTrace();
        }
        status = conn.getHeaderField(0);
        responseHeaders = conn.getHeaderFields();
        log.debug(status);
        log.debug(responseContent);
        return this;
    }

    boolean hasValidStatus() {
        if(validStatusCodes==null || validStatusCodes.size()==0) return true;
        if(status==null) return false;
        boolean ret = false;
        for(String code in validStatusCodes) {
            if(status.indexOf(code)<0) continue;
            ret = true;
            break;
        }
        return ret;
    }

    private String getRawRequestData() {
        ArrayList<String> ret = [];
        ret.add("${requestMethod} $_url" );
        requestHeaders.each({ a, b ->
                ret.add("$a: ${context.expand(b)}");
        })
        ret.add(requestContent);
        return ret.join("\n");
    }
    private getRawResponseData() {
        ArrayList<String> ret = [];
        responseHeaders.each({ a, b ->
                ret.add("$a: ${context.expand(b)}")
        });
        ret.add(responseContent);
        return ret.join("\n");
    }

    // NOTE To avoid translating too much
    def getResponseHeaders() {
        responseHeaders["#status#"]=status;
        return responseHeaders;
    }


    String toString(String prefix='') {
        ArrayList<String> ret = [];
        ret.add("url: $_url");
        ret.add(prefix+"responseHeaders:");
        responseHeaders.each({k,v ->
                ret.add(prefix+"  " + k + ": " + v);
        });
        if(responseContent!==null) {
            ret.add(prefix+"responseContent:");
            ret.addAll( responseContent.split("\n").collect({String it -> prefix + "  " + it}) );
        }
        return ret.join("\n");
    }

    String textAction() {
        ArrayList<String> ret = ["curl -v -i"];
        requestHeaders.each({k,v ->
                ret.add("-H \"" + k + ": " + context.expand(v) + "\"");
        })
        ret.add("TODO: add method"+requestMethod);
        ret.add(_url);
        return ret.join(" ");
    }

}
