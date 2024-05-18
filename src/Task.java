import java.util.Objects;

public class Task {
    protected String taskName;
    protected String description;
    protected Status status;
    protected int taskId;

    public Task(String taskName, String description, int taskId, Status status) {
        this.taskName = taskName;
        this.description = description;
        this.taskId = taskId;
        this.status = status;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskId == task.taskId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId);
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", taskId=" + taskId +
                ", status='" + status + '\'' +
                '}';
    }
}
