package lucas.skyblock.task;

public class TaskDelay {

    private final int value;
    private final Type type;

    public TaskDelay(int value, Type type){
        this.value = value;
        this.type = type;
    }

    public long toMillis(){
        switch (type){
            case SECONDS:
                return value * 1000L;
            case MINUTES:
                return value * 60000L;
            case HOURS:
                return value * 3600000L;
            case DAYS:
                return value * 86400000L;
            default:
                return value;
        }
    }

    public enum Type{
        MILLISECONDS, SECONDS, MINUTES, HOURS, DAYS
    }

}
