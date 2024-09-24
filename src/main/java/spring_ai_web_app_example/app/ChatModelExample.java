package spring_ai_web_app_example.app;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
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
 * ChatModelのサンプル
 */
public class ChatModelExample implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ChatModelExample.class, args);
	}

	@Autowired
	private ChatModel chatModel;

	@Override
	public void run(String... args) throws Exception {

		List<Message> messageList = new ArrayList<>();
		messageList.add(new SystemMessage("あなたは天気予報士です。"));
		messageList.add(new UserMessage("雨はどのような状態になると振りますか？"));
		{
			Prompt prompt = new Prompt(messageList);
			ChatResponse response = chatModel.call(prompt);
			response.getResult().getMetadata().getFinishReason();
			AssistantMessage assistantMessage = response.getResult().getOutput();
			System.out.println(assistantMessage.getContent());
			// 前回の返答をリストに入れる
			messageList.add(assistantMessage);
		}
		{// 新しい質問
			messageList.add(new UserMessage("それは正しいですか？根拠を示してください。"));
			Prompt prompt = new Prompt(messageList);
			ChatResponse response = chatModel.call(prompt);
			AssistantMessage assistantMessage = response.getResult().getOutput();
			System.out.println(assistantMessage.getContent());
		}
		System.exit(0);
	}
}
