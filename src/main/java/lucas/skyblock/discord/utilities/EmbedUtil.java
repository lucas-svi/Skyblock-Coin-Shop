package lucas.skyblock.discord.utilities;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.time.Instant;

public class EmbedUtil {

    public static MessageEmbed embed(String message, String title, int color) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setDescription(message);
        if (title != null)
            embedBuilder.setTitle(title);
        embedBuilder.setColor(color);

        embedBuilder.setAuthor("Hypixel Coin Market", "https://coins.novoline.lol", "https://novoline.lol/assets/skyblockcoins.png");
        embedBuilder.setTimestamp(Instant.now());
        embedBuilder.setFooter("Total coins sold: 0", "https://novoline.lol/assets/coins.png");
        embedBuilder.setThumbnail("https://novoline.lol/assets/skyblockcoins_empty.png");
        return embedBuilder.build();
    }

}