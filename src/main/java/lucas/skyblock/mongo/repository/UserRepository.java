package lucas.skyblock.mongo.repository;

import lucas.skyblock.mongo.entity.user.Role;
import lucas.skyblock.mongo.entity.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {

    User findByDisordID(String id);

    List<User> findAllByRole(Role role);

}
