package lucas.skyblock.mongo.entity.offer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OfferStatus {

    VOIDED("Voided"),
    CREATED("Offer created"),
    IN_WORK("Offer is in work"),
    CANCELLED("Cancelled by user"),
    PARTIALLY_COMPLETED("Offer is partially completed"),
    COMPLETED("Offer is completed");

    private final String verbose;

}
