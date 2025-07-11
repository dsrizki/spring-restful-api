package rizki_ds.spring_restful_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import jakarta.validation.Validator;
import rizki_ds.spring_restful_api.entity.User;
import rizki_ds.spring_restful_api.model.RegisterUserRequest;
import rizki_ds.spring_restful_api.repository.UserRepository;
import rizki_ds.spring_restful_api.security.BCrypt;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ValidationService validationService;
	
	@Autowired
	private Validator validator;
	
	@Transactional
	public void register(RegisterUserRequest request) {
		validationService.validate(request);
		
		
		if (userRepository.existsById(request.getUsername())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already registered");
		}
		
		User user = new User();
		user.setUsername(request.getUsername());
		user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
		user.setName(request.getName());
		
		userRepository.save(user);
	}
}
