package spring_ai_web_app_example.webapp.views;

import java.io.InputStream;
import java.util.Optional;

import org.vaadin.lineawesome.LineAwesomeIcon;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.theme.lumo.LumoUtility;

import spring_ai_web_app_example.webapp.data.User;
import spring_ai_web_app_example.webapp.security.AuthenticatedUser;
import spring_ai_web_app_example.webapp.views.chat.ChatView;
import spring_ai_web_app_example.webapp.views.chat_settings.ChatSettingsView;
import spring_ai_web_app_example.webapp.views.image_generator.ImageGeneratorView;
import spring_ai_web_app_example.webapp.views.rag.RAGView;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

	private H1 viewTitle;

	private AuthenticatedUser authenticatedUser;
	private AccessAnnotationChecker accessChecker;

	public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker) {
		this.authenticatedUser = authenticatedUser;
		this.accessChecker = accessChecker;

		setPrimarySection(Section.DRAWER);
		addDrawerContent();
		addHeaderContent();
		// <theme-editor-local-classname>
		addClassName("main-layout-app-layout-1");
	}

	private void addHeaderContent() {
		DrawerToggle toggle = new DrawerToggle();
		toggle.setAriaLabel("Menu toggle");

		viewTitle = new H1();
		viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

		addToNavbar(true, toggle, viewTitle);
	}

	private void addDrawerContent() {
		Span appName = new Span("Spring AI WebApp Example");
		appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);
		Header header = new Header(appName);
		// <theme-editor-local-classname>
		header.addClassName("main-layout-header-1");
		// <theme-editor-local-classname>

		Scroller scroller = new Scroller(createNavigation());
		// <theme-editor-local-classname>
		scroller.addClassName("main-layout-scroller-1");
		// <theme-editor-local-classname>

		addToDrawer(header, scroller, createFooter());
	}

	private SideNav createNavigation() {
		SideNav nav = new SideNav();

		if (accessChecker.hasAccess(ChatView.class)) {
			nav.addItem(new SideNavItem("チャット", ChatView.class, LineAwesomeIcon.SNAPCHAT.create()));

		}
		if (accessChecker.hasAccess(ChatSettingsView.class)) {
			nav.addItem(new SideNavItem("チャット設定", ChatSettingsView.class,
					LineAwesomeIcon.CHALKBOARD_TEACHER_SOLID.create()));
		}
		if (accessChecker.hasAccess(RAGView.class)) {
			nav.addItem(new SideNavItem("RAG", RAGView.class, LineAwesomeIcon.BOOK_OPEN_SOLID.create()));
		}
		if (accessChecker.hasAccess(ImageGeneratorView.class)) {
			nav.addItem(new SideNavItem("画像生成", ImageGeneratorView.class, LineAwesomeIcon.IMAGE_SOLID.create()));
		}
		return nav;
	}

	private Footer createFooter() {
		Footer layout = new Footer();

		Optional<User> maybeUser = authenticatedUser.get();
		if (maybeUser.isPresent()) {
			User user = maybeUser.get();

			InputStream avatorIcon = getClass().getResourceAsStream("/avatar/" + user.getUserId() + ".png");
			if (avatorIcon == null) {
				avatorIcon = getClass().getResourceAsStream("/avatar/default.png");
			}
			final InputStream inputStream = avatorIcon;
			Avatar avatar = new Avatar(user.getName());
			StreamResource resource = new StreamResource("profile-pic", () -> inputStream);
			avatar.setImageResource(resource);
			avatar.setThemeName("xsmall");
			avatar.getElement().setAttribute("tabindex", "-1");

			MenuBar userMenu = new MenuBar();
			userMenu.setThemeName("tertiary-inline contrast");

			MenuItem userName = userMenu.addItem("");
			Div div = new Div();
			div.add(avatar);
			div.add(user.getName());
			div.add(new Icon("lumo", "dropdown"));
			div.getElement().getStyle().set("display", "flex");
			div.getElement().getStyle().set("align-items", "center");
			div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
			userName.add(div);
			userName.getSubMenu().addItem("Sign out", e -> {
				authenticatedUser.logout();
			});

			layout.add(userMenu);
		} else {
			Anchor loginLink = new Anchor("login", "Sign in");
			layout.add(loginLink);
		}

		return layout;
	}

	@Override
	protected void afterNavigation() {
		super.afterNavigation();
		viewTitle.setText(getCurrentPageTitle());
	}

	private String getCurrentPageTitle() {
		PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
		return title == null ? "" : title.value();
	}
}
