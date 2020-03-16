package ch.rra.rprj;

// See: https://spring.io/guides/gs/testing-web/
// See: https://howtodoinjava.com/spring-boot2/testing/testresttemplate-post-example/

import ch.rra.rprj.model.core.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

// ./mvnw -Dtest=HttpRequestTest test

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class HttpRequestTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    // ./mvnw -Dtest=HttpRequestTest#pingTest test
    @Test
    public void pingTest() throws Exception {
        System.out.println("restTemplate: " + restTemplate);
        String resp = this.restTemplate.getForObject("http://localhost:" + port + "/ping",
                String.class);
        System.out.println("resp: " + resp);
        assertThat(resp).contains("pong");
    }

    // ./mvnw -Dtest=HttpRequestTest#loginTest test
    @Test
    public void loginTest() throws Exception {
        User resp = (User) this.restTemplate.postForObject(
                "http://localhost:" + port + "/login",
                new HashMap<String,String>(){
                    {
                        put("login","adm");
                        put("pwd","adm");
                    }
                },
                User.class);
        //System.out.println("resp: " + resp);
        assertThat(resp).isNotNull();
    }

    // ./mvnw -Dtest=HttpRequestTest#loginNegativeTest test
    @Test
    public void loginNegativeTest() throws Exception {
        User resp = (User) this.restTemplate.postForObject(
                "http://localhost:" + port + "/login",
                new HashMap<String,String>(){
                    {
                        put("login","adm");
                        put("pwd","ad");
                    }
                },
                User.class);
        //System.out.println("resp: " + resp);
        assertThat(resp).isNull();
    }

    @Test
    public void contexLoads() throws Exception {
        assertThat(restTemplate).isNotNull();
    }
}
