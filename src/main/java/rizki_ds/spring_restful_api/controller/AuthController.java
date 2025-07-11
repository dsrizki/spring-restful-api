package rizki_ds.spring_restful_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import rizki_ds.spring_restful_api.model.LoginUserRequest;
import rizki_ds.spring_restful_api.model.TokenResponse;
import rizki_ds.spring_restful_api.model.WebResponse;
import rizki_ds.spring_restful_api.service.AuthService;

@RestController
public class AuthController {
	
	@Autowired
	private AuthService authService;
	
	@PostMapping(
		path = "/api/auth/login",
		consumes = MediaType.APPLICATION_JSON_VALUE,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public WebResponse<TokenResponse> login(@RequestBody LoginUserRequest request) {
		TokenResponse tokenResponse = authService.login(request);
		
		return WebResponse.<TokenResponse>builder()
				.code(HttpStatus.OK.value())
				.message("Login success")
				.data(tokenResponse)
				.build();
	}

}
