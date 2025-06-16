package org.utn.tpfinalprogramacion3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class TPfinalProgramacion3Application {

	public static void main(String[] args) {
		System.out.println(new BCryptPasswordEncoder().encode("clave123"));
		SpringApplication.run(TPfinalProgramacion3Application.class, args);
	}

}
