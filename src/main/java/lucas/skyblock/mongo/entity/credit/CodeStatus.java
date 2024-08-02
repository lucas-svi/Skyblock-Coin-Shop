package lucas.skyblock.mongo.entity.credit;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CodeStatus {

    REDEEMABLE("Redeemable"),
    REDEEMED("Redeemed");

    private String fancyName;

}
