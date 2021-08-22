package COVID19.WhenWeMaskOff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WhenWeMaskOffApplication {

	public static void main(String[] args) {
		SpringApplication.run(WhenWeMaskOffApplication.class, args);
	}

}
