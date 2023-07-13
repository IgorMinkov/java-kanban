package managers;

import model.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static final int TASK_VIEW_HISTORY_SIZE = 10;
    private final LinkedList<Task> taskViewHistory = new LinkedList<>();

    @Override
    public void addTask(Task task) {
        if (task == null) {
            System.out.println("попытка добавить несуществующую задачу в историю");
            return;
        }
        taskViewHistory.add(task);
        if (taskViewHistory.size() > TASK_VIEW_HISTORY_SIZE) {
            taskViewHistory.removeFirst();
        }
    }

    @Override
    public List<Task> getHistory() {
        return taskViewHistory;
    }

}
