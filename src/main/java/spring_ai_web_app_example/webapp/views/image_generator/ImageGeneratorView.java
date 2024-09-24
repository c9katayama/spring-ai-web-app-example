package spring_ai_web_app_example.webapp.views.image_generator;

import org.springframework.ai.image.Image;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImageOptionsBuilder;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;

import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.ListStyleType;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.MaxWidth;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;

import jakarta.annotation.security.PermitAll;
import spring_ai_web_app_example.webapp.views.MainLayout;

@PageTitle("画像生成")
@Route(value = "image-generator", layout = MainLayout.class)
@PermitAll
public class ImageGeneratorView extends Main implements HasComponents, HasStyle {

	private OrderedList imageContainer;
	private final ImageModel imageModel;

	public ImageGeneratorView(ImageModel imageModel) {
		this.imageModel = imageModel;
		constructUI();
	}

	private void constructUI() {
		// スタイル
		addClassNames("image-gallery-view");
		addClassNames(MaxWidth.SCREEN_LARGE, Margin.Horizontal.AUTO, Padding.Bottom.LARGE, Padding.Horizontal.LARGE);
		// 全体のレイアウト
		HorizontalLayout container = new HorizontalLayout();
		container.addClassNames(AlignItems.CENTER, JustifyContent.BETWEEN);

		// 入力レイアウト
		VerticalLayout inputContainer = new VerticalLayout();

		// プロンプト入力
		TextArea promptInputTextField = new TextArea("", "prompt...");
		promptInputTextField.setWidthFull();

		// 生成ボタン
		Button button = new Button("生成");
		button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		// クリックイベント
		button.addClickListener(click -> {
			String prompt = promptInputTextField.getValue();
			generateImage(prompt);
		});
		inputContainer.add(promptInputTextField, button);

		// 画像表示用グリッド
		imageContainer = new OrderedList();
		imageContainer.addClassNames(Gap.MEDIUM, Display.GRID, ListStyleType.NONE, Margin.NONE, Padding.NONE);

		container.add(inputContainer);
		add(container, imageContainer);
	}

	private void generateImage(String prompt) {
		final int imageWidth = 512;
		final int imageHeight = 512;
		final int numOfImage = 1;
		// 画像生成API呼び出し
		ImagePrompt imagePrompt = new ImagePrompt(prompt,
				ImageOptionsBuilder.builder().withN(numOfImage).withHeight(imageHeight).withWidth(imageWidth).build());
		try {
			ImageResponse call = imageModel.call(imagePrompt);
			call.getResults().forEach(e -> {
				// 画像をグリッドで表示
				Image image = e.getOutput();
				imageContainer.add(new ImageViewCard(prompt, image.getUrl()));
			});
		} catch (Exception e) {
			Notification.show(e.getMessage(), 3000, Notification.Position.MIDDLE);
		}
	}
}
