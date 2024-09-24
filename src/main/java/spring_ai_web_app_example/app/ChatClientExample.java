package spring_ai_web_app_example.app;

import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.CallPromptResponseSpec;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
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
 * ChatClieneのサンプル
 */
public class ChatClientExample implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ChatClientExample.class, args);
	}

	@Autowired
	private ChatClient.Builder chatClientBuilder;

	@Override
	public void run(String... args) throws Exception {
		String conversationId = UUID.randomUUID().toString();
		InMemoryChatMemory chatMemory = new InMemoryChatMemory();
		ChatClient chatClient = chatClientBuilder.build();
		int histroyNum = 10;
		chatMemory.add(conversationId, new SystemMessage("あなたは優秀な天気予報士です。"));
		{
			chatMemory.add(conversationId, new UserMessage("今日の天気は?"));
			CallPromptResponseSpec call = chatClient.prompt(new Prompt(chatMemory.get(conversationId, histroyNum)))
					.call();
			String result = call.contents().stream().collect(Collectors.joining());
			System.out.println(result);
			chatMemory.add(conversationId, new AssistantMessage(result));
		}
		{
			chatMemory.add(conversationId, new UserMessage("この結果から言えることは？"));
			CallPromptResponseSpec call = chatClient.prompt(new Prompt(chatMemory.get(conversationId, histroyNum)))
					.call();
			String result = call.contents().stream().collect(Collectors.joining());
			System.out.println(result);
		}
		System.exit(0);
	}
}
