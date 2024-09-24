package spring_ai_web_app_example.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.theme.Theme;

import spring_ai_web_app_example.common.CommonConfig;

/**
 * このクラスを実行して、localhost:8080にアクセス
 *
 */
@SpringBootApplication
@Import({ CommonConfig.class, WebAppConfig.class })
@PropertySource(value = "classpath:secure.properties", ignoreResourceNotFound = true)
@PropertySource("classpath:application-ai.properties")
@PropertySource("classpath:application-webapp.properties")
@Theme(value = "spring-ai-web-app-example")
@Push
public class Application implements AppShellConfigurator {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
