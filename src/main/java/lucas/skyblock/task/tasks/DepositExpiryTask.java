package lucas.skyblock.task.tasks;

import lucas.skyblock.discord.command.Command;
import lucas.skyblock.discord.utilities.EmbedUtil;
import lucas.skyblock.discord.utilities.GuildUtility;
import lucas.skyblock.discord.utilities.Logger;
import lucas.skyblock.mongo.entity.deposit.Deposit;
import lucas.skyblock.mongo.entity.deposit.DepositStatus;
import lucas.skyblock.mongo.repository.DepositRepository;
import lucas.skyblock.utility.AppCtx;
import lucas.skyblock.Skyblock;
import lucas.skyblock.task.Task;
import lucas.skyblock.task.TaskDelay;

import java.awt.*;
import java.util.List;

public class DepositExpiryTask extends Task {
    public DepositExpiryTask(TaskDelay delay) {
        super(delay);
    }

    @Override
    public void execute() {
        super.execute();

        DepositRepository depositRepository = AppCtx.context().getBean(DepositRepository.class);

        List<Deposit> deposits = depositRepository.findAllByStatus(DepositStatus.ONGOING_NOT_PAID);

        for (Deposit deposit : deposits) {
            if(System.currentTimeMillis() > deposit.getExpiresAt()){
                deposit.setStatus(DepositStatus.EXPIRED);

                GuildUtility.sendPrivateMessageEmbed(Skyblock.getInstance().getDiscordApplication().getMainGuild(),
                        deposit.getInitiator(),
                        EmbedUtil.embed(
                                String.format("Your deposit `%s` has expired!", deposit.getUuid()) + "\n" +
                                        "If you paid it and see this message, contact " + Skyblock.LOCAL_VIEW,
                                "Order expired",
                                new Color(255, 97, 97).getRGB()
                        ));

                Logger.log(String.format("Deposit `%s` has expired", deposit.getUuid()), "Deposit expired", Command.YELLOW, Logger.LogType.DEPOSIT_LOG);

                depositRepository.save(deposit);
            }
        }
    }
}
