package spring_ai_web_app_example.app.text2image;

import org.springframework.ai.image.Image;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImageOptionsBuilder;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
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
 * Text2Imageのサンプル
 */
public class Text2ImageExample implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(Text2ImageExample.class, args);
	}

	@Autowired
	private ImageModel imageModel;

	@Override
	public void run(String... args) throws Exception {
		String prompt = "可愛い猫の画像";
		int numOfImages = 1;// 枚数
		int width = 512;// px
		int height = 512;// px
		ImagePrompt imagePrompt = new ImagePrompt(prompt, //
				ImageOptionsBuilder.builder().withN(numOfImages)//
						.withHeight(width).withWidth(height).build());
		ImageResponse call = imageModel.call(imagePrompt);

		call.getResults().forEach(e -> {
			Image image = e.getOutput();
			System.out.println(image.getUrl());// 画像URL
		});

		System.exit(0);
	}
}
