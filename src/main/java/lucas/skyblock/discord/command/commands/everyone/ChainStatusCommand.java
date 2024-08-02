package lucas.skyblock.discord.command.commands.everyone;

import lucas.skyblock.discord.command.Command;
import lucas.skyblock.mongo.entity.user.Role;
import lucas.skyblock.mongo.entity.user.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import lucas.skyblock.chain.Bitcoin;
import lucas.skyblock.chain.Litecoin;

import java.util.List;
import java.util.function.Consumer;

public class ChainStatusCommand extends Command {

    public ChainStatusCommand(CommandData commandData, List<Role> roles, Type type, Consumer<net.dv8tion.jda.api.interactions.commands.Command> upsertionListener) {
        super(commandData, roles, type, upsertionListener);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, User user) {
        event.deferReply().setEphemeral(event.getChannelType() != ChannelType.PRIVATE).queue();

        event.getHook().sendMessageEmbeds(embed("Litecoin chain: " + (new Litecoin().isSynchronized() ? "**Synchronized**" : "**Not Synchronized**") + "\n" +
                "Bitcoin chain: " + (new Bitcoin().isSynchronized() ? "**Synchronized**" : "**Not Synchronized**"), "Internal Nodes Status", CYAN))
                .setEphemeral(event.getChannelType() != ChannelType.PRIVATE).queue();
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
