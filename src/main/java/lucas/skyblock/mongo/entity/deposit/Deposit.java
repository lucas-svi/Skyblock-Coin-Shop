package lucas.skyblock.mongo.entity.deposit;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lucas.skyblock.chain.IChain;

@Document(collection = "deposits")
@Getter
@Setter
@Builder
public class Deposit {

    @Id
    private String id;

    private String uuid;
    private String initiator;
    private String cryptoAddress;

    private DepositStatus status;

    private IChain gateway;

    private double totalReceived;
    private long expiresAt;

}
