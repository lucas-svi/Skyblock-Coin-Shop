package lucas.skyblock.discord.handler.handlers.interactions;

import lucas.skyblock.discord.command.CommandHandler;
import lucas.skyblock.mongo.entity.user.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import lucas.skyblock.Skyblock;
import lucas.skyblock.discord.handler.IHandler;

public class SlashCommandInteractionHandler implements IHandler {

    @Override
    public void handle(GenericEvent event) {
        if(!(event instanceof SlashCommandInteractionEvent)){
            return;
        }

        SlashCommandInteractionEvent interactionEvent = (SlashCommandInteractionEvent) event;

        CommandHandler commandHandler = Skyblock.getInstance().getDiscordApplication().getCommandHandler();

        commandHandler.handleSlashCommand(interactionEvent, User.findOrCreate(interactionEvent));
    }

}
