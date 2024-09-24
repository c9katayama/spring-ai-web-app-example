package spring_ai_web_app_example.webapp.views.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.util.StringUtils;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import spring_ai_web_app_example.webapp.data.Advisor;
import spring_ai_web_app_example.webapp.data.AdvisorRepository;
import spring_ai_web_app_example.webapp.views.MainLayout;

@PageTitle("チャット")
@Route(value = "", layout = MainLayout.class)
@PermitAll
@Slf4j
public class ChatView extends VerticalLayout {

	private final ChatModel chatModel;
	private final AdvisorRepository advisorRepository;
	private final VectorStore vectorStore;

	private RadioButtonGroup<String> regOnOffButton;
	private List<Message> chatHistory = new ArrayList<>();

	private final int maxToken = 8000;
	private final int maxChatHistoryNum = 4;

	public ChatView(ChatModel chatModel, AdvisorRepository advisorRepository, VectorStore vectorStore) {
		this.chatModel = chatModel;
		this.advisorRepository = advisorRepository;
		this.vectorStore = vectorStore;
		setMaxWidth("800px");
		setWidthFull();
		setHeightFull();
		setMargin(true);

		// RAG利用有無のRadioButton
		regOnOffButton = new RadioButtonGroup<>();
		regOnOffButton.setLabel("RAG利用");
		regOnOffButton.setItems("ON", "OFF");
		regOnOffButton.setValue("OFF");
		regOnOffButton.setRequired(true);
		add(regOnOffButton);
		// prompt入力追加
		addInputUI();
	}

	// 入力画面追加
	private void addInputUI() {

		TextArea prompt = new TextArea();
		prompt.setPrefixComponent(new Icon(VaadinIcon.QUESTION));

		Button button = new Button("Ask AI");
		button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		Div buttonWrapp = new Div(button);

		FormLayout formLayout = new FormLayout();
		formLayout.setWidth("100%");
		formLayout.setColspan(prompt, 2);
		formLayout.add(prompt, buttonWrapp);

		button.addClickListener(c -> {
			String input = prompt.getValue();
			if (StringUtils.hasLength(input)) {
				prompt.setReadOnly(true);
				buttonWrapp.remove(button);
				ProgressBar progressBar = new ProgressBar();
				progressBar.setIndeterminate(true);
				buttonWrapp.add(progressBar);
				addResultAndPromptInputAsync(input, () -> buttonWrapp.remove(progressBar));
			}
		});
		this.add(formLayout);
	}

	private Message createSystemMessage(String userInput) {
		StringBuilder systemMessageText = new StringBuilder();

		// 事前に定義したシステムメッセージ
		Optional<Advisor> advisor = advisorRepository.findById("SystemPrompt");
		if (advisor.isPresent()) {
			systemMessageText.append(advisor.get().getSystemMessage());
		}
		final String responseStyle = "\n\n回答は必ずdivタグからはじまるHTML形式で整形する。\n返答は必ず" + maxToken + "以下にする。";
		systemMessageText.append(responseStyle);

		Message systemMessage = new SystemMessage(systemMessageText.toString());
		log.info("systemMessage:" + systemMessage.getContent());
		return systemMessage;
	}

	private Message createUserMessage(String userInput) {
		// RAGの利用
		if (regOnOffButton.getValue().equals("ON")) {
			// 近似0.8,上位3つをRAG用に追加
			SearchRequest searchRequest = SearchRequest.query(userInput).withSimilarityThreshold(0.8).withTopK(3);
			List<Document> searchResult = vectorStore.similaritySearch(searchRequest);
			if (searchResult.size() > 0) {
				String rag = "\n\n回答には、以下の情報を加味して回答してください。\n"
						+ searchResult.stream().map(d -> d.getContent()).collect(Collectors.joining("\n"));
				// RAGをUserMessageに追加
				userInput += rag;
			}
		}
		Message userMessageText = new UserMessage(userInput);
		return userMessageText;
	}

	private void addResultAndPromptInputAsync(String userInput, Runnable callback) {

		Message userMessage = createUserMessage(userInput);

		log.info("call AI " + userMessage.getContent());

		final StringBuffer contentHtml = new StringBuffer();
		final Div output = new Div();
		output.getStyle().set("width", "auto");
		output.getStyle().set("height", "auto");
		this.add(output);
		final UI ui = UI.getCurrent();

		// AIを非同期で呼び出し
		Flux<ChatResponse> result = callAIAsync(userMessage);
		result.subscribe((t) -> {
			// APIレスポンスの画面表示
			String resultHtml = t.getResults().stream().map(s -> s.getOutput().getContent())
					.collect(Collectors.joining("\n"));
			contentHtml.append(resultHtml);
			ui.access(() -> {
				output.getElement().setProperty("innerHTML", "<html>" + contentHtml.toString() + "</html>");
			});
		}, (error) -> {
			// エラー表示
			Notification.show(error.getMessage(), 3000, Position.TOP_CENTER);
		}, () -> {
			// 非同期処理完了
			ui.access(() -> {
				if (callback != null) {
					callback.run();
				}
				// 結果を履歴に追加
				chatHistory.add(new AssistantMessage(contentHtml.toString()));
				log.info("response:" + contentHtml.toString());
				// 次の入力画面表示
				addInputUI();
			});
		});
	}

	private Flux<ChatResponse> callAIAsync(Message userMessage) {
		chatHistory.add(userMessage);
		// SystemMessageと、直近の履歴をmaxChatHistoryNumまでいれてリクエスト
		List<Message> requestMessageList = new ArrayList<>();
		requestMessageList.add(createSystemMessage(userMessage.getContent()));
		int listSize = chatHistory.size();
		requestMessageList.addAll(chatHistory.subList(Math.max(listSize - maxChatHistoryNum, 0), listSize));

		log.info("request:" + requestMessageList.stream().map(s -> s.getContent()).collect(Collectors.joining("\n")));
		return chatModel.stream(new Prompt(chatHistory));
	}
}
