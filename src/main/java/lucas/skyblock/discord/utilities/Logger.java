package lucas.skyblock.discord.utilities;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import lucas.skyblock.Skyblock;

import java.awt.*;

public class Logger {

    public static void log(String log, String title, Color color, LogType logType) {
        TextChannel textChannelById = getTextChannel(logType);

        if (textChannelById != null) {
            textChannelById.sendMessageEmbeds(EmbedUtil.embed(log, title, color.getRGB())).queue();
        }
    }

    public static void log(String log, String title, int color, LogType logType) {
        TextChannel textChannelById = getTextChannel(logType);

        if (textChannelById != null) {
            textChannelById.sendMessageEmbeds(EmbedUtil.embed(log, title, color)).queue();
        }
    }

    private static TextChannel getTextChannel(LogType logType) {
        switch (logType) {
            case EXCEPTION:
                return Skyblock.getInstance().getDiscordApplication().getLoggingGuild().getTextChannelById(Skyblock.LOGGING_EXCEPTION_CHANNEL);
            case STARTUP:
                return Skyblock.getInstance().getDiscordApplication().getLoggingGuild().getTextChannelById(Skyblock.LOGGING_STARTUP_CHANNEL);
            case CACHE:
                return Skyblock.getInstance().getDiscordApplication().getLoggingGuild().getTextChannelById(Skyblock.LOGGING_CACHE_CHANNEL);
            case DEPOSIT_LOG:
                return Skyblock.getInstance().getDiscordApplication().getLoggingGuild().getTextChannelById(Skyblock.LOGGING_DEPOSIT_CHANNEL);
            case NEW_OFFERS:
                return Skyblock.getInstance().getDiscordApplication().getLoggingGuild().getTextChannelById(Skyblock.LOGGING_NEW_OFFERS_CHANNEL);
            default:
                return Skyblock.getInstance().getDiscordApplication().getLoggingGuild().getTextChannelById(Skyblock.LOGGING_BLOCkCHAIN_CHANNEL);
        }
    }

    public enum LogType {
        EXCEPTION, CACHE, DEPOSIT_LOG, BLOCKCHAIN_LOG, STARTUP, NEW_OFFERS
    }


}
