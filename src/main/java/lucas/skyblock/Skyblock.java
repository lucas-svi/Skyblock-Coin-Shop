package lucas.skyblock;

import lombok.Getter;
import lucas.skyblock.discord.DiscordApplication;
import lucas.skyblock.discord.command.CommandHandler;
import lucas.skyblock.discord.handler.DiscordEventHandler;
import lucas.skyblock.task.TaskExecutor;

public class Skyblock {

    private static final boolean LINUX = false;

    @Getter
    protected DiscordApplication discordApplication;

    @Getter
    protected TaskExecutor taskExecutor;

    public static final String LOCAL_VIEW = System.getProperty("discordUser");

    public static final String BOT_TOKEN = System.getProperty("botToken");
    public static final String MAIN_GUILD_ID = "1065011223474745484";
    public static final String LOGGING_GUILD_ID = "1095792668233187388";

    public static final String LOGGING_EXCEPTION_CHANNEL = "1095794212345225338";
    public static final String LOGGING_DEPOSIT_CHANNEL = "1095833596956250245";
    public static final String LOGGING_CACHE_CHANNEL = "1096100431119265842";
    public static final String LOGGING_BLOCkCHAIN_CHANNEL = "1096100575952773180";
    public static final String LOGGING_STARTUP_CHANNEL = "1096110951553573085";
    public static final String LOGGING_NEW_OFFERS_CHANNEL = "1096128838506913975";

    public static final String LOGGING_TEST_OFFERS_CHANNEL = "1096163761963946058";
    public static final String LOGGING_TICKETS_CATEGORY_ID = "1096205749719400449";
    public static final String LOGGING_EVERYONE_ROLE_ID = "1095792668233187388";

    public static final long DEPOSIT_EXPIRY_MILLIS = 3_600_000L;

    public static final double TOP_PRICE = 10;
    public static final double BOTTOM_PRICE = 12;
    public static final double THRESHOLD = 200_000_000;

    @Getter
    private static final long now = System.currentTimeMillis();

    public void start(){
        discordApplication = DiscordApplication
                .create()
                .token(BOT_TOKEN)
                .withCommandHandler(new CommandHandler())
                .withEventHandler(new DiscordEventHandler())
                .start();

        taskExecutor = new TaskExecutor();

        while (true) {
            taskExecutor.tick();
        }
    }

    private enum Singleton {
        INSTANCE;

        private final Skyblock value;

        Singleton() {
            this.value = new Skyblock();
        }
    }

    public static Skyblock getInstance() {
        return Skyblock.Singleton.INSTANCE.value;
    }
}
