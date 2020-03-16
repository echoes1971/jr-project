package ch.rra.rprj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
public class RPrjApplication {

/*
	@GetMapping(value = "/{path:[^\\.]*}")
	public String redirect() {
		return "forward:/";
	}
*/

	public static void main(String[] args) {
		SpringApplication.run(RPrjApplication.class, args);
	}

}
