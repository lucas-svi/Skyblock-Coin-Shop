package lucas.skyblock.task;

import lombok.Getter;
import lombok.Setter;
import lucas.skyblock.discord.utilities.Logger;
import lucas.skyblock.task.tasks.DepositExpiryTask;
import lucas.skyblock.task.tasks.UpdateExchangeRatesTask;
import lucas.skyblock.task.tasks.DepositProcessor;

import java.util.ArrayList;
import java.util.List;

public class TaskExecutor {

    private final List<Task> tasks = new ArrayList<>();

    @Getter
    @Setter
    private boolean isLoaded;

    public TaskExecutor(){
        tasks.add(new DepositProcessor(new TaskDelay(10, TaskDelay.Type.SECONDS)));
        tasks.add(new UpdateExchangeRatesTask(new TaskDelay(15, TaskDelay.Type.MINUTES)));
        tasks.add(new DepositExpiryTask(new TaskDelay(5, TaskDelay.Type.SECONDS)));
    }

    public void tick(){
        for (Task task : tasks) {
            if(task.shouldExecute()){
                try{
                    task.execute();
                }catch (Exception e) {
                    e.printStackTrace();
                            Logger.log("Exception while executing task " + task.getClass().getName(), e.getMessage(), 0xffffffff, Logger.LogType.EXCEPTION);
                }
            }
        }
    }

}
