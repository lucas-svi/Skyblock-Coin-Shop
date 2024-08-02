package lucas.skyblock.mongo.entity.offer;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "offers")
@Builder
@Getter
@Setter
public class Offer {

    @Id
    private String id;

    private String uuid;
    private String initiator;
    private String broadcastedMessageID, ticketChannelID;
    private double coinAmount, coinAmountInCredits;

    private OfferStatus status;

    private String worker;

}
