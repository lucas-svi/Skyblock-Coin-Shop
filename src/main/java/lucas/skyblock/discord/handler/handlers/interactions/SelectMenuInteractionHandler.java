package lucas.skyblock.discord.handler.handlers.interactions;

import lucas.skyblock.discord.command.CommandHandler;
import lucas.skyblock.discord.handler.IHandler;
import lucas.skyblock.mongo.entity.user.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import lucas.skyblock.Skyblock;

public class SelectMenuInteractionHandler implements IHandler {

    @Override
    public void handle(GenericEvent event) {
        if(!(event instanceof GenericSelectMenuInteractionEvent)){
            return;
        }

        GenericSelectMenuInteractionEvent interactionEvent = (GenericSelectMenuInteractionEvent) event;

        CommandHandler commandHandler = Skyblock.getInstance().getDiscordApplication().getCommandHandler();

        commandHandler.handleSelectMenuInteractions(interactionEvent, User.findOrCreate(interactionEvent));
    }

}
