package lucas.skyblock.mongo.entity.user;

import lombok.Getter;

public enum Role {

    BANNED("Banned"),
    EVERYONE("User"),
    CUSTOMER("Customer/Buyer"),
    PARTNER("Partner / Coin dealer"),
    STAFF("Staff member"),
    ADMIN("Administrator");

    @Getter
    private final String fancyName;

    Role(String fancyName){
        this.fancyName = fancyName;
    }
    
}
