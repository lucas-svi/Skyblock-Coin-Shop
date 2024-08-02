package lucas.skyblock.discord.command.commands.admin;

import lucas.skyblock.discord.command.Command;
import lucas.skyblock.mongo.entity.credit.CodeStatus;
import lucas.skyblock.mongo.entity.credit.CreditCode;
import lucas.skyblock.mongo.entity.user.Role;
import lucas.skyblock.mongo.entity.user.User;
import lucas.skyblock.mongo.repository.CreditCodeRepository;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import lucas.skyblock.discord.utilities.KeyFactory;
import lucas.skyblock.utility.AppCtx;

import java.util.List;
import java.util.function.Consumer;

public class PanelCommand extends Command {

    public PanelCommand(CommandData commandData, List<Role> roles, Type type, Consumer<net.dv8tion.jda.api.interactions.commands.Command> upsertionListener) {
        super(commandData, roles, type, upsertionListener);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, User user) {
        event.replyEmbeds(
                embed("Please choose one of the options below", "Administrator Panel", YELLOW)
        ).addComponents(
                ActionRow.of(
                        Button.of(ButtonStyle.SECONDARY, "panel_generate_credit_codes", "Generate credit codes \uD83D\uDD11")
                )
        ).queue();
    }

    @Override
    public void buttonClicked(ButtonInteractionEvent event, User user, String id) {
        switch (id){
            case "panel_generate_credit_codes":
                generateCreditCodesModal(event);
                break;
        }
    }

    @Override
    public void modalInteractied(ModalInteractionEvent event, User user, String id) {
        switch (id){
            case "panel_modal_generate":
                generateCreditCodes(event);
                break;
        }
    }

    @Override
    public void selectMenuInteracted(GenericSelectMenuInteractionEvent event, User user, String id) {

    }

    private void generateCreditCodes(ModalInteractionEvent event){
        event.deferReply().queue();

        int amount = Integer.parseInt(event.getValues().get(1).getAsString());
        double creditPerCode = Double.parseDouble(event.getValues().get(0).getAsString());

        CreditCodeRepository creditVoucherRepository = AppCtx.context().getBean(CreditCodeRepository.class);

        StringBuilder keys = new StringBuilder();

        System.out.println(keys.toString());

        for (int i = 0; i < amount; i++) {
            CreditCode voucher = CreditCode.builder().code(KeyFactory.generateKey()).credits(creditPerCode).status(CodeStatus.REDEEMABLE).build();
            keys.append(voucher.getCode()).append("\n");
            creditVoucherRepository.save(voucher);
        }

        event.getHook().sendMessageEmbeds(embed(keys.toString(), "Generated " + amount + " codes (" + creditPerCode + " credits each)", CYAN)).queue();
    }

    private void generateCreditCodesModal(ButtonInteractionEvent event){
        TextInput credits = TextInput.create("amountOfCredits", "Amount of credits per code", TextInputStyle.SHORT).setRequired(true).build();
        TextInput codes = TextInput.create("amountOfCodes", "Amount of codes to generate", TextInputStyle.SHORT).setRequired(true).build();

        Modal modal = Modal.create("panel_modal_generate", "Generate codes").addComponents(ActionRow.of(credits), ActionRow.of(codes)).build();

        event.replyModal(modal).queue();
    }
}
