package managers;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;

public interface TaskManager {

    ArrayList<Task> getAllTasks();

    void deleteAllTasks();

    Task getTaskById(int id);

    Task createTask(String name, String description);

    Task updateTask(Status status, Integer id);

    Task updateTask(String name, String description, Integer id);

    void deleteTaskById(int id);

    ArrayList<Epic> getAllEpics();

    void deleteAllEpics();

    Epic getEpicById(int id);

    Epic createEpic(String name, String description);

    Epic updateEpic(String name, String description, Integer id);

    void deleteEpicById(int id);

    ArrayList<Subtask> getAllSubtask();

    void deleteAllSubtask();

    Subtask getSubtaskById(int id);

    Subtask createSubtask(String name, String description, Integer epicId);

    Subtask updateSubtask(Status status, Integer id);

    Subtask updateSubtask(String name, String description, Integer id);

    void deleteSubtaskById(int id);

    HistoryManager getHistoryManager();

}
