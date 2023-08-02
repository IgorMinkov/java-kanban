package managers;

import model.Task;
import model.Node;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private Node head = null;
    private Node tail = null;
    private int historySize = 0;

    private final Map<Integer, Node> nodePointer = new HashMap<>();

    @Override
    public void addTask(Task task) {
        if (task == null) {
            System.out.println("попытка добавить несуществующую задачу в историю");
            return;
        }
        if (nodePointer.containsKey(task.getId())) {
            Node node = nodePointer.get(task.getId());
            removeNode(node);
            nodePointer.remove(node.getValue().getId());
        }
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        removeNode(nodePointer.get(id));
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    public void linkLast(Task task) {
        final Node oldTail = tail;
        final Node newNode = new Node(oldTail, task);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.setNext(newNode);
        }
        historySize++;
        nodePointer.put(task.getId(), newNode);
    }

    public List<Task> getTasks() {
        List<Task> numberedTasks = new ArrayList<>();
        if (head == null) {
            System.out.println("Пустая история просмотров");
            return numberedTasks;
        }
        Node currentNode = head;
        while (currentNode != null) {
            numberedTasks.add(currentNode.getValue());
            currentNode = currentNode.getNext();
        }
        return numberedTasks;
    }

    public void removeNode(Node node) {
        if (!nodePointer.containsValue(node))
            return;
        if (node == head && node == tail) {
            head = null;
            tail = null;
        } else if (node == head) {
            head = node.getNext();
            node.getNext().setPrev(null);
            node.setNext(null);
        } else if (node == tail) {
            tail = node.getPrev();
            node.getPrev().setNext(null);
            node.setPrev(null);
        } else {
            node.getPrev().setNext(node.getNext());
            node.getNext().setPrev(node.getPrev());
        }
        nodePointer.remove(node.getValue().getId());
        historySize--;
    }

}
