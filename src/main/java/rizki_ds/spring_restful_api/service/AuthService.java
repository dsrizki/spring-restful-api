package rizki_ds.spring_restful_api.service;

import java.math.BigInteger;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import rizki_ds.spring_restful_api.entity.User;
import rizki_ds.spring_restful_api.model.LoginUserRequest;
import rizki_ds.spring_restful_api.model.TokenResponse;
import rizki_ds.spring_restful_api.repository.UserRepository;
import rizki_ds.spring_restful_api.security.BCrypt;

@Service
public class AuthService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ValidationService validationService;
	
	@Transactional
	public TokenResponse login(LoginUserRequest request) {
		validationService.validate(request);
		
		User user = userRepository.findById(request.getUsername())
			.orElseThrow(() ->  new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or password wrong"));
		
		if (BCrypt.checkpw(request.getPassword(), user.getPassword())) {
			user.setToken(UUID.randomUUID().toString());
			// set 30 hari
			user.setTokenExpiredAt(next30Days());			
			userRepository.save(user);
			
			return TokenResponse.builder()
					.token(user.getToken())
					.expiredAt(user.getTokenExpiredAt())
					.build();
		} else {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or password wrong");
		}
	}
	  
	private BigInteger next30Days() {
		return BigInteger.valueOf(System.currentTimeMillis() + (1000 * 60 * 24 * 30));
	}
	
	@Transactional
	public void logout(User user) {
		user.setToken(null);
		user.setTokenExpiredAt(null);
		
		userRepository.save(user);
	}
}
