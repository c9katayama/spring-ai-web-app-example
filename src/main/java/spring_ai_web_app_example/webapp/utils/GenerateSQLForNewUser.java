package spring_ai_web_app_example.webapp.utils;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.google.common.collect.Lists;

import spring_ai_web_app_example.webapp.data.Role;

/**
 * Util to generate SQLs for new user
 */
public class GenerateSQLForNewUser {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		System.out.print("input user id(numeric number): ");
		String userid = scanner.nextLine();

		System.out.print("input user name: ");
		String username = scanner.nextLine();

		System.out.print("input display name: ");
		String name = scanner.nextLine();

		System.out.print("input new password: ");
		String password = scanner.nextLine();

		String roleValues = Lists.newArrayList(Role.values()).stream().map(r -> r.name())
				.collect(Collectors.joining(","));
		System.out.print("input user roles [" + roleValues + "]: ");
		String[] roleStrings = scanner.nextLine().split(",");
		List<Role> roles = Lists.newArrayList(roleStrings).stream().map(s -> Role.valueOf(s))
				.collect(Collectors.toList());

		final String hashedPassword = hash(password);
		final String createUserSQL = String.format(
				"insert into application_user (version, id, username,name,hashed_password) values (1, '%s','%s','%s','%s')",
				userid, username, name, hashedPassword);
		System.out.println(createUserSQL);
		for (Role role : roles) {
			String createRoleSQL = String.format("insert into user_roles (user_id, roles) values ('%s', '%s')", userid,
					role.toString());
			System.out.println(createRoleSQL);
		}
	}

	public static String hash(String password) {
		return new BCryptPasswordEncoder().encode(password);
	}
}