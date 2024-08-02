package lucas.skyblock.discord.command.commands.everyone;

import lucas.skyblock.discord.command.Command;
import lucas.skyblock.mongo.entity.offer.Offer;
import lucas.skyblock.mongo.entity.offer.OfferStatus;
import lucas.skyblock.mongo.entity.user.Role;
import lucas.skyblock.mongo.entity.user.User;
import lucas.skyblock.mongo.repository.OfferRepository;
import lucas.skyblock.mongo.repository.UserRepository;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
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
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import lucas.skyblock.Skyblock;
import lucas.skyblock.discord.utilities.GuildUtility;
import lucas.skyblock.discord.utilities.Logger;
import lucas.skyblock.utility.AppCtx;
import lucas.skyblock.utility.StringUtility;

import java.util.*;
import java.util.function.Consumer;

public class OffersCommand extends Command {

    public OffersCommand(CommandData commandData, List<Role> roles, Type type, Consumer<net.dv8tion.jda.api.interactions.commands.Command> upsertionListener) {
        super(commandData, roles, type, upsertionListener);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, User user) {
        UserRepository userRepository = AppCtx.context().getBean(UserRepository.class);

        List<User> dealers = userRepository.findAllByRole(Role.PARTNER);

        if (dealers.isEmpty()) {
            event.replyEmbeds(embed("You might want to check again later.", "No coin dealers available!", RED)).queue();
            return;
        }

        StringBuilder formatted = new StringBuilder();

        List<User> notAccepting = new ArrayList<>();

        for (User dealer : dealers) {
            if (!dealer.getAcceptingOffers()) {
                notAccepting.add(dealer);
                continue;
            }

            formatted
                    .append("\uD83D\uDFE2   **")
                    .append(dealer.getUsername())
                    .append(" - ")
                    .append("Accepting offers")
                    .append("**\n");
        }

        if (!notAccepting.isEmpty()) {
            if (dealers.size() != notAccepting.size())
                formatted.append("\n");

            for (User dealer : notAccepting) {
                formatted
                        .append("\uD83D\uDD34   **")
                        .append(dealer.getUsername())
                        .append(" - ")
                        .append("Currently not accepting offers")
                        .append("**\n");
            }
        }

        Button button = dealers.size() == notAccepting.size() ? Button.of(ButtonStyle.PRIMARY, "offers_create", "Purchase Coins").asDisabled() : Button.of(ButtonStyle.PRIMARY, "offers_create", "Purchase Coins").asEnabled();

        event.replyEmbeds(embed(formatted.toString(), "Buy skyblock coins", YELLOW))
                .addActionRow(
                        button
                )
                .queue();
    }

    @Override
    public void buttonClicked(ButtonInteractionEvent event, User user, String id) {
        if (id.startsWith("offer_claim")) {
            claimOffer(event, user, id.split("_")[2]);
        } else if (id.startsWith("offer_abandon")) {
            abandonOffer(event, user, id.split("_")[2]);
        } else if (id.startsWith("offer_completed")) {
            offerCompleted(event, user, id.split("_")[2]);
        }else if(id.startsWith("offer_partially_completed")){
            offerPartiallyCompletedModal(event, user, id.split("_")[3]);
        } else {
            switch (id) {
                case "offers_create":
                    createOfferModal(event, user);
                    break;
            }
        }
    }

    @Override
    public void modalInteractied(ModalInteractionEvent event, User user, String id) {
        if(id.startsWith("offer_modal_partial_complete")){
            offerPartiallyCompleted(event, user, id.split("_")[4]);
        }else {
            switch (id) {
                case "offer_modal":
                    createOffer(event, user);
                    break;
            }
        }
    }

    @Override
    public void selectMenuInteracted(GenericSelectMenuInteractionEvent event, User user, String id) {

    }


    private void createOffer(ModalInteractionEvent event, User user) {
        event.deferReply().queue();

        double coinAmount = Double.parseDouble(event.getValues().get(0).getAsString());

        double price;

        double l = coinAmount / 1_000_000.0D;

        if (coinAmount < Skyblock.THRESHOLD) {
            price = l * Skyblock.TOP_PRICE;
        } else {
            price = l * Skyblock.BOTTOM_PRICE;
        }

        Offer offer = Offer.builder()
                .uuid(UUID.randomUUID().toString())
                .coinAmount(coinAmount)
                .coinAmountInCredits(price)
                .initiator(user.getDisordID())
                .status(OfferStatus.CREATED)
                .build();

        if (user.getBalance() < offer.getCoinAmountInCredits()) {
            event.getHook().sendMessageEmbeds(
                    embed
                            (
                                    "Not enough credits in your balance! Use </balance:1095825935393103902>" +
                                            " to get more information",
                                    null, RED
                            )
            ).queue();
            return;
        } else {
            user.setBalance(user.getBalance() - offer.getCoinAmountInCredits());

            if (user.getOnHold() == null) {
                user.setOnHold(0.0);
            }

            user.setOnHold(user.getOnHold() + offer.getCoinAmountInCredits());
            AppCtx.context().getBean(UserRepository.class).save(user);
        }

        OfferRepository offerRepository = AppCtx.context().getBean(OfferRepository.class);

        // todo save
        String formatted = String.format(
                "Offer `%s` was placed\n" +
                        "It will now be forwarded to our partners who will accept your request soon.\n" +
                        "You will be notified in here once your offer gets accepted.", offer.getUuid()
        );

        event.getHook().sendMessageEmbeds(embed(formatted, "Offer `" + offer.getUuid() + "` placed", YELLOW)).queue();

        // todo logging -> main
        Guild main = Skyblock.getInstance().getDiscordApplication().getLoggingGuild();

        TextChannel offerChannel = main.getTextChannelById(Skyblock.LOGGING_TEST_OFFERS_CHANNEL);

        if (offerChannel == null) {
            event.getHook().sendMessageEmbeds(embed("Offer can not be delivered", "Error", RED)).queue();
            return;
        }

        offerChannel.sendMessageEmbeds(
                embed(
                        "Customer: <@" + offer.getInitiator() + ">\n\n" +
                                "Coin amount: `" + StringUtility.verboseNumber(offer.getCoinAmount()) + " coins`\n" +
                                "In credits: `" + String.format("%.2f", offer.getCoinAmountInCredits()) + " credits`", "Offer `" + offer.getUuid() + "`", GREEN
                )
        ).addActionRow(
                Button.of(ButtonStyle.SUCCESS, "offer_claim_" + offer.getUuid(), "Claim this order", Emoji.fromUnicode("\uD83D\uDEC4"))
        ).queue(success -> {
            offer.setBroadcastedMessageID(success.getId());
            offerRepository.save(offer);
        });

        Logger.log("Offer `" + offer.getUuid() + "` was created by <@" + offer.getInitiator() + ">\n " +
                "Coin amount: `" + StringUtility.verboseNumber(offer.getCoinAmount()) + " coins`\n" +
                "In credits: `" + String.format("%.2f", offer.getCoinAmountInCredits()) + " credits`", "New offer", YELLOW, Logger.LogType.NEW_OFFERS);

        offerRepository.save(offer);
    }

    private void claimOffer(ButtonInteractionEvent event, User user, String id) {
        event.deferReply().setEphemeral(true).queue();

        OfferRepository offerRepository = AppCtx.context().getBean(OfferRepository.class);

        Offer offer = offerRepository.findByUuid(id);

        if (offer.getStatus() == OfferStatus.IN_WORK) {
            event.getHook().sendMessageEmbeds(
                    embed("This offer is already claimed by someone else!", "Offer is already claimed", RED)
            ).setEphemeral(true).queue();
            return;
        }

        offer.setStatus(OfferStatus.IN_WORK);
        offer.setWorker(user.getDisordID());

        offerRepository.save(offer);

        UserRepository userRepository = AppCtx.context().getBean(UserRepository.class);

        // todo logging -> main
        Guild main = Skyblock.getInstance().getDiscordApplication().getLoggingGuild();

        TextChannel offerChannel = main.getTextChannelById(Skyblock.LOGGING_TEST_OFFERS_CHANNEL);

        main.loadMembers().onSuccess(members -> {
            Member reseller = null, initiator = null;

            for (Member member : members) {
                if (member.getId().equals(user.getDisordID())) {
                    reseller = member;
                } else if (member.getId().equals(offer.getInitiator())) {
                    initiator = member;
                }
            }

            User customer = userRepository.findByDisordID(offer.getInitiator());

            if (reseller == null || initiator == null) {
                event.getHook().sendMessageEmbeds(embed("Could not locate reseller and/or offer initiator\nVoiding this offer", "Error", RED)).setEphemeral(true).queue();
                offer.setStatus(OfferStatus.VOIDED);

                customer.setOnHold(customer.getOnHold() - offer.getCoinAmountInCredits());
                customer.setBalance(customer.getBalance() + offer.getCoinAmountInCredits());

                offerRepository.save(offer);
                userRepository.save(customer);

                offerChannel.deleteMessageById(offer.getBroadcastedMessageID()).queue();
                return;
            }

            Member finalReseller = reseller;
            Member finalInitiator = initiator;
            main.createTextChannel("offer-" + customer.getUsername(), main.getCategoryById(Skyblock.LOGGING_TICKETS_CATEGORY_ID)).queue(s -> {
                EnumSet<Permission> permissionEnumSet = EnumSet.of(Permission.MESSAGE_SEND, Permission.MESSAGE_HISTORY, Permission.VIEW_CHANNEL);

                s.getPermissionContainer().upsertPermissionOverride(finalReseller).setAllowed(permissionEnumSet).queue();
                s.getPermissionContainer().upsertPermissionOverride(finalInitiator).setAllowed(permissionEnumSet).queue();
                s.getPermissionContainer().upsertPermissionOverride(main.getRoleById(Skyblock.LOGGING_EVERYONE_ROLE_ID)).setDenied(permissionEnumSet).queue();

                event.getHook().sendMessageEmbeds(embed("Offer claimed. <#" + s.getId() + ">", null, GREEN)).setEphemeral(true).queue();

                GuildUtility.sendPrivateMessageEmbed(main, offer.getInitiator(), embed("Your offer was accepted by `" + user.getUsername() + "`\n" +
                        "Please proceed to this channel to continue: <#" + s.getId() + ">", "Offer `" + offer.getUuid() + "` was accepted", GREEN));

                s.sendMessage("<@" + user.getDisordID() + "> <@" + offer.getInitiator() + "> You can now start the exchange.").addActionRow(
                        Button.of(ButtonStyle.SUCCESS, "offer_completed_" + offer.getUuid(), "Mark this offer as completed", Emoji.fromUnicode("\u2714\uFE0F")),
                        Button.of(ButtonStyle.SECONDARY, "offer_partially_completed_" + offer.getUuid(), "Mark this offer as partially completed"),
                        Button.of(ButtonStyle.DANGER, "offer_abandon_" + offer.getUuid(), "Abandon this offer")
                ).queue();

                offer.setTicketChannelID(s.getId());
                offerRepository.save(offer);

                offerChannel.editMessageById(offer.getBroadcastedMessageID(), MessageEditData.fromEmbeds(
                        embed(
                                "Customer: <@" + offer.getInitiator() + ">\n\n" +
                                        "Coin amount: `" + StringUtility.verboseNumber(offer.getCoinAmount()) + " coins`\n" +
                                        "In credits: `" + String.format("%.2f", offer.getCoinAmountInCredits()) + " credits`\n\n" +
                                        "Claimed by: `" + event.getUser().getName() + "#" + event.getUser().getDiscriminator() + "`",
                                "Offer `" + offer.getUuid() + "`", RED)
                )).queue();

                offerChannel.editMessageComponentsById(offer.getBroadcastedMessageID(), ActionRow.of(
                        Button.of(ButtonStyle.DANGER, "offer_claimed", "Claimed by " + event.getUser().getName(), Emoji.fromUnicode("\uD83D\uDEC4")).asDisabled()
                )).queue();
            });
        });
    }

    private void offerCompleted(ButtonInteractionEvent event, User user, String id) {
        if (user.getRole() != Role.PARTNER) {
            event.replyEmbeds(embed("Only partners can mark orders as completed", "You cannot perform that action", RED)).setEphemeral(true).queue();
            return;
        }

        OfferRepository offerRepository = AppCtx.context().getBean(OfferRepository.class);

        Offer offer = offerRepository.findByUuid(id);

        if (offer.getStatus() != OfferStatus.IN_WORK) {
            event.replyEmbeds(embed("This offer is already completed, wait for this ticket to delete!", null, RED)).setEphemeral(true).queue();
            return;
        }

        event.replyEmbeds(embed("Offer marked as completed.\n" +
                "This channel will be deleted in 5 seconds.", "Offer `" + offer.getUuid() + "` completed", GREEN)).queue();

        UserRepository userRepository = AppCtx.context().getBean(UserRepository.class);

        User initiator = userRepository.findByDisordID(offer.getInitiator());
        User worker = userRepository.findByDisordID(offer.getWorker());

        worker.setWithdrawableBalance(worker.getWithdrawableBalance() + offer.getCoinAmountInCredits());
        initiator.setOnHold(initiator.getOnHold() - offer.getCoinAmountInCredits());
        offer.setStatus(OfferStatus.COMPLETED);

        offerRepository.save(offer);
        userRepository.save(initiator);
        userRepository.save(worker);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Guild main = Skyblock.getInstance().getDiscordApplication().getLoggingGuild();

                TextChannel offerChannel = main.getTextChannelById(Skyblock.LOGGING_TEST_OFFERS_CHANNEL);

                offerChannel.deleteMessageById(offer.getBroadcastedMessageID()).queue();
                event.getChannel().delete().queue();
            }
        }, 5_000);

    }

    private void offerPartiallyCompletedModal(ButtonInteractionEvent event, User user, String id) {
        if (user.getRole() != Role.PARTNER) {
            event.replyEmbeds(embed("Only partners can mark orders as completed", "You cannot perform that action", RED)).setEphemeral(true).queue();
            return;
        }

        OfferRepository offerRepository = AppCtx.context().getBean(OfferRepository.class);

        Offer offer = offerRepository.findByUuid(id);

        if (offer.getStatus() != OfferStatus.IN_WORK) {
            event.replyEmbeds(embed("This offer is already completed, wait for this ticket to delete!", null, RED)).setEphemeral(true).queue();
            return;
        }

        TextInput textInput = TextInput.create("offer_amount_paid", "Enter the amount of coins you transferred", TextInputStyle.SHORT).build();

        Modal modal = Modal.create("offer_modal_partial_complete_" + offer.getUuid(), "Mark this order as partially completed")
                .addComponents(
                        ActionRow.of(textInput)
                )
                .build();

        event.replyModal(modal).queue();
    }

    private void offerPartiallyCompleted(ModalInteractionEvent event, User user, String id){
        OfferRepository offerRepository = AppCtx.context().getBean(OfferRepository.class);

        Offer offer = offerRepository.findByUuid(id);

        int coins = Integer.parseInt(event.getValues().get(0).getAsString());

        if(coins >= offer.getCoinAmount()){
            event.replyEmbeds(embed("You cannot make the coin amount equal to or higher  than the initial value!", null, RED)).setEphemeral(true).queue();
            return;
        }

        double price;

        double l = coins / 1_000_000.0D;

        if (coins < Skyblock.THRESHOLD) {
            price = l * Skyblock.TOP_PRICE;
        } else {
            price = l * Skyblock.BOTTOM_PRICE;
        }

        event.replyEmbeds(embed("Offer marked as partially completed.\n" +
                "This channel will be deleted in 5 seconds.", "Offer `" + offer.getUuid() + "` completed", GREEN)).queue();

        UserRepository userRepository = AppCtx.context().getBean(UserRepository.class);

        User initiator = userRepository.findByDisordID(offer.getInitiator());
        User worker = userRepository.findByDisordID(offer.getWorker());

        double oldprice = offer.getCoinAmountInCredits();
        double difference = oldprice - price;

        worker.setWithdrawableBalance(worker.getWithdrawableBalance() + price);
        initiator.setOnHold(initiator.getOnHold() - offer.getCoinAmountInCredits());
        initiator.setBalance(initiator.getBalance() + difference);

        offer.setCoinAmount(coins);
        offer.setCoinAmountInCredits(price);
        offer.setStatus(OfferStatus.PARTIALLY_COMPLETED);

        offerRepository.save(offer);
        userRepository.save(initiator);
        userRepository.save(worker);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Guild main = Skyblock.getInstance().getDiscordApplication().getLoggingGuild();

                TextChannel offerChannel = main.getTextChannelById(Skyblock.LOGGING_TEST_OFFERS_CHANNEL);

                offerChannel.deleteMessageById(offer.getBroadcastedMessageID()).queue();
                event.getChannel().delete().queue();
            }
        }, 5_000);
    }

    private void abandonOffer(ButtonInteractionEvent event, User user, String id) {
        event.replyEmbeds(embed("Abandoning this offer and broadcasting it to other partners again\n" +
                "This channel will be deleted in 5 seconds.", "Offer abandoned", CYAN)).queue();

        OfferRepository offerRepository = AppCtx.context().getBean(OfferRepository.class);

        Offer offer = offerRepository.findByUuid(id);

        if (offer.getStatus() != OfferStatus.IN_WORK) {
            return;
        }

        // todo logging -> main
        Guild main = Skyblock.getInstance().getDiscordApplication().getLoggingGuild();

        TextChannel textChannel = main.getTextChannelById(Skyblock.LOGGING_TEST_OFFERS_CHANNEL);

        if (textChannel == null) {
            event.getHook().sendMessageEmbeds(embed("Offer can not be delivered", "Error", RED)).queue();
            return;
        }

        offer.setStatus(OfferStatus.CREATED);
        offerRepository.save(offer);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                event.getChannel().delete().queue();
                textChannel.deleteMessageById(offer.getBroadcastedMessageID()).queue();
                textChannel.sendMessageEmbeds(
                        embed(
                                "Customer: <@" + offer.getInitiator() + ">\n\n" +
                                        "Coin amount: `" + StringUtility.verboseNumber(offer.getCoinAmount()) + " coins`\n" +
                                        "In credits: `" + String.format("%.2f", offer.getCoinAmountInCredits()) + " credits`", "Offer `" + offer.getUuid() + "`", GREEN
                        )
                ).addActionRow(
                        Button.of(ButtonStyle.SUCCESS, "offer_claim_" + offer.getUuid(), "Claim this order", Emoji.fromUnicode("\uD83D\uDEC4"))
                ).queue(success -> {
                    offer.setBroadcastedMessageID(success.getId());
                    offerRepository.save(offer);
                });
            }
        }, 5_000);
    }

    private void cancelOffer(ButtonInteractionEvent event, User user) {

    }

    private void createOfferModal(ButtonInteractionEvent event, User user) {
        TextInput amount = TextInput
                .create("offer_amount", "Amount of coins to buy", TextInputStyle.SHORT)
                .setRequired(true)
                .setPlaceholder("This value must be a number")
                .build();

        Modal modal = Modal.create("offer_modal", "Place an offer").addComponents(
                ActionRow.of(amount)
        ).build();

        event.replyModal(modal).queue();
    }
}
