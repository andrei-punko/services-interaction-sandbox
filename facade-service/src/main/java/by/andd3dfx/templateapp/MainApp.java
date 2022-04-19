package by.andd3dfx.templateapp;

import by.andd3dfx.templateapp.util.StartupHelper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class MainApp {

	public static void main(String[] args) {
		Environment env = new SpringApplication(MainApp.class)
				.run(args)
				.getEnvironment();
		StartupHelper.logApplicationStartup(env);
	}
}
