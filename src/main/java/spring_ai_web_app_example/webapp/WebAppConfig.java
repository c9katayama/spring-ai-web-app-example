package spring_ai_web_app_example.webapp;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.sql.init.SqlDataSourceScriptDatabaseInitializer;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import spring_ai_web_app_example.webapp.data.UserRepository;

@Configuration
public class WebAppConfig {

	@Bean
	SqlDataSourceScriptDatabaseInitializer dataSourceScriptDatabaseInitializer(DataSource dataSource,
			SqlInitializationProperties properties, UserRepository repository) {
		// This bean ensures the database is only initialized when empty
		return new SqlDataSourceScriptDatabaseInitializer(dataSource, properties) {
			@Override
			public boolean initializeDatabase() {
				if (repository.count() == 0L) {
					return super.initializeDatabase();
				}
				return false;
			}
		};
	}
}
