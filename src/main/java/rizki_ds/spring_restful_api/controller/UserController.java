package rizki_ds.spring_restful_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import rizki_ds.spring_restful_api.entity.User;
import rizki_ds.spring_restful_api.model.RegisterUserRequest;
import rizki_ds.spring_restful_api.model.UpdateUserRequest;
import rizki_ds.spring_restful_api.model.UserResponse;
import rizki_ds.spring_restful_api.model.WebResponse;
import rizki_ds.spring_restful_api.service.UserService;

@RestController
public class UserController {

	@Autowired
	private UserService userService;
	
	@PostMapping(
		path = "/api/users",
		consumes = MediaType.APPLICATION_JSON_VALUE,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public WebResponse<UserResponse> register(@RequestBody RegisterUserRequest request) {
		UserResponse userResponse = userService.register(request);

		return WebResponse.<UserResponse>builder()
			.code(HttpStatus.CREATED.value())
			.message("User successfully registered")
			.data(userResponse)
			.build();
	}
	
	@GetMapping(
		path = "/api/users/current",
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public WebResponse<UserResponse> get(User user) {
 		UserResponse userResponse = userService.get(user);
 		
 		return WebResponse.<UserResponse>builder()
			.code(HttpStatus.OK.value())
			.message("Current user")
			.data(userResponse)
			.build();
	}
	
	@PatchMapping(
		path = "/api/users/current",
		consumes = MediaType.APPLICATION_JSON_VALUE,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public WebResponse<UserResponse> update(User user, @RequestBody UpdateUserRequest request) {
		UserResponse userResponse = userService.update(user, request);
		
		return WebResponse.<UserResponse>builder()
			.code(HttpStatus.OK.value())
			.message("User successfully updated")
			.data(userResponse)
			.build();
	}
}
