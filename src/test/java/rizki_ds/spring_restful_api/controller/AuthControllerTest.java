package rizki_ds.spring_restful_api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import rizki_ds.spring_restful_api.entity.User;
import rizki_ds.spring_restful_api.model.LoginUserRequest;
import rizki_ds.spring_restful_api.model.TokenResponse;
import rizki_ds.spring_restful_api.model.WebResponse;
import rizki_ds.spring_restful_api.repository.UserRepository;
import rizki_ds.spring_restful_api.security.BCrypt;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
	
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
	void loginFailedUserNotFound() throws Exception {
		LoginUserRequest request = new LoginUserRequest();
		request.setUsername("test");
		request.setPassword("test");
		
		mockMvc.perform(
			post("/api/auth/login")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		).andExpectAll(status().isUnauthorized()
		).andDo(result -> {
			WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});

			assertNotNull(response.getCode());
			assertNotNull(response.getMessage());
			
			assertEquals(response.getCode(), HttpStatus.UNAUTHORIZED.value());
			assertEquals(response.getMessage(), "Username or password wrong");
		});
	}
	
	@Test
	void loginFailedWrongPassword() throws Exception {
		User user = new User();
		user.setName("Test");
		user.setUsername("test");
		user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
		userRepository.save(user);

		LoginUserRequest request = new LoginUserRequest();
		request.setUsername("test");
		request.setPassword("salah");
		
		mockMvc.perform(post("/api/auth/login")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		).andExpectAll(status().isUnauthorized()
		).andDo(result -> {
			WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
			
			assertNotNull(response.getCode());
			assertNotNull(response.getMessage());
			
			assertEquals(response.getCode(), HttpStatus.UNAUTHORIZED.value());
			assertEquals(response.getMessage(), "Username or password wrong");
		});
	}
	
	@Test
	void loginSuccess() throws Exception {
		User user = new User();
		user.setName("Test");
		user.setUsername("test");
		user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
		userRepository.save(user);

		LoginUserRequest request = new LoginUserRequest();
		request.setUsername("test");
		request.setPassword("test");
		
		mockMvc.perform(post("/api/auth/login")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		).andExpectAll(status().isOk()
		).andDo(result -> {
			WebResponse<TokenResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
			
			assertNotNull(response.getCode());
			assertNotNull(response.getMessage());
			assertNotNull(response.getData());
			assertNotNull(response.getData().getToken());
			assertNotNull(response.getData().getExpiredAt());
			
			assertEquals(response.getCode(), HttpStatus.OK.value());
			assertEquals(response.getMessage(), "Login success");
			
			User userDb = userRepository.findById("test").orElse(null);
			assertNotNull(userDb);
			assertEquals(userDb.getToken(), response.getData().getToken());
			assertEquals(userDb.getTokenExpiredAt(), response.getData().getExpiredAt());
		});
	}
	
	@Test
	void logoutFailed() throws Exception {
		mockMvc.perform(delete("/api/auth/logout")
			.accept(MediaType.APPLICATION_JSON)
		).andExpectAll(status().isUnauthorized()
		).andDo(result -> {
			WebResponse<TokenResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
			
			assertNotNull(response.getCode());
			assertNotNull(response.getMessage());
			
			assertEquals(response.getCode(), HttpStatus.UNAUTHORIZED.value());
			assertEquals(response.getMessage(), "Unauthenticated");
		});
	}
	
	@Test
	void logoutSuccess() throws Exception {
		User user = new User();
		user.setName("test");
		user.setUsername("test");
		user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
		user.setToken("test");
		user.setTokenExpiredAt(BigInteger.valueOf(System.currentTimeMillis() + 1_000_000L));
		userRepository.save(user);

		mockMvc.perform(delete("/api/auth/logout")
			.accept(MediaType.APPLICATION_JSON)
			.header("X-Api-Key", "test")
		).andExpectAll(status().isOk()
		).andDo(result -> {
			WebResponse<TokenResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
			
			assertNotNull(response.getCode());
			assertNotNull(response.getMessage());
			
			assertEquals(response.getCode(), HttpStatus.OK.value());
			assertEquals(response.getMessage(), "Logout success");
			
			User userDb = userRepository.findById("test").orElse(null);
			assertNotNull(userDb);
			assertNull(userDb.getToken());
			assertNull(userDb.getTokenExpiredAt());
		});
	}
}
