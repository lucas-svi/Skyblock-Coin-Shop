package lucas.skyblock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SkyblockApplicationStarter {

	public static void main(String[] args) {
		SpringApplication.run(SkyblockApplicationStarter.class, args);

		Skyblock.getInstance().start();
	}

}
