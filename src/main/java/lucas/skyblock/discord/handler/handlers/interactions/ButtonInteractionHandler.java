package lucas.skyblock.discord.handler.handlers.interactions;

import lucas.skyblock.discord.command.CommandHandler;
import lucas.skyblock.discord.handler.IHandler;
import lucas.skyblock.mongo.entity.user.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import lucas.skyblock.Skyblock;

public class ButtonInteractionHandler implements IHandler {

    @Override
    public void handle(GenericEvent event) {
        if(!(event instanceof ButtonInteractionEvent)){
            return;
        }

        ButtonInteractionEvent interactionEvent = (ButtonInteractionEvent) event;

        CommandHandler commandHandler = Skyblock.getInstance().getDiscordApplication().getCommandHandler();

        commandHandler.handleButtonClick(interactionEvent, User.findOrCreate(interactionEvent));
    }

}
