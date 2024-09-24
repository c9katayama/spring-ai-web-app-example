package spring_ai_web_app_example.webapp.views.chat_settings;

import java.util.Optional;

import org.springframework.util.StringUtils;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;

import jakarta.annotation.security.RolesAllowed;
import spring_ai_web_app_example.webapp.data.Advisor;
import spring_ai_web_app_example.webapp.data.AdvisorRepository;
import spring_ai_web_app_example.webapp.security.AuthenticatedUser;
import spring_ai_web_app_example.webapp.views.MainLayout;

@PageTitle("チャット設定")
@Route(value = "advisor_settings", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class ChatSettingsView extends Composite<VerticalLayout> {

	private AdvisorRepository advisorRepository;

	public ChatSettingsView(AuthenticatedUser authenticatedUser, AdvisorRepository advisorRepository) {
		this.advisorRepository = advisorRepository;
		getContent().setWidth("100%");
		getContent().getStyle().set("flex-grow", "1");
		getContent().setJustifyContentMode(JustifyContentMode.START);
		getContent().setAlignItems(Alignment.CENTER);

		VerticalLayout layoutColumn2 = new VerticalLayout();
		layoutColumn2.setWidthFull();
		getContent().setFlexGrow(1.0, layoutColumn2);
		layoutColumn2.setWidth("100%");
		layoutColumn2.setMaxWidth("800px");
		layoutColumn2.setHeight("min-content");

		TextArea textField1 = new TextArea();

		textField1.setLabel("System Prompt設定");
		textField1.setWidthFull();

		// ボタン
		HorizontalLayout layoutRow = new HorizontalLayout();
		layoutRow.setWidthFull();
		layoutColumn2.setFlexGrow(1.0, layoutRow);
		layoutRow.addClassName(Gap.MEDIUM);
		layoutRow.setWidth("100%");
		layoutRow.setHeight("min-content");
		Button buttonPrimary = new Button();
		buttonPrimary.setText("Save");
		buttonPrimary.setWidth("min-content");
		buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		getContent().add(layoutColumn2);
		layoutColumn2.add(textField1);
		layoutColumn2.add(layoutRow);
		layoutRow.add(buttonPrimary);

		fill("SystemPrompt", textField1);

		buttonPrimary.addClickListener(e -> {
			save("SystemPrompt", textField1.getValue());
			Notification.show("saved.", 3000, Position.TOP_CENTER);
		});
	}

	private void fill(String id, TextArea textArea) {
		Optional<Advisor> advisor = advisorRepository.findById(id);
		if (advisor.isPresent()) {
			textArea.setValue(advisor.get().getSystemMessage());
		}
	}

	private void save(String advisorId, String systemMessage) {
		if (StringUtils.hasLength(systemMessage)) {
			Advisor advisorForSave;
			Optional<Advisor> advisor = advisorRepository.findById(advisorId);
			if (advisor.isPresent()) {
				advisorForSave = advisor.get();
			} else {
				advisorForSave = new Advisor();
			}
			advisorForSave.setAdvisorId(advisorId);
			advisorForSave.setSystemMessage(systemMessage);
			advisorRepository.save(advisorForSave);
		} else {
			advisorRepository.deleteById(advisorId);
		}
	}
}
