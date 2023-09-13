package managers;

import exceptions.ManagerSaveException;
import model.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTasksManager(File file) {
        super();
        this.file = file;
    }

    public int getTaskCounter() {
        return taskCounter;
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager manager = new FileBackedTasksManager(file);
        try {
            String bigLine = Files.readString(Path.of(file.getPath()));
            if (bigLine.isBlank()) {
                System.out.println("Попытка восстановления из пустого файла");
                return manager;
            }
            String[] split = bigLine.split("\n");
            if (split.length == 1) {
                System.out.println("В файле нет сохраненных задач");
                return manager;
            }
            List<Integer> idStorage = new ArrayList<>();
            for (int i = 1; i < split.length; i++) {
                if (!split[i].isBlank() && !Objects.equals(split[i], "\r")) {
                    Task task = CSVFileOperator.fromString(split[i]);
                    if (task != null) {
                        idStorage.add(task.getId());
                        switch (task.getType()) {
                            case TASK:
                                manager.updateTask(task);
                                break;
                            case EPIC:
                                manager.updateEpic((Epic) task);
                                break;
                            case SUBTASK:
                                manager.getEpicById(((Subtask) task).getEpicId())
                                        .getSubtaskIdList().add(task.getId());
                                manager.updateSubtask((Subtask) task);
                                break;
                            default:
                                System.out.println("Не распознан тип задачи при чтении из файла");
                        }
                    }
                    manager.taskCounter = manager.findMax(idStorage);
                } else {
                    i++;
                    if (i >= split.length) {
                        System.out.println("В файле нет сохраненной истории");
                        return manager;
                    }
                    List<Integer> history = CSVFileOperator.historyFromString(split[i]);
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
                    ++i;
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

    public void save() {
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
    public Task getTaskById(Integer id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Task createTask(String name, String description, LocalDateTime startTime) {
        Task task = super.createTask(name, description, startTime);
        save();
        return task;
    }

    @Override
    public Task createTask(String name, String description, LocalDateTime startTime, Integer duration) {
        Task task = super.createTask(name, description, startTime, duration);
        save();
        return task;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public Task updateTaskStatus(Status status, Integer id) {
        Task modifiedTask = super.updateTaskStatus(status, id);
        save();
        return modifiedTask;
    }

    @Override
    public Task renameTask(String name, String description, Integer id) {
        Task modifiedTask = super.renameTask(name, description, id);
        save();
        return modifiedTask;
    }

    @Override
    public void deleteTaskById(Integer id) {
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
    public Epic getEpicById(Integer id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Epic createEpic(String name, String description, LocalDateTime startTime) {
        Epic epic = super.createEpic(name, description, startTime);
        save();
        return epic;
    }

    @Override
    public Epic createEpic(String name, String description, LocalDateTime startTime, Integer duration) {
        Epic epic = super.createEpic(name, description, startTime, duration);
        save();
        return epic;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public Epic renameEpic(String name, String description, Integer id) {
        Epic modifiedEpic = super.renameEpic(name, description, id);
        save();
        return modifiedEpic;
    }

    @Override
    public void deleteEpicById(Integer id) {
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
    public Subtask getSubtaskById(Integer id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public Subtask createSubtask(String name, String description, LocalDateTime startTime, Integer epicId) {
        Subtask subtask = super.createSubtask(name, description, startTime, epicId);
        save();
        return subtask;
    }

    @Override
    public Subtask createSubtask(String name, String description, LocalDateTime startTime,
                                 Integer duration, Integer epicId) {
        Subtask subtask = super.createSubtask(name, description, startTime, duration, epicId);
        save();
        return subtask;
    }

    @Override
    public Subtask updateSubtaskStatus(Status status, Integer id) {
        Subtask modifiedSubtask = super.updateSubtaskStatus(status, id);
        save();
        return modifiedSubtask;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public Subtask renameSubtask(String name, String description, Integer id) {
        Subtask modifiedSubtask = super.renameSubtask(name, description, id);
        save();
        return modifiedSubtask;
    }

    @Override
    public void deleteSubtaskById(Integer id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public HistoryManager getHistoryManager() {
        return super.getHistoryManager();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return super.getPrioritizedTasks();
    }

}