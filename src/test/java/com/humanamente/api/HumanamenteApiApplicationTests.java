package com.humanamente.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
	webEnvironment = SpringBootTest.WebEnvironment.MOCK,
	properties = {
		"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration"
	}
)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class HumanamenteApiApplicationTests {

	@Test
	void contextLoads() {
		// Test that Spring context loads successfully
	}

}
