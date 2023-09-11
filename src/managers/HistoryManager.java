package managers;

import model.Task;

import java.util.List;

public interface HistoryManager {

    void addTask(Task task);

    void remove(Integer id);

    List<Task> getHistory();

}