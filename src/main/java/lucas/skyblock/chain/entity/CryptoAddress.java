package lucas.skyblock.chain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CryptoAddress {

    private final String address;
    private final float balance;

}
