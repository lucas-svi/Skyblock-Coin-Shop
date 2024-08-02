package lucas.skyblock.mongo.entity.credit;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "credit-codes")
@Getter
@Setter
@Builder
public class CreditCode {

    @Id
    private String id;

    private String code;
    private String redeemedBy;

    private CodeStatus status;

    private long redeemedOn;
    private double credits;

    @Override
    public String toString() {
        return "CreditCode{" +
                "code='" + code + '\'' +
                ", redeemedBy='" + redeemedBy + '\'' +
                ", status=" + status +
                ", redeemedOn=" + redeemedOn +
                ", credits=" + credits +
                '}';
    }
}
