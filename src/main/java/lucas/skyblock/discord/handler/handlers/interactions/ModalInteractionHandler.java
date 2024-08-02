package lucas.skyblock.discord.handler.handlers.interactions;

import lucas.skyblock.discord.command.CommandHandler;
import lucas.skyblock.discord.handler.IHandler;
import lucas.skyblock.mongo.entity.user.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import lucas.skyblock.Skyblock;

public class ModalInteractionHandler implements IHandler {

    @Override
    public void handle(GenericEvent event) {
        if(!(event instanceof ModalInteractionEvent)){
            return;
        }

        ModalInteractionEvent interactionEvent = (ModalInteractionEvent) event;

        CommandHandler commandHandler = Skyblock.getInstance().getDiscordApplication().getCommandHandler();

        commandHandler.handleModalInteractions(interactionEvent, User.findOrCreate(interactionEvent));
    }

}
