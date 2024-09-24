package spring_ai_web_app_example.app.vector_store_initializer;

import java.io.IOException;
import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import spring_ai_web_app_example.common.CommonConfig;

@SpringBootApplication
@Import(CommonConfig.class)
@PropertySource(value = "classpath:secure.properties", ignoreResourceNotFound = true)
@PropertySource("classpath:application-ai.properties")
@PropertySource("classpath:application-app.properties")
/**
 * VectorStoreに初期データを登録
 * 
 * <pre>
 * 初期データ(PDF)はsrc/main/resources/ragに格納。
 * VectorStoreを再初期化する場合は、db/vector-store.jsonを削除して再度実行。
 * </pre>
 */
public class VectorStoreInitializer implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(VectorStoreInitializer.class, args);
	}

	@Autowired
	private VectorStore vectorStore;

	@Autowired
	private ResourcePatternResolver resourcePatternResolver;

	@Override
	public void run(String... args) throws Exception {
		if (CommonConfig.VECTOR_STORE_FILE.exists() == false) {
			initVectorDB();
		}
		// 検索テスト
		SearchRequest serRequest = SearchRequest.query("AI").withTopK(1);
		vectorStore.similaritySearch(serRequest).stream().forEach(System.out::println);
	}

	private void initVectorDB() throws IOException {
		Resource[] resources = resourcePatternResolver.getResources("classpath:/rag/**.pdf");
		for (Resource resource : resources) {
			System.out.println(resource.getFilename());
			// 1ページ1Documentを作成する
			PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(resource,
					PdfDocumentReaderConfig.builder().build());
			List<Document> docs = pdfReader.read();
			vectorStore.add(docs);
		}

		if (vectorStore instanceof SimpleVectorStore) {
			((SimpleVectorStore) vectorStore).save(CommonConfig.VECTOR_STORE_FILE);
		}

		System.exit(0);
	}
}
