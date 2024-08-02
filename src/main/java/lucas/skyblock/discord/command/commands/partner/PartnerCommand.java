package lucas.skyblock.discord.command.commands.partner;

import lucas.skyblock.discord.command.Command;
import lucas.skyblock.mongo.entity.user.Role;
import lucas.skyblock.mongo.entity.user.User;
import lucas.skyblock.mongo.repository.UserRepository;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import lucas.skyblock.utility.AppCtx;

import java.util.List;
import java.util.function.Consumer;

public class PartnerCommand extends Command {

    public PartnerCommand(CommandData commandData, List<Role> roles, Type type, Consumer<net.dv8tion.jda.api.interactions.commands.Command> upsertionListener) {
        super(commandData, roles, type, upsertionListener);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, User user) {
        event.replyEmbeds(embed("Please choose one of the options below", "Partner Panel", YELLOW))
                .addComponents(
                        ActionRow.of(
                                Button.of(ButtonStyle.PRIMARY, "partner_change_status", "Change my status")
                               // Button.of(ButtonStyle.SECONDARY, "partner_show_statistics", "Show my statistics")
                        )
                )
                .queue();
    }

    @Override
    public void buttonClicked(ButtonInteractionEvent event, User user, String id) {
        switch (id){
            case "partner_change_status":
                changeStatusSelectMenu(event, user);
                break;
            case "partner_show_statistics":
                break;
        }
    }

    @Override
    public void modalInteractied(ModalInteractionEvent event, User user, String id) {

    }

    @Override
    public void selectMenuInteracted(GenericSelectMenuInteractionEvent event, User user, String id) {
        switch (id){
            case "partner_change_status_selection":
                changeStatus(event, user);
                break;
        }
    }

    private void changeStatus(GenericSelectMenuInteractionEvent event, User user){
        String selection = (String) event.getValues().get(0);

        UserRepository userRepository = AppCtx.context().getBean(UserRepository.class);

        switch (selection){
            case "offers.accepting":
                user.setAcceptingOffers(true);
                event.replyEmbeds(embed("Your status was changed to: `Accepting offers`", null, YELLOW)).queue();
                break;
            case "offers.not.accepting":
                user.setAcceptingOffers(false);
                event.replyEmbeds(embed("Your status was changed to: `Not accepting offers`", null, YELLOW)).queue();

        }

        userRepository.save(user);
    }

    private void changeStatusSelectMenu(ButtonInteractionEvent event, User user){
        StringSelectMenu.Builder roleSelection = StringSelectMenu.create("partner_change_status_selection");

        UserRepository userRepository = AppCtx.context().getBean(UserRepository.class);

        roleSelection.addOption("I am accepting offers right now", "offers.accepting", Emoji.fromUnicode("\uD83D\uDFE2"));
        roleSelection.addOption("I am not accepting offers right now", "offers.not.accepting", Emoji.fromUnicode("\uD83D\uDD34"));

        event.replyEmbeds(
                embed("Please select a new role", null, YELLOW)
        ).addActionRow(
                roleSelection.build()
        ).queue();
    }
}
