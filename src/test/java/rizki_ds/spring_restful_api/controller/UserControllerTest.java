package rizki_ds.spring_restful_api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import rizki_ds.spring_restful_api.entity.User;
import rizki_ds.spring_restful_api.model.RegisterUserRequest;
import rizki_ds.spring_restful_api.model.WebResponse;
import rizki_ds.spring_restful_api.repository.UserRepository;
import rizki_ds.spring_restful_api.security.BCrypt;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@BeforeEach
	void setUp() {
		userRepository.deleteAll();
	}
	
	@Test
	void testRegisterSuccess() throws Exception {
		RegisterUserRequest request = new RegisterUserRequest();
		request.setUsername("test");
		request.setPassword("rahasia");
		request.setName("Test");
		
		mockMvc.perform(
			post("/api/users")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		).andExpectAll(
			status().isOk()
		).andDo(result -> {
			WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
					
			assertEquals("OK", response.getData());
		});
	}
	
	@Test
	void testRegisterBadRequest() throws Exception {
		RegisterUserRequest request = new RegisterUserRequest();
		request.setUsername("");
		request.setPassword("");
		request.setName("");
		
		mockMvc.perform(
			post("/api/users")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		).andExpectAll(
			status().isBadRequest()
		).andDo(result -> {
			WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
					
			assertNotNull(response.getErrors());
		});
	}
	
	@Test
	void testRegisterDuplicate() throws Exception {
		User user = new User();
		user.setUsername("test");
		user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
		user.setName("Test");
		userRepository.save(user);
		
		RegisterUserRequest request = new RegisterUserRequest();
		request.setUsername("");
		request.setPassword("");
		request.setName("");
		request.setUsername("test");
		request.setPassword("rahasia");
		request.setName("Test");
		
		mockMvc.perform(
			post("/api/users")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		).andExpectAll(
			status().isBadRequest()
		).andDo(result -> {
			WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
					
			assertNotNull(response.getErrors());
		});
	}
}
