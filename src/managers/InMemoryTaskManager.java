package managers;

import model.Status;
import model.Task;
import model.Subtask;
import model.Epic;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class InMemoryTaskManager implements TaskManager {

    protected Map<Integer, Task> taskStorage = new HashMap<>();
    protected Map<Integer, Subtask> subtaskStorage = new HashMap<>();
    protected Map<Integer, Epic> epicStorage = new HashMap<>();
    protected int taskCounter = 0;

    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    private Integer generateId() {
        return ++taskCounter;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(taskStorage.values());
    }

    @Override
    public void deleteAllTasks() {
        for (Task task: taskStorage.values()) {
            historyManager.remove(task.getId());
        }
        taskStorage.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = taskStorage.get(id);
        historyManager.addTask(task);
        return task;
    }

    @Override
    public Task createTask(String name, String description) {
        if (name == null) {
            System.out.println("не задано имя задачи");
            return null;
        }
        Task task = new Task(name, description);
        int id = generateId();
        task.setId(id);
        taskStorage.put(id, task);
        return task;
    }

    @Override
    public Task updateTask(Status status, Integer id) {
        Task modifiedTask = getTaskById(id);
        if (modifiedTask == null) {
            System.out.println("нет такой задачи");
            return null;
        }
        modifiedTask.setStatus(status);
        return taskStorage.put(modifiedTask.getId(), modifiedTask);
    }

    @Override
    public Task updateTask(String name, String description, Integer id) {
        Task modifiedTask = getTaskById(id);
        if (modifiedTask == null) {
            System.out.println("нет такого эпика");
            return null;
        }
        if (!Objects.equals(name, "")) {
            modifiedTask.setName(name);
        }
        if (!Objects.equals(description, "")) {
            modifiedTask.setDescription(description);
        }
        return taskStorage.put(modifiedTask.getId(), modifiedTask);
    }

    @Override
    public void deleteTaskById(int id) {
        Task task = taskStorage.get(id);
        if (task == null) {
            System.out.println("нет такой задачи");
            return;
        }
        taskStorage.remove(id);
        historyManager.remove(id);
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epicStorage.values());
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic: epicStorage.values()) {
            historyManager.remove(epic.getId());
        }
        epicStorage.clear();
        for (Subtask subtask: subtaskStorage.values()) {
            historyManager.remove(subtask.getId());
        }
        subtaskStorage.clear();
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epicStorage.get(id);
        historyManager.addTask(epic);
        return epic;
    }

    @Override
    public Epic createEpic(String name, String description) {
        if (name == null) {
            System.out.println("не задано имя эпика");
            return null;
        }
        Epic epic = new Epic(name, description);
        int id = generateId();
        epic.setId(id);
        epicStorage.put(id, epic);
        return epic;
    }

    @Override
    public Epic updateEpic(String name, String description, Integer id) {
        Epic modifiedEpic = getEpicById(id);
        if (modifiedEpic == null) {
            System.out.println("нет такого эпика");
            return null;
        }
        if (!Objects.equals(name, " ")) {
            modifiedEpic.setName(name);
        }
        if (!Objects.equals(description, " ")) {
            modifiedEpic.setDescription(description);
        }
        return epicStorage.put(modifiedEpic.getId(), modifiedEpic);
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epicStorage.get(id);
        if (epic == null) {
            System.out.println("нет такого эпика");
            return;
        }
        for (Integer i : epicStorage.get(id).getSubtaskIdList()) {
            subtaskStorage.remove(i);
            historyManager.remove(i);
        }
        epicStorage.get(id).getSubtaskIdList().clear();
        epicStorage.remove(id);
        historyManager.remove(id);
    }

    @Override
    public ArrayList<Subtask> getAllSubtask() {
        return new ArrayList<>(subtaskStorage.values());
    }

    @Override
    public void deleteAllSubtask() {
        for (Subtask subtask: subtaskStorage.values()) {
            historyManager.remove(subtask.getId());
        }
        subtaskStorage.clear();
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtaskStorage.get(id);
        historyManager.addTask(subtask);
        return subtask;
    }

    @Override
    public Subtask createSubtask(String name, String description, Integer epicId) {
        if (name == null) {
            System.out.println("не задано имя подзадачи");
            return null;
        }
        if (epicId == null) {
            System.out.println("не найден эпик для подзадачи");
            return null;
        }
        Subtask subtask = new Subtask(name, description, epicId);
        int id = generateId();
        subtask.setId(id);
        subtaskStorage.put(id, subtask);
        getEpicById(subtask.getEpicId()).getSubtaskIdList().add(subtask.getId());
        return subtask;
    }

    @Override
    public Subtask updateSubtask(Status status, Integer id) {
        Subtask modifiedSubtask = getSubtaskById(id);
        if (modifiedSubtask == null) {
            System.out.println("нет такой подзадачи");
            return null;
        }
        modifiedSubtask.setStatus(status);
        updateEpicStatus(modifiedSubtask.getEpicId());
        return subtaskStorage.put(modifiedSubtask.getId(), modifiedSubtask);
    }

    @Override
    public Subtask updateSubtask(String name, String description, Integer id) {
        Subtask modifiedSubtask = getSubtaskById(id);
        if (modifiedSubtask == null) {
            System.out.println("нет такого эпика");
            return null;
        }
        if (!Objects.equals(name, "")) {
            modifiedSubtask.setName(name);
        }
        if (!Objects.equals(description, "")) {
            modifiedSubtask.setDescription(description);
        }
        return subtaskStorage.put(modifiedSubtask.getId(), modifiedSubtask);
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtaskStorage.get(id);
        if (subtask == null) {
            System.out.println("нет такой подзадачи");
            return;
        }
        getEpicById(subtask.getEpicId()).getSubtaskIdList().remove(subtask.getId());
        subtaskStorage.remove(id);
        updateEpicStatus(subtask.getEpicId());
        historyManager.remove(id);
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    public void updateEpicStatus(int epicId) {

        int newCounter = 0;
        int doneCounter = 0;

        for (Integer id : epicStorage.get(epicId).getSubtaskIdList()) {
            switch (subtaskStorage.get(id).getStatus()) {
                case NEW:
                    newCounter++;
                    break;
                case DONE:
                    doneCounter++;
                    break;
            }
        }
        if (newCounter == epicStorage.get(epicId).getSubtaskIdList().size()) {
            epicStorage.get(epicId).setStatus(Status.NEW);
        } else if (doneCounter == epicStorage.get(epicId).getSubtaskIdList().size()) {
            epicStorage.get(epicId).setStatus(Status.DONE);
        } else {
            epicStorage.get(epicId).setStatus(Status.IN_PROGRESS);
        }
    }

}
