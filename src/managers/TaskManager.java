package managers;

import model.Epic;
import model.Subtask;
import model.Task;
import model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskManager {

    List<Task> getAllTasks();

    void deleteAllTasks();

    Task getTaskById(Integer id);

    Task createTask(String name, String description, LocalDateTime startTime);

    Task createTask(String name, String description, LocalDateTime startTime, Integer duration);

    void updateTask(Task task);

    Task updateTaskStatus(Status status, Integer id);

    Task renameTask(String name, String description, Integer id);

    void deleteTaskById(Integer id);

    List<Epic> getAllEpics();

    void deleteAllEpics();

    Epic getEpicById(Integer id);

    Epic createEpic(String name, String description, LocalDateTime startTime);

    Epic createEpic(String name, String description, LocalDateTime startTime, Integer duration);

    void updateEpic(Epic epic);

    Epic renameEpic(String name, String description, Integer id);

    void deleteEpicById(Integer id);

    List<Subtask> getAllSubtask();

    void deleteAllSubtask();

    Subtask getSubtaskById(Integer id);

    Subtask createSubtask(String name, String description, LocalDateTime startTime, Integer epicId);

    Subtask createSubtask(String name, String description, LocalDateTime startTime,
                          Integer duration, Integer epicId);

    void updateSubtask(Subtask subtask);

    Subtask updateSubtaskStatus(Status status, Integer id);

    Subtask renameSubtask(String name, String description, Integer id);

    void deleteSubtaskById(Integer id);

    HistoryManager getHistoryManager();

    List<Task> getPrioritizedTasks();

}
