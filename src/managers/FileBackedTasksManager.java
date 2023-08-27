package managers;

import exceptions.ManagerSaveException;
import model.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTasksManager(File file) {
        super();
        this.file = file;
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(CSVFileOperator.setHeader());
            for (Task task : taskStorage.values()) {
                writer.write(CSVFileOperator.toString(task) + "\n");
            }
            for (Epic epic : epicStorage.values()) {
                writer.write(CSVFileOperator.toString(epic) + "\n");
            }
            for (Subtask subtask : subtaskStorage.values()) {
                writer.write(CSVFileOperator.toString(subtask) + "\n");
            }
            writer.newLine();
            writer.write(CSVFileOperator.historyToString(historyManager) + "\n");
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи в файл: " + e.getMessage());
        }
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager manager = new FileBackedTasksManager(file);
        try {
            String bigLine = Files.readString(Path.of(file.getPath()));
            String[] split = bigLine.split("\n");
            if (split.length == 1) {
                System.out.println("В файле нет сохраненных задач");
                return manager;
            }
            List<Integer> idStorage = new ArrayList<>();
            for (int i = 1; i < split.length; i++) {
                if (!split[i].isEmpty() && !Objects.equals(split[i], "\r")) {
                    Task task = CSVFileOperator.fromString(split[i]);
                    if (task != null) {
                        idStorage.add(task.getId());
                        switch (task.getType()) {
                            case TASK:
                                manager.taskStorage.put(task.getId(), task);
                                break;
                            case EPIC:
                                manager.epicStorage.put(task.getId(), (Epic) task);
                                break;
                            case SUBTASK:
                                manager.subtaskStorage.put(task.getId(), (Subtask) task);
                                manager.getEpicById(((Subtask) task).getEpicId())
                                        .getSubtaskIdList().add(task.getId());
                                break;
                            default:
                                System.out.println("Не распознан тип задачи при чтении из файла");
                        }
                    }
                    manager.taskCounter = manager.findMax(idStorage);
                } else {
                    if (!split[i + 1].isEmpty()) {
                        List<Integer> history = CSVFileOperator.historyFromString(split[i + 1]);
                        if (!history.isEmpty()) {
                            for (Integer id : history) {
                                if (manager.taskStorage.containsKey(id)) {
                                    manager.historyManager.addTask(manager.getTaskById(id));
                                } else if (manager.epicStorage.containsKey(id)) {
                                    manager.historyManager.addTask(manager.getEpicById(id));
                                } else if (manager.subtaskStorage.containsKey(id)) {
                                    manager.historyManager.addTask(manager.getSubtaskById(id));
                                } else {
                                    System.out.println("не найдена задача с таким id");
                                }
                            }
                        }
                        i++;
                    } else {
                        System.out.println("В файле нет сохраненной истории");
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения из файла: " + e.getMessage());
        }
        return manager;
    }

    private Integer findMax(List<Integer> list) {
        int max = 0;
        for (Integer value : list) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    public int getTaskCounter() {
        return taskCounter;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return super.getAllTasks();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Task createTask(String name, String description) {
        Task task = super.createTask(name, description);
        save();
        return task;
    }

    @Override
    public Task updateTask(Status status, Integer id) {
        Task modifiedTask = super.updateTask(status, id);
        save();
        return modifiedTask;
    }

    @Override
    public Task updateTask(String name, String description, Integer id) {
        Task modifiedTask = super.updateTask(name, description, id);
        save();
        return modifiedTask;
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return super.getAllEpics();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Epic createEpic(String name, String description) {
        Epic epic = super.createEpic(name, description);
        save();
        return epic;
    }

    @Override
    public Epic updateEpic(String name, String description, Integer id) {
        Epic modifiedEpic = super.updateEpic(name, description, id);
        save();
        return modifiedEpic;
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public ArrayList<Subtask> getAllSubtask() {
        return super.getAllSubtask();
    }

    @Override
    public void deleteAllSubtask() {
        super.deleteAllSubtask();
        save();
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public Subtask createSubtask(String name, String description, Integer epicId) {
        Subtask subtask = super.createSubtask(name, description, epicId);
        save();
        return subtask;
    }

    @Override
    public Subtask updateSubtask(Status status, Integer id) {
        Subtask modifiedSubtask = super.updateSubtask(status, id);
        save();
        return modifiedSubtask;
    }

    @Override
    public Subtask updateSubtask(String name, String description, Integer id) {
        Subtask modifiedSubtask = super.updateSubtask(name, description, id);
        save();
        return modifiedSubtask;
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public HistoryManager getHistoryManager() {
        return super.getHistoryManager();
    }

    @Override
    public void updateEpicStatus(int epicId) {
        super.updateEpicStatus(epicId);
    }
}