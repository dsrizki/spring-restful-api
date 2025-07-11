package rizki_ds.spring_restful_api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import rizki_ds.spring_restful_api.model.RegisterUserRequest;
import rizki_ds.spring_restful_api.model.UserResponse;
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
			WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
					
//			assertEquals("OK", response.getData());			
			assertNotNull(response.getCode());
			assertNotNull(response.getMessage());
			assertNotNull(response.getData());

			assertEquals(response.getCode(), HttpStatus.CREATED.value());
			assertEquals(response.getMessage(), "User successfully registered");
			assertEquals(response.getData().getName(), request.getName());
			assertEquals(response.getData().getUsername(), request.getUsername());
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
					
			assertNotNull(response.getCode());
			assertNotNull(response.getMessage());
			
			assertEquals(response.getCode(), HttpStatus.BAD_REQUEST.value());
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
					
			assertNotNull(response.getCode());
			assertNotNull(response.getMessage());
			
			assertEquals(response.getCode(), HttpStatus.BAD_REQUEST.value());
			assertEquals(response.getMessage(), "Username already registered");
		});
	}
	
	@Test
	void getUserUnauthorized() throws Exception {
		mockMvc.perform(
			get("/api/users/current")
				.accept(MediaType.APPLICATION_JSON)
				.header("X-Api-Key", "notfound")
		).andExpectAll(status().isUnauthorized()
		).andDo(result -> {
			WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
					
			assertNotNull(response.getCode());
			assertNotNull(response.getMessage());
			
			assertEquals(response.getCode(), HttpStatus.UNAUTHORIZED.value());
			assertEquals(response.getMessage(), "Unauthenticated");
		});	
	}
	
	@Test
	void getUserUnauthorizedTokenNotSent() throws Exception {
		mockMvc.perform(
			get("/api/users/current")
				.accept(MediaType.APPLICATION_JSON)
		).andExpectAll(status().isUnauthorized()
		).andDo(result -> {
			WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
					
			assertNotNull(response.getCode());
			assertNotNull(response.getMessage());
			
			assertEquals(response.getCode(), HttpStatus.UNAUTHORIZED.value());
			assertEquals(response.getMessage(), "Unauthenticated");
		});	
	}
	
	@Test
	void getUserSuccess() throws Exception {
		User user = new User();
		user.setName("test");
		user.setUsername("test");
		user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
		user.setToken("test");
		user.setTokenExpiredAt(BigInteger.valueOf(System.currentTimeMillis() + 1_000_000L));
		userRepository.save(user);
		
		mockMvc.perform(
			get("/api/users/current")
				.accept(MediaType.APPLICATION_JSON)
				.header("X-Api-Key", "test")
		).andExpectAll(status().isOk()
		).andDo(result -> {
			WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
					
			assertNotNull(response.getCode());
			assertNotNull(response.getMessage());
			assertNotNull(response.getData());
			
			assertEquals(response.getCode(), HttpStatus.OK.value());
			assertEquals(response.getMessage(), "Current user");
			assertEquals(response.getData().getName(), "test");
			assertEquals(response.getData().getUsername(), "test");
		});	
	}
}
