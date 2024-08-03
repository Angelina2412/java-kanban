import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class Epic extends Task {
    private List<Subtask> subtasks;

    public Epic(String taskName, String description, int taskId, Status status) {
        super(taskName, description, taskId, status);
        this.subtasks = null;
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
        Duration totalDuration = Duration.ZERO;
        LocalDateTime earliestStartTime = null;

        for (Subtask subtask : subtasks) {
            if (subtask != null) {
                totalDuration = totalDuration.plus(subtask.getDuration());
                if (earliestStartTime == null || subtask.getStartTime().isBefore(earliestStartTime)) {
                    earliestStartTime = subtask.getStartTime();
                }
            }
        }

        this.duration = totalDuration;
        this.startTime = earliestStartTime;
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

