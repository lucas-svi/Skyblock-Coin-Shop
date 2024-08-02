package lucas.skyblock.chain;

import lucas.skyblock.chain.entity.CryptoAddress;

public interface IChain {

    boolean isSynchronized();

    String getNewAddress();

    CryptoAddress getBalance(String address, int min_conf);

}
