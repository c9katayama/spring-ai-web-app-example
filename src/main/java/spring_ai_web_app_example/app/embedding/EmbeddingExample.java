package spring_ai_web_app_example.app.embedding;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import spring_ai_web_app_example.common.CommonConfig;

@SpringBootApplication
@Import(CommonConfig.class)
@PropertySource(value = "classpath:secure.properties", ignoreResourceNotFound = true)
@PropertySource("classpath:application-ai.properties")
@PropertySource("classpath:application-app.properties")
/**
 * Embeddingのサンプル
 */
public class EmbeddingExample implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(EmbeddingExample.class, args);
	}

	@Autowired
	private EmbeddingModel embeddingModel;

	@Override
	public void run(String... args) throws Exception {

		float[] embed = embeddingModel.embed("Embeddingする対象のテキスト");
		System.out.println(embed.length);
		System.out.println(embed);
	}
}
