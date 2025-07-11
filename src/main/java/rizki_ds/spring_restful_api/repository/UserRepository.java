package rizki_ds.spring_restful_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rizki_ds.spring_restful_api.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

	Optional<User> findFirstByToken(String token);
}
