package spring_ai_web_app_example.webapp.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "advisor")
public class Advisor {

	@Id
	private String advisorId;
	private String systemMessage;

	@Version
	private int version;
}
