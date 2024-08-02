package lucas.skyblock.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import lucas.skyblock.mongo.entity.credit.CreditCode;

import java.util.List;

public interface CreditCodeRepository extends MongoRepository<CreditCode, String> {

    CreditCode findByCode(String code);

    List<CreditCode> findAllByCode(String code);

}
