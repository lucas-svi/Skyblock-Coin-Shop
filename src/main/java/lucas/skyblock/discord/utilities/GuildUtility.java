package lucas.skyblock.discord.utilities;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;

public class GuildUtility {

    public static void sendPrivateMessageEmbed(Guild guild, String id, MessageEmbed embed) {
        guild.loadMembers(member -> {
            if (member.getUser().getId().equals(id)) {
                member.getUser().openPrivateChannel().queue(success -> success.sendMessageEmbeds(embed).queue());
            }
        });
    }

    public static void sendPrivateMessageEmbedComponent(Guild guild, String id, MessageEmbed embed, LayoutComponent... components) {
        guild.loadMembers(member -> {
            if (member.getUser().getId().equals(id)) {
                member.getUser().openPrivateChannel().queue(success -> success.sendMessageEmbeds(embed).addComponents(components).queue());
            }
        });
    }


    public static void sendPrivateMessage(Guild guild, String id, String message) {
        guild.loadMembers(member -> {
            if (member.getUser().getId().equals(id)) {
                member.getUser().openPrivateChannel().queue(success -> success.sendMessage(message).queue());
            }
        });
    }

    public static void file_sendPrivateMessageEmbed(Guild guild, String id, MessageEmbed embed, File file) {
        guild.loadMembers(member -> {
            if (member.getUser().getId().equals(id)) {
                member.getUser().openPrivateChannel().queue(success -> success.sendMessageEmbeds(embed).addFiles(FileUpload.fromData(file)).queue());
            }
        });
    }
}