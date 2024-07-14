import java.util.List;

public class Epic extends Task {
    private List<Subtask> subtasks;

    public Epic(String taskName, String description, int taskId, Status status, List<Subtask> subtasks) {
        super(taskName, description, taskId, status);
        this.subtasks = subtasks;
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<Subtask> subtasks) {
        this.subtasks = subtasks;
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

