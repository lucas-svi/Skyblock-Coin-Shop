package lucas.skyblock.discord.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lucas.skyblock.mongo.entity.user.Role;
import lucas.skyblock.mongo.entity.user.User;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import lucas.skyblock.discord.utilities.EmbedUtil;

import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public abstract class Command {

    @Getter
    private final CommandData data;
    @Getter
    private final List<Role> roles;
    @Getter
    private final Consumer<net.dv8tion.jda.api.interactions.commands.Command> upsertionListener;

    @Getter
    private final Type type;

    @Getter
    @Setter
    private String id;

    public Command(CommandData commandData, List<Role> roles, Type type, Consumer<net.dv8tion.jda.api.interactions.commands.Command> upsertionListener){
        this.data = commandData;
        this.roles = roles;
        this.type = type;
        this.upsertionListener = upsertionListener;
    }


    public abstract void execute(SlashCommandInteractionEvent event, User user);

    public abstract void buttonClicked(ButtonInteractionEvent event, User user, String id);

    public abstract void modalInteractied(ModalInteractionEvent event, User user, String id);

    public abstract void selectMenuInteracted(GenericSelectMenuInteractionEvent event, User user, String id);

    public void notAllowed(SlashCommandInteractionEvent event){
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTimestamp(Instant.now());
        embedBuilder.setFooter("Hypixel Coin Market");
        embedBuilder.setThumbnail("https://novoline.lol/assets/skyblockcoins_empty.png");
        embedBuilder.setTitle("**Insufficient permissions**");
        embedBuilder.setDescription("You are not allowed to use this command");

        embedBuilder.setColor(Color.WHITE);

        event.replyEmbeds(embedBuilder.build()).setEphemeral(event.getChannelType() != ChannelType.PRIVATE).queue();
    }

    public void privateOnly(SlashCommandInteractionEvent event){
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTimestamp(Instant.now());
        embedBuilder.setFooter("Hypixel Coin Market");
        embedBuilder.setThumbnail("https://novoline.lol/assets/skyblockcoins_empty.png");
        embedBuilder.setTitle("**Can't use this command here**");
        embedBuilder.setDescription("This command can only be used in DM's");

        embedBuilder.setColor(Color.WHITE);

        event.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
    }

    public MessageEmbed embed(String message, String title, int color) {
        return EmbedUtil.embed(message, title, color);
    }

    public Color randomColor(){
        return new Color(ThreadLocalRandom.current().nextInt(0,255),
                ThreadLocalRandom.current().nextInt(0,255),
                ThreadLocalRandom.current().nextInt(0,255));
    }

    public static final int RED = new Color(255, 97, 97).getRGB();
    public static final int GREEN = new Color(58, 208, 0).getRGB();
    public static final int CYAN = new Color(0, 143, 179).getRGB();
    public static final int PURPLE = new Color(190, 97, 255).getRGB();
    public static final int YELLOW = new Color(253, 213, 0).getRGB();
    public static final int MAGENTA = new Color(253, 0, 255).getRGB();
    public static final int WHITE = Color.WHITE.getRGB();

    @Getter
    @AllArgsConstructor
    public enum Type{

        GLOBAL("Usable on __servers__ and __DM's__"),
        PRIVATE("Usable in __DM's only__");

        private final String fancyName;

    }

}
