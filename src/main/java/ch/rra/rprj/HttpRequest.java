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
    HashMap<String,String> requestParameters;
    HashMap<String,String> requestHeaders;
    HashMap<String,String> responseHeaders;
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

    public HttpRequest stepHttpRequest(HashMap<String, Object> _args) {
        // **** Input parameters: start.
        requestMethod = _args.containsKey("method") ? (String) _args.get("method") : "GET";
        url = _args.containsKey("url") ? (String) _args.get("url") : "";
        requestParameters = _args.containsKey("params") ? (HashMap) _args.get("params") : new HashMap();
        requestHeaders = _args.containsKey("headers") ? (HashMap) _args.get("headers") : new HashMap();
        contentType = _args.containsKey("contentType") ? (String) _args.get("contentType") : "application/json";
        requestContent = _args.containsKey("body") ? (String) _args.get("body") : "";
        validStatusCodes = _args.containsKey("valid_status_codes") ? ((List<String>)_args.get("valid_status_codes")) : new ArrayList<String>();
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
        for(Map.Entry me : requestHeaders.entrySet()) {
            conn.setRequestProperty((String) me.getKey(), (String) me.getValue());
        }
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
                    new String(conn.getInputStream().readAllBytes()) :
                    new String(conn.getErrorStream().readAllBytes());
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

    public boolean hasValidStatus() {
        if(validStatusCodes==null || validStatusCodes.size()==0) return true;
        if(status==null) return false;
        boolean ret = false;
        for(String code : validStatusCodes) {
            if(status.indexOf(code)<0) continue;
            ret = true;
            break;
        }
        return ret;
    }

    private String getRawRequestData() {
        ArrayList<String> ret = new ArrayList<String>();
        ret.add("${requestMethod} $_url" );
        for(Map.Entry me : requestHeaders) {
            ret.add(me.getKey()+": "+me.getValue());
        }
        ret.add(requestContent);
        return ret.stream().collect(Collectors.joining("\n"));
    }
    private String getRawResponseData() {
        ArrayList<String> ret = new ArrayList<String>();
        for(Map.Entry me : responseHeaders) {
            ret.add(me.getKey()+": "+me.getValue());
        }
        ret.add(responseContent);
        return ret.stream().collect(Collectors.joining("\n"));
    }

    // NOTE To avoid translating too much
    public HashMap<String,String> getResponseHeaders() {
        responseHeaders["#status#"]=status;
        return responseHeaders;
    }


    public String toString() { return toString(""); }
    public String toString(String prefix) {
        ArrayList<String> ret = new ArrayList<String>();
        ret.add("url: $_url");
        ret.add(prefix+"responseHeaders:");
        for(Map.Entry me : responseHeaders) {
            ret.add(prefix + "  " + me.getKey() + ": " + me.getValue());
        }
        if(responseContent!=null) {
            ret.add(prefix+"responseContent:");
            for(String s : responseContent.split("\n")) {
                ret.add(prefix+"  "+s);
            }
        }
        return ret.stream().collect(Collectors.joining("\n"));
    }

    public String textAction() {
        ArrayList<String> ret = new ArrayList<String>();
        ret.add("curl -v -i");
        for(Map.Entry me : requestHeaders) {
            ret.add("-H \"" + me.getKey() + ": " + me.getValue() + "\"");
        }
        ret.add("TODO: add method"+requestMethod);
        ret.add(_url);
        return ret.stream().collect(Collectors.joining(" "));
    }

}
