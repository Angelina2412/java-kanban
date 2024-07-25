import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class Epic extends Task {
    private List<Subtask> subtasks;
    private Duration totalDuration;

    public Epic(String taskName, String description, int taskId, Status status, Duration duration, LocalDateTime startTime) {
        super(taskName, description, taskId, status, duration, startTime);
        this.subtasks = null;
        this.totalDuration = duration;
        this.startTime = startTime;
    }

    public Epic(String taskName, String description, int taskId, Status status, List<Subtask> subtasks, Duration duration, LocalDateTime startTime) {
        super(taskName, description, taskId, status, duration, startTime);
        this.subtasks = subtasks;
        calculateTimes();
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<Subtask> subtasks) {
        this.subtasks = subtasks;
    }


    public void calculateTimes() {
        totalDuration = Duration.ZERO;
        startTime = null;

        for (Subtask subtask : subtasks) {
            if (subtask != null) {
                totalDuration = totalDuration.plus(subtask.getDuration());
                if (startTime == null || subtask.getStartTime().isBefore(startTime)) {
                    startTime = subtask.getStartTime();
                }
            }
        }
        this.duration = totalDuration;
    }

    @Override
    public String getType() {
        return "EPIC";
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", taskId=" + getTaskId() +
                ", status='" + status + '\'' +
                '}';
    }
}

