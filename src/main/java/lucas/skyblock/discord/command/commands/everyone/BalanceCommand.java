package lucas.skyblock.discord.command.commands.everyone;

import lucas.skyblock.discord.command.Command;
import lucas.skyblock.mongo.entity.credit.CodeStatus;
import lucas.skyblock.mongo.entity.credit.CreditCode;
import lucas.skyblock.mongo.entity.deposit.Deposit;
import lucas.skyblock.mongo.entity.deposit.DepositStatus;
import lucas.skyblock.mongo.entity.user.Role;
import lucas.skyblock.mongo.entity.user.User;
import lucas.skyblock.mongo.repository.CreditCodeRepository;
import lucas.skyblock.mongo.repository.DepositRepository;
import lucas.skyblock.mongo.repository.UserRepository;
import net.dv8tion.jda.api.EmbedBuilder;
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
import lucas.skyblock.Skyblock;
import lucas.skyblock.chain.Bitcoin;
import lucas.skyblock.chain.IChain;
import lucas.skyblock.chain.Litecoin;
import lucas.skyblock.discord.utilities.Logger;
import lucas.skyblock.utility.AppCtx;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static lucas.skyblock.utility.StringUtility.fancyNumber;

public class BalanceCommand extends Command {

    public BalanceCommand(CommandData commandData, List<Role> roles, Type type, Consumer<net.dv8tion.jda.api.interactions.commands.Command> upsertionListener) {
        super(commandData, roles, type, upsertionListener);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, User user) {
        double onHold = user.getOnHold() == null ? 0 : user.getOnHold();

        if(user.getRole() == Role.PARTNER){
            event.replyEmbeds(
                    embed("Partner balance: `" + fancyNumber(user.getWithdrawableBalance()) + " credits`", "Current partner balance", YELLOW)
            ).queue();
        }else {
            event.replyEmbeds(
                    embed("Balance: `" + user.getBalance() + " credits`\n" +
                            "On Hold: `" + String.format("%.2f", onHold) + " credits`", "Current balance", YELLOW)
            ).addActionRow(
                    Button.of(ButtonStyle.SUCCESS, "balance_deposit", "Deposit Funds \uD83D\uDCB8"),
                    Button.of(ButtonStyle.PRIMARY, "balance_redeem", "Redeem Credit Code \uD83D\uDCB3")
            ).queue();
        }
    }

    @Override
    public void buttonClicked(ButtonInteractionEvent event, User user, String id) {
        if(id.startsWith("deposit_cancel")){
            cancelDeposit(event, id.split("deposit_cancel")[1]);
            return;
        }

        switch (id){
            case "balance_deposit":
                chooseChainDialog(event, user);
                break;
            case "balance_redeem":
                reedemCreditCodeModal(event, user);
                break;
            case "deposit_litecoin":
                createDeposit(event, user, new Litecoin());
                break;
            case "deposit_bitcoin":
                createDeposit(event, user, new Bitcoin());
                break;
        }
    }

    @Override
    public void modalInteractied(ModalInteractionEvent event, User user, String id) {
        if(id.startsWith("deposit_modal_redeem")){
            redeemCredits(event);
        }
    }

    @Override
    public void selectMenuInteracted(GenericSelectMenuInteractionEvent event, User user, String id) {

    }

    private void reedemCreditCodeModal(ButtonInteractionEvent event, User user){
        TextInput serial = TextInput.create("redeem_serial", "Code to redeem", TextInputStyle.SHORT).setRequired(true).setMinLength(18).setMaxLength(18).build();

        Modal modal = Modal.create("deposit_modal_redeem_" + user.getDisordID(), "Redeem credit code").addComponents(ActionRow.of(serial)).build();

        event.replyModal(modal).queue();
    }

    private void redeemCredits(ModalInteractionEvent event){
        String discordID = event.getModalId().split("_")[3];

        UserRepository userRepository = AppCtx.context().getBean(UserRepository.class);

        User user = userRepository.findByDisordID(discordID);

        String code = event.getValues().get(0).getAsString();

        CreditCodeRepository codeRepository = AppCtx.context().getBean(CreditCodeRepository.class);

        CreditCode creditCode = codeRepository.findByCode(code);

        if(creditCode == null){
            event.replyEmbeds(embed("Credit code not found, did you copy it right?", null, RED)).queue();
            return;
        }

        if(creditCode.getStatus() == CodeStatus.REDEEMED){
            event.replyEmbeds(embed("This credit code was redeemed by someone else!", null, RED)).queue();
            return;
        }

        user.setBalance(user.getBalance() + creditCode.getCredits());
        userRepository.save(user);

        creditCode.setStatus(CodeStatus.REDEEMED);
        creditCode.setRedeemedOn(System.currentTimeMillis());
        creditCode.setRedeemedBy(user.getDisordID());

        codeRepository.save(creditCode);

        event.replyEmbeds(embed(String.format("`%.2f` credits were added to your account.", creditCode.getCredits()), "Code redeemed successfully", GREEN)).queue();

        Logger.log(
                String.format("Credit serial: `%s`\n" +
                        "Amount redeemed: `%s`\n" +
                        "Redeemed by: `%s`\n" +
                        "Redeemed <t:%s:R>", creditCode.getCode(), creditCode.getCredits(), event.getUser().getName() + "(" + user.getDisordID() + ")", System.currentTimeMillis()/1000),
                "Credit code redeemed", randomColor().getRGB(), Logger.LogType.DEPOSIT_LOG);
    }

    private void chooseChainDialog(ButtonInteractionEvent event, User user){
        event.replyEmbeds(
                embed("Please choose cryptocurrency you would like to deposit with", null, WHITE)
        ).addActionRow(
                Button.of(ButtonStyle.SECONDARY, "deposit_litecoin", "Litecoin"),
                Button.of(ButtonStyle.SECONDARY, "deposit_bitcoin", "Bitcoin")
        ).queue();
    }

    private void createDeposit(ButtonInteractionEvent event, User user, IChain chain){
        event.deferReply().queue();

        String newAddress = chain.getNewAddress();

        Deposit deposit = Deposit
                .builder()
                .gateway(chain)
                .initiator(user.getDisordID())
                .cryptoAddress(newAddress)
                .totalReceived(0)
                .uuid(UUID.randomUUID().toString())
                .expiresAt(System.currentTimeMillis() + Skyblock.DEPOSIT_EXPIRY_MILLIS)
                .status(DepositStatus.ONGOING_NOT_PAID)
                .build();

        EmbedBuilder depositEmbedReply = new EmbedBuilder();

        depositEmbedReply.setAuthor("Hypixel Coin Market", "https://coins.novoline.lol", "https://novoline.lol/assets/skyblockcoins.png");
        depositEmbedReply.setTimestamp(Instant.now());
        depositEmbedReply.setThumbnail("https://novoline.lol/assets/skyblockcoins_empty.png");

        depositEmbedReply.setColor(Command.CYAN);
        depositEmbedReply.setImage(
                String.format("https://chart.googleapis.com/chart?chs=250x250&cht=qr&chl=%s", deposit.getCryptoAddress()));

        depositEmbedReply.setTitle("Your deposit is created");
        depositEmbedReply.setDescription(
                String.format(
                        "Please deposit desired amount of `%s` to `%s`\n" +
                                "Your deposit ID: `%s`\n" +
                                "Your deposit expires <t:%s:R>\n\n" +
                                "Your account will receive 1 credit per 1 cent you paid (e.g $1 - 100 credits)\n\n" +
                                "__Make sure to send cryptocurrency before this deposit expires!__\n" +
                                "**This address can only be used once, create new deposit to receive new address if your previous deposit expired**",
                        chain instanceof Bitcoin ? "BTC" : "LTC", deposit.getCryptoAddress(),
                        deposit.getUuid(),
                        (System.currentTimeMillis() + Skyblock.DEPOSIT_EXPIRY_MILLIS) / 1_000
                )
        );

        depositEmbedReply.setFooter("Deposit " + deposit.getUuid());

        event.getHook().sendMessageEmbeds(depositEmbedReply.build()).addActionRow(
                Button.of(ButtonStyle.DANGER, "deposit_cancel_" + deposit.getUuid(), "Cancel this deposit")
        ).queue();

        DepositRepository depositRepository = AppCtx.context().getBean(DepositRepository.class);

        depositRepository.save(deposit);
    }

    private void cancelDeposit(ButtonInteractionEvent event, String dId) {
        event.deferReply().queue();

        DepositRepository depositRepository = AppCtx.context().getBean(DepositRepository.class);

        Deposit deposit = depositRepository.findByUuid(dId);

        deposit.setStatus(DepositStatus.CANCELLED_BY_USER);

        depositRepository.save(deposit);

        event.getHook().sendMessageEmbeds(embed(String.format("Deposit `%s` was cancelled", deposit.getUuid()), null, Command.RED)).queue();
    }
}
