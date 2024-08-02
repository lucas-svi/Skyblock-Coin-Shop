package lucas.skyblock.task;

import lombok.Getter;
import lombok.Setter;
import lucas.skyblock.discord.utilities.Logger;

import java.awt.*;

public abstract class Task {

    @Getter
    @Setter
    private long nextExecutionTime;

    @Getter
    private final TaskDelay delay;

    public Task(TaskDelay delay){
        this.delay = delay;
    }

    public void execute(){
        nextExecutionTime = System.currentTimeMillis() + delay.toMillis();
    }

    public boolean shouldExecute(){
        return System.currentTimeMillis() > nextExecutionTime;
    }

    public void log(String log, String title, Color color, Logger.LogType logType){
        Logger.log(log, title, color, logType);
    }

    public void log(String log, String title, int color, Logger.LogType logType){
        Logger.log(log, title, color, logType);
    }

}
