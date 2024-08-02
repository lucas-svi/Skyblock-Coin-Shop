package lucas.skyblock.chain;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import lucas.skyblock.chain.entity.CryptoAddress;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

public class Bitcoin implements IChain{

    private static final String RPC_ENDPOINT = "http://lucas:a5kAfkb@127.0.0.1:8332/";

    @SneakyThrows
    public boolean isSynchronized(){
        try {
            JsonObject result = JsonParser.parseString(Request.Post(RPC_ENDPOINT)
                    .addHeader("Content-Type", "text/plain")
                    .body(
                            new StringEntity(
                                    "{\"jsonrpc\": \"1.0\", \"id\": \"curltest\", \"method\": \"getblockchaininfo\", \"params\": []}",
                                    ContentType.APPLICATION_JSON
                            )
                    )
                    .execute()
                    .returnContent().asString()
            ).getAsJsonObject().get("result").getAsJsonObject();

            if(result == null){
                return false;
            }

            return result.get("blocks").getAsInt() == result.get("headers").getAsInt();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @SneakyThrows
    public String getNewAddress() {
        return JsonParser.parseString(Request.Post(RPC_ENDPOINT)
                .addHeader("Content-Type", "text/plain")
                .body(
                        new StringEntity(
                                "{\"jsonrpc\": \"1.0\", \"id\": \"curltest\", \"method\": \"getnewaddress\", \"params\": []}",
                                ContentType.APPLICATION_JSON
                        )
                )
                .execute()
                .returnContent().asString()
        ).getAsJsonObject().get("result").getAsString();
    }
    @SneakyThrows
    public CryptoAddress getBalance(String address, int min_conf){
        StringEntity requestEntity = new StringEntity(
                String.format("{\"jsonrpc\": \"1.0\", \"id\": \"curltest\", \"method\": \"getreceivedbyaddress\", \"params\": [\"%s\", %s]}", address, min_conf),
                ContentType.APPLICATION_JSON);

        Response response = Request.Post(RPC_ENDPOINT)
                .addHeader("Content-Type", "text/plain")
                .body(requestEntity)
                .execute();

        float balance = JsonParser.parseString(response.returnContent().asString()).getAsJsonObject().get("result").getAsFloat();

        return new CryptoAddress(address, balance);
    }
}
