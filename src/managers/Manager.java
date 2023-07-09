package managers;

import model.Task;
import model.Subtask;
import model.Epic;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {

    HashMap<Integer, Task> taskStorage = new HashMap<>();
    HashMap<Integer, Subtask> subtaskStorage = new HashMap<>();
    HashMap<Integer, Epic> epicStorage = new HashMap<>();

    //<editor-fold desc="метод-идентификатор задач">
    private int taskCounter = 0;

    private Integer generateId() {
        return ++taskCounter;
    }
    //</editor-fold>

    //<editor-fold desc="методы для Task">
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(taskStorage.values());
    }

    public void deleteAllTasks() {
        taskStorage.clear();
    }

    public Task getTaskById(int id) {
        return taskStorage.get(id);
    }

    public Task createTask(Task task) {
        if (task.getName() == null) {
            System.out.println("пустое имя задачи");
            return null;
        }
        int id = generateId();
        task.setId(id);
        taskStorage.put(id, task);
        return task;
    }

    public Task updateTask(Task task) {
        if (task == null || task.getId() == null ) {
            System.out.println("нет такой задачи");
            return null;
        }
        Task oldTask = taskStorage.get(task.getId());
        if (oldTask == null) {
            System.out.println("нет такой задачи");
            return null;
        }
        taskStorage.put(task.getId(), task);
        return task;
    }

    public void deleteTaskById(int id) {
        Task task = taskStorage.get(id);
        if (task == null) {
            System.out.println("нет такой задачи");
            return;
        }
        taskStorage.remove(id);
    }
    //</editor-fold>

    //<editor-fold desc="методы для Epic">
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epicStorage.values());
    }

    public void deleteAllEpics() {
        epicStorage.clear();
        subtaskStorage.clear();
    }

    public Epic getEpicById(int id) {
        return epicStorage.get(id);
    }

    public Epic createEpic(Epic epic) {
        if (epic.getName() == null) {
            System.out.println("пустое имя эпика");
            return null;
        }
        int id = generateId();
        epic.setId(id);
        epicStorage.put(id, epic);
        return epic;
    }

    public Epic updateEpic(Epic epic) {
        if (epic == null || epic.getId() == null ) {
            System.out.println("нет такого эпика");
            return null;
        }
        Epic oldEpic = epicStorage.get(epic.getId());
        if (oldEpic == null) {
            System.out.println("нет такого эпика");
            return null;
        }
            epicStorage.put(epic.getId(), epic);
        return epic;
    }

    public void deleteEpicById(int id) {
        Epic epic = epicStorage.get(id);
        if (epic == null) {
            System.out.println("нет такого эпика");
            return;
        }
        for (Integer i : epicStorage.get(id).getSubtaskIdList()) {
            subtaskStorage.remove(i);
        }
        epicStorage.get(id).getSubtaskIdList().clear();
        epicStorage.remove(id);
    }
    //</editor-fold>

    //<editor-fold desc="методы для Subtask">
    public ArrayList<Subtask> getAllSubtask() {
        return new ArrayList<>(subtaskStorage.values());
    }

    public void deleteAllSubtask() {
        subtaskStorage.clear();
    }

    public Subtask getSubtaskById(int id) {
        return subtaskStorage.get(id);
    }

    public Subtask createSubtask(Subtask subtask) {
        if (subtask.getName() == null) {
            System.out.println("пустое имя подзадачи");
            return null;
        }
        int id = generateId();
        subtask.setId(id);
        subtaskStorage.put(id, subtask);
        getEpicById(subtask.epicId).getSubtaskIdList().add(subtask.getId());
        return subtask;
    }

    public Subtask updateSubtask(Subtask subtask) {
        if (subtask == null || subtask.getId() == null ) {
            System.out.println("нет такой подзадачи");
            return null;
        }
        Task oldTask = subtaskStorage.get(subtask.getId());
        if (oldTask == null) {
            System.out.println("нет такой подзадачи");
            return null;
        }
            subtaskStorage.put(subtask.getId(), subtask);
            updateEpicStatus(subtask.epicId);
            return subtask;
    }

    public void deleteSubtaskById(int id) {
        Subtask subtask = subtaskStorage.get(id);
        if (subtask == null) {
            System.out.println("нет такой подзадачи");
            return;
        }
        getEpicById(subtask.epicId).getSubtaskIdList().remove(subtask.getId());
        subtaskStorage.remove(id);
        updateEpicStatus(subtask.epicId);
    }
    //</editor-fold>


    public void updateEpicStatus(int epicId) {

        int newCounter = 0;
        int doneCounter = 0;

        for (Integer id : epicStorage.get(epicId).getSubtaskIdList()) {
            switch (subtaskStorage.get(id).getStatus()) {
                case "NEW":
                    newCounter++;
                    break;
                case "DONE":
                    doneCounter++;
                    break;
            }
        }

        if (newCounter == epicStorage.get(epicId).getSubtaskIdList().size()) {
            epicStorage.get(epicId).setStatus("NEW");
        } else if (doneCounter == epicStorage.get(epicId).getSubtaskIdList().size()) {
            epicStorage.get(epicId).setStatus("DONE");
        } else {
            epicStorage.get(epicId).setStatus("IN_PROGRESS");
        }
    }

}
