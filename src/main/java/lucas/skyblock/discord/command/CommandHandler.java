package lucas.skyblock.discord.command;

import lombok.Getter;
import lombok.SneakyThrows;
import lucas.skyblock.mongo.entity.user.Role;
import lucas.skyblock.mongo.entity.user.User;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import lucas.skyblock.Skyblock;
import lucas.skyblock.discord.command.commands.admin.CreditsCommand;
import lucas.skyblock.discord.command.commands.admin.PanelCommand;
import lucas.skyblock.discord.command.commands.admin.UserCommand;
import lucas.skyblock.discord.command.commands.everyone.BalanceCommand;
import lucas.skyblock.discord.command.commands.everyone.ChainStatusCommand;
import lucas.skyblock.discord.command.commands.everyone.HelpCommand;
import lucas.skyblock.discord.command.commands.everyone.OffersCommand;
import lucas.skyblock.discord.command.commands.partner.PartnerCommand;
import lucas.skyblock.discord.utilities.EmbedUtil;
import lucas.skyblock.discord.utilities.Logger;

import java.awt.*;
import java.util.*;
import java.util.List;

public class CommandHandler {

    public static final List<Role> EVERYONE = Arrays.asList(Role.EVERYONE, Role.ADMIN, Role.CUSTOMER, Role.STAFF, Role.PARTNER);
    public static final List<Role> PARTNER = Arrays.asList(Role.ADMIN,  Role.STAFF, Role.PARTNER);
    public static final List<Role> STAFF = Arrays.asList(Role.ADMIN, Role.STAFF);
    public static final List<Role> DEVELOPER = Collections.singletonList(Role.ADMIN);

    @Getter
    private final List<Command> commands = new ArrayList<>();

    public CommandHandler() {
        commands.add(new HelpCommand(Commands.slash("help", "Prints useful information about other commands"), EVERYONE, Command.Type.GLOBAL, cmd -> {

        }));

        commands.add(new ChainStatusCommand(Commands.slash("nodes", "Prints our blockchain nodes' status"), EVERYONE, Command.Type.GLOBAL, cmd -> {

        }));

        commands.add(new BalanceCommand(Commands.slash("balance", "Used to view your account balance, make deposits and redeem credit codes"), EVERYONE, Command.Type.PRIVATE, cmd -> {

        }));

        commands.add(new OffersCommand(Commands.slash("purchase", "Purchase skyblock coins"), EVERYONE, Command.Type.PRIVATE, cmd -> {

        }));

        commands.add(new PanelCommand(Commands.slash("panel", "Administrator Panel"), DEVELOPER, Command.Type.PRIVATE, cmd -> {

        }));

        commands.add(new CreditsCommand(Commands.slash("credits", "Adds credits to a user (Admin only)"), DEVELOPER, Command.Type.GLOBAL, command -> {
            command.editCommand().addOptions(
                    new OptionData(OptionType.USER, "user", "User", true),
                    new OptionData(OptionType.NUMBER, "amount", "Amount", true)
            ).queue();
        }));

        commands.add(new UserCommand(Commands.slash("user", "Lookup a user (Staff/Partner only)"), PARTNER, Command.Type.GLOBAL, command -> {
            command.editCommand().addOptions(
                    new OptionData(OptionType.USER, "user", "User", true)
            ).queue();
        }));

        commands.add(new PartnerCommand(Commands.slash("partner", "Partner panel (Staff/Partner only)"), PARTNER, Command.Type.PRIVATE, cmd -> {

        }));
    }

    public void upsertCommands(JDA jda) {
        long now = System.currentTimeMillis();

        Timer timer = new Timer();
        for (Command command : commands) {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    jda.upsertCommand(command.getData()).queue(s -> {
                        if(commands.indexOf(command) == 0)
                            Logger.log("Starting the bot \n" +
                                    "...................…………………………._¸„„„„_\n" +
                                    "……………………………….…………...„--~*'¯…….'\\\n" +
                                    "…………………….…………………… („-~~--„¸_….,/ì'Ì\n" +
                                    "……………….…………………….¸„-^\"¯ : : : : :¸-¯\"¯/'\n" +
                                    "………………………………¸„„-^\"¯ : : : : : : : '\\¸„„,-\"\n" +
                                    "**¯¯¯'^^~-„„„----~^*'\"¯ : : : : : : : : : :¸-\"\n" +
                                    ".:.:.:.:.„-^\" : : : : : : : : : : : : : : : : :„-\"\n" +
                                    ":.:.:.:.:.:.:.:.:.:.: : : : : : : : : : ¸„-^¯\n" +
                                    ".::.:.:.:.:.:.:.:. : : : : : : : ¸„„-^¯\n" +
                                    ":.' : : '\\ : : : : : : : ;¸„„-~\"\n" +
                                    ":.:.:: :\"-„\"\"***/*'ì¸'¯\n" +
                                    ":.': : : : :\"-„ : : :\"\\\n" +
                                    ".:.:.: : : : :\" : : : : \\,\n" +
                                    ":.: : : : : : : : : : : : 'Ì\n" +
                                    ": : : : : : :, : : : : : :/\n" +
                                    "\"-„_::::_„-*__„„~\"", null, Color.WHITE, Logger.LogType.STARTUP);

                        command.setId(s.getId());
                        command.getUpsertionListener().accept(s);
                        Logger.log("Inserted command: `" + command.getData().getName() + " (" + (commands.indexOf(command) + 1) + "/" + commands.size() + ")`", null, Color.YELLOW, Logger.LogType.STARTUP);

                        if((commands.indexOf(command) + 1) == commands.size()){
                            Logger.log("Insertion took `" + (System.currentTimeMillis() - now) + "` ms", "All commands are inserted", Color.YELLOW, Logger.LogType.STARTUP);
                            Logger.log("Startup took `" + (System.currentTimeMillis() - Skyblock.getNow()) + "` ms" +
                                    "\n" +
                                    "…………………...„„-~^^~„-„„_\n" +
                                    "………………„-^*'' : : „'' : : : : *-„\n" +
                                    "…………..„-* : : :„„--/ : : : : : : : '\\\n" +
                                    "…………./ : : „-* . .| : : : : : : : : '|\n" +
                                    "……….../ : „-* . . . | : : : : : : : : |\n" +
                                    "………...\\„-* . . . . .| : : : : : : : :'|\n" +
                                    "……….../ . . . . . . '| : : : : : : : :|\n" +
                                    "……..../ . . . . . . . .'\\ : : : : : : : |\n" +
                                    "……../ . . . . . . . . . .\\ : : : : : : :|\n" +
                                    "……./ . . . . . . . . . . . '\\ : : : : : /\n" +
                                    "….../ . . . . . . . . . . . . . *-„„„„-*'\n" +
                                    "….'/ . . . . . . . . . . . . . . '|\n" +
                                    "…/ . . . . . . . ./ . . . . . . .|\n" +
                                    "../ . . . . . . . .'/ . . . . . . .'|\n" +
                                    "./ . . . . . . . . / . . . . . . .'|\n" +
                                    "'/ . . . . . . . . . . . . . . . .'|\n" +
                                    "'| . . . . . \\ . . . . . . . . . .|\n" +
                                    "'| . . . . . . \\„_^- „ . . . . .'|\n" +
                                    "'| . . . . . . . . .'\\ .\\ ./ '/ . |\n" +
                                    "| .\\ . . . . . . . . . \\ .'' / . '|\n" +
                                    "| . . . . . . . . . . / .'/ . . .|\n" +
                                    "| . . . . . . .| . . / ./ ./ . .|", "The bot is ready.", Color.YELLOW, Logger.LogType.STARTUP);
                        }
                    });
                }
            }, 2500L * commands.indexOf(command));
        }
    }

    @SneakyThrows
    public void handleButtonClick(ButtonInteractionEvent event, User user) {
        for (Command command : commands) {
            command.buttonClicked(event, user, event.getButton().getId());
        }
    }

    @SneakyThrows
    public void handleModalInteractions(ModalInteractionEvent event, User user){
        for (Command command : commands) {
            command.modalInteractied(event, user, event.getModalId());
        }
    }

    @SneakyThrows
    public void handleSelectMenuInteractions(GenericSelectMenuInteractionEvent event, User user){
        for (Command command : commands) {
            command.selectMenuInteracted(event, user, event.getComponent().getId());
        }
    }

    @SneakyThrows
    public void handleSlashCommand(SlashCommandInteractionEvent event, User user) {
        if(user.getRole() == Role.BANNED){
            event.replyEmbeds(
                    EmbedUtil.embed("You are banned!\n If you think this ban is unjustified - contact admins", "Failed to execute this command", Command.RED)
            ).setEphemeral(true).queue();
            return;
        }

        for (Command command : commands) {
            if (!command.getData().getName().equals(event.getName())) {
                continue;
            }

            if (!command.getRoles().contains(user.getRole())) {
                command.notAllowed(event);
                continue;
            }

            if (command.getType() == Command.Type.PRIVATE && event.getChannelType() != ChannelType.PRIVATE) {
                command.privateOnly(event);
                continue;
            }

            try {
                command.execute(event, user);
            }catch (Exception e){
                Logger.log("Exception while processing command " + command.getData().getName(), e.getLocalizedMessage(), 0xffffffff, Logger.LogType.EXCEPTION);
            }
        }
    }
}
