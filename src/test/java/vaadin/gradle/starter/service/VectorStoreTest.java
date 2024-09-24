package vaadin.gradle.starter.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class VectorStoreTest {

	@Autowired
	private VectorStore vectorStore;

	@Test
	public void testVectorStoreOperations() {
		// メタデータの作成
		Map<String, Object> metadata = new HashMap<>();
		metadata.put("source", "userInput");
		metadata.put("timestamp", LocalDateTime.now().toString());

		{
			String text = """
					LINEMOお申し込み特典は終了日未定。
					※1 時間帯により速度制御の場合あり。通話従量制（税込22円/30秒）。ナビダイヤルなど異なる料金の電話番号あり。
					※2 ソフトバンク・ワイモバイル・LINEモバイルからの乗り換えは対象外。
					※3 適用条件あり。特典付与対象判定月までLINEMOベストプランVを継続した場合。
					※4 出金・譲渡不可。PayPay公式ストア/PayPayカード公式ストアでも利用可。
					""";
			Document document = new Document(text, metadata);
			vectorStore.add(Collections.singletonList(document));

		}
		{
			String text = """
					文字列リテラルはある意味で「ささいな」機能です。ですが、これらは小さな苛立ちが積み重なるほど頻繁に使用されます。
					ですから、複数行の文字列がないことが、近年のJavaに対する最も一般的な不満の一つになっているのは当然のことでしょう。
					また、他の多くの言語では、異なるユースケースをサポートするために複数の形式の文字列リテラルが存在します。
					""";
			Document document = new Document(text, metadata);
			vectorStore.add(Collections.singletonList(document));
		}

		SearchRequest searchRequest = SearchRequest.defaults().withTopK(1).withQuery("LINEMO");
		List<Document> similaritySearch = vectorStore.similaritySearch(searchRequest);
		similaritySearch.stream().forEach(e -> System.out.println(e.getContent()));

	}
}