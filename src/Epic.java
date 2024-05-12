import java.util.List;

public class Epic extends Task {
    private List<Subtask> subtasks;

    public Epic(String taskName, String description, int taskId, Status status, List<Subtask> subtasks) {
        super(taskName, description, taskId, status);
        this.subtasks = subtasks;
        updateStatus();
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    public void updateStatus() {
        boolean allSubtasksDone = true;
        boolean allSubtasksNew = true;

        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() != Status.DONE) {
                allSubtasksDone = false;
            }
            if (subtask.getStatus() != Status.NEW) {
                allSubtasksNew = false;
            }
        }

        if (allSubtasksDone) {
            setStatus(Status.DONE);
        } else if (allSubtasksNew) {
            setStatus(Status.NEW);
        } else {
            setStatus(Status.IN_PROGRESS);
        }
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

