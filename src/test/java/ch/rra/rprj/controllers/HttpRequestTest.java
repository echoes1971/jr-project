package ch.rra.rprj.controllers;

// See: https://spring.io/guides/gs/testing-web/
// See: https://howtodoinjava.com/spring-boot2/testing/testresttemplate-post-example/

import ch.rra.rprj.model.core.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Vector;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

// ./mvnw -Dtest=HttpRequestTest test

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class HttpRequestTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Value("${rprj.rootobj.id}")
    private String rootObjId;

    // ./mvnw -Dtest=HttpRequestTest#pingTest test
    @Test
    public void pingTest() throws Exception {
        String resp = this.restTemplate.getForObject("http://localhost:" + port + "/ping",
                String.class);
        System.out.println("resp: " + resp);
        assertThat(resp).contains("pong");
    }

    // ./mvnw -Dtest=HttpRequestTest#rootObj test
    @Test
    public void rootObj() throws Exception {
        HashMap<String, Object> resp = this.restTemplate.getForObject("http://localhost:" + port + "/ui/rootobj",
                HashMap.class);
        System.out.println("resp: " + resp);
        assertThat(resp.get("id")).isEqualTo(rootObjId);
    }

    // ./mvnw -Dtest=HttpRequestTest#currentObj test
    @Test
    public void currentObj() throws Exception {
        String objId = "-10";
        HashMap<String, Object> resp = this.restTemplate.getForObject("http://localhost:" + port + "/ui/obj/" + objId,
                HashMap.class);
        System.out.println("resp: " + resp);
        boolean check = resp.get("id").equals(objId) || resp.get("father_id").equals(objId);
        assertThat(check).isTrue();
        //assertThat(resp.get("id")).isEqualTo(objId);
    }

    // ./mvnw -Dtest=HttpRequestTest#currentObjEmpty test
    @Test
    public void currentObjEmpty() throws Exception {
        String objId = "-10";
        HashMap<String, Object> resp = this.restTemplate.getForObject("http://localhost:" + port + "/ui/obj/",
                HashMap.class);
        System.out.println("resp: " + resp);
        //assertThat(resp.get("id")).isEqualTo(objId);
    }

    // ./mvnw -Dtest=HttpRequestTest#parentList test
    @Test
    public void parentList() throws Exception {
        String objId = "-20";
        Vector<HashMap<String, Object>> resp = this.restTemplate.getForObject(
                "http://localhost:" + port + "/ui/parentlist/" + objId, Vector.class);
        System.out.println("resp: " + resp.size());
        resp.forEach(hm -> {
            System.out.println(" " + hm.get("id") + "\t" + hm.get("father_id") + "\t" + hm.get("name"));
        });
        assertThat(resp.size()).isEqualTo(1);

        objId = "-21";
        resp = this.restTemplate.getForObject("http://localhost:" + port + "/ui/parentlist/" + objId, Vector.class);
        System.out.println("resp: " + resp.size());
        resp.forEach(hm -> {
            System.out.println(" " + hm.get("id") + "\t" + hm.get("father_id") + "\t" + hm.get("name"));
        });
        assertThat(resp.size()).isEqualTo(2);

        objId = "-24";
        resp = this.restTemplate.getForObject("http://localhost:" + port + "/ui/parentlist/" + objId, Vector.class);
        System.out.println("resp: " + resp.size());
        resp.forEach(hm -> {
            System.out.println(" " + hm.get("id") + "\t" + hm.get("father_id") + "\t" + hm.get("name"));
        });
        assertThat(resp.size()).isEqualTo(2);
    }

    // ./mvnw -Dtest=HttpRequestTest#topMenu test
    @Test
    public void topMenu() throws Exception {
        Vector<HashMap<String, String>> resp = this.restTemplate.getForObject(
                "http://localhost:" + port + "/ui/topmenu", Vector.class);
        System.out.println("resp: " + resp);
        System.out.println("ids: "+resp.stream().map(x -> x.get("id")).collect(Collectors.joining(",")));
        assertThat(resp.size()>0);
    }

    // ./mvnw -Dtest=HttpRequestTest#loginTest test
    @Test
    public void loginTest() throws Exception {
        User resp = this.restTemplate.postForObject("http://localhost:" + port + "/login",
                new HashMap<String,String>(){{
                    put("login","adm");
                    put("pwd","adm");
                    }},
                User.class);
        //System.out.println("resp: " + resp);
        assertThat(resp).isNotNull();
    }

    // ./mvnw -Dtest=HttpRequestTest#loginNegativeTest test
    @Test
    public void loginNegativeTest() throws Exception {
        User resp = this.restTemplate.postForObject("http://localhost:" + port + "/login",
                new HashMap<String,String>(){{
                        put("login","adm");
                        put("pwd","ad");
                    }},
                User.class);
        //System.out.println("resp: " + resp);
        assertThat(resp).isNull();
    }

    @Test
    public void contexLoads() throws Exception {
        assertThat(restTemplate).isNotNull();
    }
}
