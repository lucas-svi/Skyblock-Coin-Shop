package lucas.skyblock.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import lucas.skyblock.mongo.entity.deposit.Deposit;
import lucas.skyblock.mongo.entity.deposit.DepositStatus;

import java.util.List;

public interface DepositRepository extends MongoRepository<Deposit, String> {

    Deposit findByUuid(String uuid);

    Deposit findByCryptoAddress(String cryptoAddress);

    List<Deposit> findAllByStatus(DepositStatus status);

}
