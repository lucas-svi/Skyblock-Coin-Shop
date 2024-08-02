package lucas.skyblock.discord.command.commands.everyone;

import lucas.skyblock.discord.command.Command;
import lucas.skyblock.discord.command.CommandHandler;
import lucas.skyblock.mongo.entity.user.Role;
import lucas.skyblock.mongo.entity.user.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import lucas.skyblock.Skyblock;

import java.util.List;
import java.util.function.Consumer;

public class HelpCommand extends Command {

    public HelpCommand(CommandData commandData, List<Role> roles, Type type, Consumer<net.dv8tion.jda.api.interactions.commands.Command> upsertionListener) {
        super(commandData, roles, type, upsertionListener);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, User user) {
        CommandHandler commandHandler = Skyblock.getInstance().getDiscordApplication().getCommandHandler();

        StringBuilder builder = new StringBuilder();

        for (Command command : commandHandler.getCommands()) {
            if(!command.getRoles().contains(user.getRole())){
                continue;
            }

            builder
                    .append("</")
                    .append(command.getData().getName())
                    .append(":")
                    .append(command.getId())
                    .append(">")
                    .append(" - **")
                    .append(((SlashCommandData) command.getData()).getDescription())
                    .append("** (").append(command.getType().getFancyName()).append(")\n");
        }

        event.replyEmbeds(embed(builder.toString(), "Here's a list of usable commands", YELLOW)).setEphemeral(event.getChannelType() != ChannelType.PRIVATE).queue();
    }

    @Override
    public void buttonClicked(ButtonInteractionEvent event, User customer, String id) {

    }

    @Override
    public void modalInteractied(ModalInteractionEvent event, User user, String id) {

    }

    @Override
    public void selectMenuInteracted(GenericSelectMenuInteractionEvent event, User user, String id) {

    }
}
