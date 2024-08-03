import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private Epic epic;

    public Subtask(String taskName, String description, int taskId, Status status, Duration duration, LocalDateTime startTime) {
        super(taskName, description, taskId, status, duration, startTime);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    @Override
    public String getType() {
        return "SUBTASK";
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", taskId=" + getTaskId() +
                ", status='" + status + '\'' +
                '}';
    }
}
