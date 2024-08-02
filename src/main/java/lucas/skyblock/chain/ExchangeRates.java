package lucas.skyblock.chain;

import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lucas.skyblock.discord.utilities.Logger;
import lucas.skyblock.utility.StringUtility;
import org.apache.http.client.fluent.Request;

import java.awt.*;
import java.math.BigDecimal;

public class ExchangeRates {


    @Getter
    @Setter
    private float BTC, LTC;

    @SneakyThrows
    public void update() {
        setBTC(1f / new info.blockchain.api.exchangerates.ExchangeRates().toBTC("USD", new BigDecimal(1.0F)).floatValue());
        setLTC(JsonParser.parseString(Request.Get("https://cex.io/api/last_price/LTC/USD").execute().returnContent().asString()).getAsJsonObject().get("lprice").getAsFloat());

        Logger.log(String.format("Litecoin/USD: `$%s`\n Bitcoin/USD: `$%s`", StringUtility.fancyNumber(getLTC()), StringUtility.fancyNumber(getBTC())), "Updated exchange rates", Color.RED, Logger.LogType.CACHE);
    }

    @SneakyThrows
    public float convertOneToUSD(IChain network) {
        if (network instanceof Bitcoin) {
            return getBTC();
        } else {
            return getLTC();
        }
    }

    @SneakyThrows
    public BigDecimal convertFromUSD(float amount, IChain network) {
        if (network instanceof Bitcoin) {
            info.blockchain.api.exchangerates.ExchangeRates exchangeRates = new info.blockchain.api.exchangerates.ExchangeRates();
            return exchangeRates.toBTC("USD", new BigDecimal(amount));
        } else {
            return BigDecimal.valueOf(amount / getLTC());
        }
    }

    private enum Singleton {
        INSTANCE;

        private final ExchangeRates value;

        Singleton() {
            this.value = new ExchangeRates();
        }
    }

    public static ExchangeRates getInstance() {
        return ExchangeRates.Singleton.INSTANCE.value;
    }

}