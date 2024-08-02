package lucas.skyblock.mongo.repository;

import lucas.skyblock.mongo.entity.offer.Offer;
import lucas.skyblock.mongo.entity.offer.OfferStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OfferRepository extends MongoRepository<Offer, String> {

    Offer findByUuid(String uuid);

    List<Offer> findAllByInitiator(String id);

    List<Offer> findAllByWorker(String id);

    List<Offer> findAllByStatus(OfferStatus status);

}
