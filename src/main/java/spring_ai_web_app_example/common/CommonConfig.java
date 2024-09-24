package spring_ai_web_app_example.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;

@Configuration
public class CommonConfig {

	@Bean
	public AwsCredentialsProvider awsCredentialsProvider() {
		// .aws/credentialsに以下のプロファイル名でキーを登録
		return ProfileCredentialsProvider.create("spring-ai-web-app-example-credential-profile");
	}

	public static final File VECTOR_STORE_FILE = new File("db/vector-store.json");

	@Bean
	public VectorStore vectorStore(EmbeddingModel embeddingModel) throws IOException {
		SimpleVectorStore vectorDB = new SimpleVectorStore(embeddingModel);
		if (VECTOR_STORE_FILE.exists()) {
			vectorDB.load(VECTOR_STORE_FILE);
		} else {
			Files.createDirectories(VECTOR_STORE_FILE.getParentFile().toPath());
		}
		return vectorDB;
	}
}
