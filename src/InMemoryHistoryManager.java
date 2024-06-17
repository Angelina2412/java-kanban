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
            history.add(current.getTask());
            current = current.getNext();
        }
        return history;
    }

    private void linkLast(Node newNode) {
        if (tail == null) {
            head = tail = newNode;
        } else {
            tail.setNext(newNode);
            newNode.setPrev(tail);
            tail = newNode;
        }
    }

    private void removeNode(Node node) {
        if (node == null) return;

        if (node.getPrev() != null) {
            node.getPrev().setNext(node.getNext());
        } else {
            head = node.getNext();
        }

        if (node.getNext() != null) {
            node.getNext().setPrev(node.getPrev());
        } else {
            tail = node.getPrev();
        }

        taskMap.remove(node.getTask().getTaskId());
    }

    public Node getHead() {
        return head;
    }
}

