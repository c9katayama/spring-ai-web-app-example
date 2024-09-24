package spring_ai_web_app_example.webapp.views.rag;

import java.io.InputStream;
import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;
import spring_ai_web_app_example.common.CommonConfig;
import spring_ai_web_app_example.webapp.views.MainLayout;

@PageTitle("RAG")
@Route(value = "rag", layout = MainLayout.class)
@PermitAll
public class RAGView extends VerticalLayout {

	private Upload upload;

	VectorStore vectorStore;

	public RAGView(VectorStore vectorStore) {
		this.vectorStore = vectorStore;
		setMargin(true);
		Div label = new Div("VectorStoreに追加するPDFをアップロード");
		// Upload部分
		MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
		upload = new Upload(buffer);
		// PDFファイルのみupload可能
		upload.setAcceptedFileTypes("application/pdf", ".pdf");
		upload.setMaxFileSize(Integer.MAX_VALUE);
		upload.addSucceededListener(event -> {
			String fileName = event.getFileName();
			InputStream inputStream = buffer.getInputStream(fileName);
			addContect(inputStream);
		});
		add(label, upload);
	}

	private void addContect(InputStream is) {
		try {
			Resource resource = new InputStreamResource(is);
			PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(resource,
					PdfDocumentReaderConfig.builder().build());
			List<Document> docs = pdfReader.read();
			vectorStore.add(docs);

			if (vectorStore instanceof SimpleVectorStore) {
				((SimpleVectorStore) vectorStore).save(CommonConfig.VECTOR_STORE_FILE);
			}
		} catch (Exception e) {
			Notification.show(e.getMessage(), 300, Position.TOP_CENTER);
		}
	}
}
