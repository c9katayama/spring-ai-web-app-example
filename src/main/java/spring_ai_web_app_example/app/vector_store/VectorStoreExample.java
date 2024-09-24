package spring_ai_web_app_example.app.vector_store;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import spring_ai_web_app_example.common.CommonConfig;

@SpringBootApplication
@Import(CommonConfig.class)
@PropertySource(value = "classpath:secure.properties", ignoreResourceNotFound = true)
@PropertySource("classpath:application-ai.properties")
@PropertySource("classpath:application-app.properties")
/**
 * VectorStoreのサンプル
 */
public class VectorStoreExample implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(VectorStoreExample.class, args);
	}

	@Autowired
	private VectorStore vectorStore;

	@Override
	public void run(String... args) throws Exception {
		Resource resource = new ClassPathResource("/rag/140120240209531969.pdf");
		// 1ページ1Documentを作成する
		PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(resource,
				PdfDocumentReaderConfig.builder().build());
		List<Document> docs = pdfReader.read();
		// VectorStoreの保存(Embeddingもこの中で行われる
		vectorStore.add(docs);

		// 検索テスト
		SearchRequest serRequest = SearchRequest.query("AI").withTopK(1);
		vectorStore.similaritySearch(serRequest).stream().forEach(System.out::println);

		System.exit(0);
	}
}
