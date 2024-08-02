package lucas.skyblock.mongo.entity.deposit;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum DepositStatus {

    ONGOING_NOT_PAID("Ongoing, waiting for payment"),
    ONGOING_PAID_NOT_CONFIRMED("Ongoing, payment received but it is unconfirmed"),
    COMPLETED("Deposit completed"),
    CANCELLED_BY_USER("Deposit was cancelled by user"),
    EXPIRED("Deposit Expired / Was not paid in time");

    @Getter
    private final String fancyName;

}
