package rizki_ds.spring_restful_api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
import rizki_ds.spring_restful_api.model.UpdateUserRequest;
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
	
	@Test
	void updateUserUnauthorized() throws Exception {
		UpdateUserRequest request = new UpdateUserRequest();
		
		
		mockMvc.perform(
			patch("/api/users/current")
				.accept(MediaType.APPLICATION_JSON)
				.header("X-Api-Key", "notfound")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
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
	void updateUserSuccess() throws Exception {
		User user = new User();
		user.setName("test");
		user.setUsername("test");
		user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
		user.setToken("test");
		user.setTokenExpiredAt(BigInteger.valueOf(System.currentTimeMillis() + 1_000_000L));
		userRepository.save(user);

		UpdateUserRequest request = new UpdateUserRequest();
		request.setName("name updated");
		request.setPassword("password updated");
		
		
		mockMvc.perform(
			patch("/api/users/current")
				.accept(MediaType.APPLICATION_JSON)
				.header("X-Api-Key", "test")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		).andExpectAll(status().isOk()
		).andDo(result -> {
			WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
					
			assertNotNull(response.getCode());
			assertEquals(response.getCode(), HttpStatus.OK.value());

			assertNotNull(response.getMessage());
			assertEquals(response.getMessage(), "User successfully updated");

			assertNotNull(response.getData());			
			assertNotNull(response.getData().getName());
			assertEquals(response.getData().getName(), "name updated");
			assertNotNull(response.getData().getUsername());
			assertEquals(response.getData().getUsername(), user.getUsername());
			
			User userDb = userRepository.findById("test").orElse(null);
			assertEquals("name updated", userDb.getName());
			assertNotNull(userDb);
			assertTrue(BCrypt.checkpw("password updated", userDb.getPassword()));
			
		});	
	}
}
