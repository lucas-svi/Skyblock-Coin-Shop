package lucas.skyblock.discord;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lucas.skyblock.discord.command.CommandHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import lucas.skyblock.Skyblock;
import lucas.skyblock.discord.handler.DiscordEventHandler;

@Getter
@Setter
public class DiscordApplication {

    protected String token;

    protected DiscordEventHandler discordEventHandler;

    protected CommandHandler commandHandler;

    protected JDA jda;

    public static DiscordApplication create(){
        return new DiscordApplication();
    }

    public DiscordApplication token(String token){
        this.token = token;
        return this;
    }

    public DiscordApplication withEventHandler(DiscordEventHandler discordEventHandler){
        if(this.discordEventHandler != null){
            throw new RuntimeException("Event handler is already instantiated!");
        }

        this.discordEventHandler = discordEventHandler;
        return this;
    }

    public DiscordApplication withCommandHandler(CommandHandler handler){
        if(this.commandHandler != null){
            throw new RuntimeException("Command handler is already instantiated!");
        }

        this.commandHandler = handler;
        return this;
    }

    @SneakyThrows
    public DiscordApplication start() {
        if(token.isEmpty()){
            throw new RuntimeException("Discord bot token is empty!");
        }

        if(discordEventHandler == null){
            throw new RuntimeException("Discord event handler is not set!");
        }

        if(commandHandler == null){
            throw new RuntimeException("Command handler was not set");
        }

        jda = JDABuilder.createDefault(token)
                .addEventListeners(discordEventHandler)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .enableIntents(GatewayIntent.GUILD_MESSAGES)
                .enableIntents(GatewayIntent.DIRECT_MESSAGES)
                .build();

        jda.awaitReady();

        commandHandler.upsertCommands(jda);

        return this;
    }

    public Guild getMainGuild(){
        for (Guild guild : jda.getGuilds()) {
            if(guild.getId().equals(Skyblock.MAIN_GUILD_ID)){
                return guild;
            }
        }
        return null;
    }

    public Guild getLoggingGuild(){
        for (Guild guild : jda.getGuilds()) {
            if(guild.getId().equals(Skyblock.LOGGING_GUILD_ID)){
                return guild;
            }
        }
        return null;
    }
}
