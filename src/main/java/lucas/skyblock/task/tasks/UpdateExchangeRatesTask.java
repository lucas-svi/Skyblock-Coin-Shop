package lucas.skyblock.task.tasks;

import lucas.skyblock.chain.ExchangeRates;
import lucas.skyblock.task.Task;
import lucas.skyblock.task.TaskDelay;

public class UpdateExchangeRatesTask extends Task {

    public UpdateExchangeRatesTask(TaskDelay delay) {
        super(delay);
    }

    @Override
    public void execute() {
        super.execute();
        ExchangeRates.getInstance().update();
    }
}