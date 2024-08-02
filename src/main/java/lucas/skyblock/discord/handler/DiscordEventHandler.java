package lucas.skyblock.discord.handler;

import lucas.skyblock.discord.handler.handlers.interactions.SlashCommandInteractionHandler;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import lucas.skyblock.discord.handler.handlers.interactions.ButtonInteractionHandler;
import lucas.skyblock.discord.handler.handlers.interactions.ModalInteractionHandler;
import lucas.skyblock.discord.handler.handlers.interactions.SelectMenuInteractionHandler;

import java.util.ArrayList;
import java.util.List;

public class DiscordEventHandler implements EventListener {

    private final List<IHandler> handlers = new ArrayList<>();

    public DiscordEventHandler(){
        handlers.add(new SlashCommandInteractionHandler());
        handlers.add(new ButtonInteractionHandler());
        handlers.add(new ModalInteractionHandler());
        handlers.add(new SelectMenuInteractionHandler());
    }

    @Override
    public void onEvent(GenericEvent event) {
        handlers.forEach(handler -> handler.handle(event));
    }

}
