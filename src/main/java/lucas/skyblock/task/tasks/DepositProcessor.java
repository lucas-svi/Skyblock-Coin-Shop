package lucas.skyblock.task.tasks;

import lucas.skyblock.chain.Bitcoin;
import lucas.skyblock.chain.ExchangeRates;
import lucas.skyblock.chain.entity.CryptoAddress;
import lucas.skyblock.discord.command.Command;
import lucas.skyblock.discord.utilities.EmbedUtil;
import lucas.skyblock.discord.utilities.GuildUtility;
import lucas.skyblock.discord.utilities.Logger;
import lucas.skyblock.mongo.entity.deposit.Deposit;
import lucas.skyblock.mongo.entity.deposit.DepositStatus;
import lucas.skyblock.mongo.entity.user.User;
import lucas.skyblock.mongo.repository.DepositRepository;
import lucas.skyblock.mongo.repository.UserRepository;
import lucas.skyblock.task.Task;
import lucas.skyblock.task.TaskDelay;
import lucas.skyblock.utility.AppCtx;
import lucas.skyblock.utility.StringUtility;
import lucas.skyblock.Skyblock;

import java.awt.*;

public class DepositProcessor extends Task {

    public DepositProcessor(TaskDelay delay) {
        super(delay);
    }

    @Override
    public void execute() {
        super.execute();

        DepositRepository depositRepository = AppCtx.context().getBean(DepositRepository.class);

        for (Deposit deposit : depositRepository.findAllByStatus(DepositStatus.ONGOING_NOT_PAID)) {
            handleNonPaid(deposit);
        }

        for (Deposit deposit : depositRepository.findAllByStatus(DepositStatus.ONGOING_PAID_NOT_CONFIRMED)) {
            handlePaid(deposit);
        }
    }

    private void handleNonPaid(Deposit deposit){
        UserRepository userRepository = AppCtx.context().getBean(UserRepository.class);
        DepositRepository depositRepository = AppCtx.context().getBean(DepositRepository.class);

        User customer = userRepository.findByDisordID(deposit.getInitiator());

        ExchangeRates exchangeRates = ExchangeRates.getInstance();

        CryptoAddress cryptoAddressUnfonfirmed = deposit.getGateway().getBalance(deposit.getCryptoAddress(), 0);
        CryptoAddress cryptoAddress = deposit.getGateway().getBalance(deposit.getCryptoAddress(), 1);

        float confirmedBalance = cryptoAddress.getBalance();
        float unconfirmedBalance = cryptoAddressUnfonfirmed.getBalance();

        double fiatConfirmed = exchangeRates.convertOneToUSD(deposit.getGateway()) * confirmedBalance;
        double fiatUnconfirmed = exchangeRates.convertOneToUSD(deposit.getGateway()) * unconfirmedBalance;

        System.out.println(unconfirmedBalance);

        if(confirmedBalance > 0){
            customer.setBalance(customer.getBalance() + Math.ceil(fiatConfirmed/0.01));
            userRepository.save(customer);

            deposit.setTotalReceived(confirmedBalance);
            deposit.setStatus(DepositStatus.COMPLETED);

            depositRepository.save(deposit);

            GuildUtility.sendPrivateMessageEmbed(
                    Skyblock.getInstance().getDiscordApplication().getMainGuild(),
                    customer.getDisordID(),
                    EmbedUtil.embed(
                            String.format("The transaction is now confirmed on blockchain and your deposit is marked as completed \n"
                                    + "`%.2f` credits were added to your account", Math.ceil(fiatConfirmed/0.01)),
                            String.format("Deposit `%s` is completed", deposit.getUuid()),
                            Command.CYAN)
            );

            log("Deposit " + deposit.getUuid() + " was paid and confirmed (fast-complete)", "Deposit completed", Color.GREEN.darker().getRGB(), Logger.LogType.DEPOSIT_LOG);
            return;
        }

        if(unconfirmedBalance <= 0){
            return;
        }

        userRepository.save(customer);

        deposit.setTotalReceived(unconfirmedBalance);

        deposit.setStatus(DepositStatus.ONGOING_PAID_NOT_CONFIRMED);

        depositRepository.save(deposit);

        double toReceive = Math.ceil(fiatUnconfirmed/0.01);

        GuildUtility.sendPrivateMessageEmbed(
                Skyblock.getInstance().getDiscordApplication().getMainGuild(),
                customer.getDisordID(),
                EmbedUtil.embed( String.format(
                        "We found your payment, your deposit is now marked as paid. \n " +
                                "`%.2f` credits will be added to your account once the transaction gets `%s` confirmation(s). \n " +
                                "Additionally, you can find your transaction here: %s",
                        toReceive,
                        1,
                        String.format("https://blockchair.com/%s/address/%s", deposit.getGateway() instanceof Bitcoin ? "bitcoin" : "litecoin", deposit.getCryptoAddress())
                ), "Deposit paid", Command.CYAN)
        );

        log(String.format("Deposit `%s` by `%s` was paid with `%s %s`, waiting for confirmation", deposit.getUuid(), deposit.getInitiator(), StringUtility.fancyNumber(unconfirmedBalance), deposit.getGateway() instanceof Bitcoin ? "btc" : "ltc"), "Deposit paid", new Color(0, 136, 168), Logger.LogType.DEPOSIT_LOG);
    }

    private void handlePaid(Deposit deposit){
        UserRepository userRepository = AppCtx.context().getBean(UserRepository.class);
        DepositRepository depositRepository = AppCtx.context().getBean(DepositRepository.class);

        ExchangeRates blockChainAPI = ExchangeRates.getInstance();

        User customer = userRepository.findByDisordID(deposit.getInitiator());

        CryptoAddress cryptoAddress = deposit.getGateway().getBalance(deposit.getCryptoAddress(), 1);

        float confirmedBalance = cryptoAddress.getBalance();

        log(String.format(
                "Address: `%s`\n" +
                        "Confirmed Balance: `%s`\n", cryptoAddress.getAddress(), StringUtility.fancyNumber(cryptoAddress.getBalance())
        ), "Querying address", new Color(12, 255, 151), Logger.LogType.BLOCKCHAIN_LOG);

        if(confirmedBalance <= 0){
            return;
        }

        double fiatConfirmed = blockChainAPI.convertOneToUSD(deposit.getGateway()) * confirmedBalance;

        customer.setBalance(customer.getBalance() + Math.ceil(fiatConfirmed/0.01));
        userRepository.save(customer);

        deposit.setTotalReceived(fiatConfirmed);
        deposit.setStatus(DepositStatus.COMPLETED);
        depositRepository.save(deposit);

        GuildUtility.sendPrivateMessageEmbed(
                Skyblock.getInstance().getDiscordApplication().getMainGuild(),
                customer.getDisordID(),
                EmbedUtil.embed(
                        String.format("The transaction is now confirmed on blockchain and your deposit is marked as completed \n"
                                + "`%.2f` credits were added to your account", Math.ceil(fiatConfirmed/0.01)),
                        String.format("Deposit `%s` is completed", deposit.getUuid()),
                        Command.CYAN)
        );

        log("Deposit " + deposit.getUuid() + " was confirmed on blockchain", "Deposit confirmed", Color.GREEN.darker().getRGB(), Logger.LogType.DEPOSIT_LOG);
    }
}
