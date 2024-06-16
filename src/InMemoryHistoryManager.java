import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private Node head;
    private Node tail;
    private Map<Integer, Node> taskMap;

    public InMemoryHistoryManager() {
        taskMap = new HashMap<>();
    }
    @Override
    public void addTask(Task task) {
        if (task != null) {
            if (taskMap.containsKey(task.getTaskId())) {
                removeNode(taskMap.get(task.getTaskId()));
            }
            Node newNode = new Node(task);
            linkLast(newNode);
            taskMap.put(task.getTaskId(), newNode);
        }
    }
    @Override
    public void remove(int id) {
        if (taskMap.containsKey(id)) {
            removeNode(taskMap.get(id));
        }
    }
    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        Node current = head;
        while (current != null) {
            history.add(current.task);
            current = current.next;
        }
        return history;
    }

    private void linkLast(Node newNode) {
        if (tail == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
    }
    private void removeNode(Node node) {
        if (node == null) return;

        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }

        taskMap.remove(node.task.getTaskId());
    }

    public Node getHead() {
        return head;
    }
}
