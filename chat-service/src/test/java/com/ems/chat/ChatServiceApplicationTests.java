package com.ems.chat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.ai.openai.api-key=test-key",
		"spring.ai.openai.chat.options.model=gpt-4o-mini"
})
class ChatServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
