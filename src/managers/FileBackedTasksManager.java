package managers;

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
            throw new ManagerSaveException();
        }
    }

    private static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager manager = new FileBackedTasksManager(file);
        try {
            String bigLine = Files.readString(Path.of(file.getPath()));
            String[] split = bigLine.split("\n");
            if (split.length == 1) {
                System.out.println("В файле нет сохраненных задач");
            } else {
                List <Integer> idStorage = new ArrayList<>();
                for (int i = 1; i < split.length; i++) {
                    if (!split[i].isEmpty() && !Objects.equals(split[i], "\r")) {
                        Task task = CSVFileOperator.fromString(split[i]);
                        if (task != null) {
                            idStorage.add(task.getId());
                            if (task instanceof Epic) {
                                manager.epicStorage.put(task.getId(), (Epic) task);
                            } else if (task instanceof Subtask) {
                                manager.subtaskStorage.put(task.getId(), (Subtask) task);
                                manager.getEpicById(((Subtask) task).getEpicId()).getSubtaskIdList().add(task.getId());
                            } else {
                                manager.taskStorage.put(task.getId(), task);
                            }
                        }
                        manager.taskCounter = manager.findMax(idStorage);
                    } else {
                        if (!split[i+1].isEmpty()) {
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
            }
        } catch (IOException e) {
            System.out.println("Ошибка чтения из файла");
        }
        return manager;
    }

    private Integer findMax (List <Integer> list) {
        int max = 0;
        for (Integer value : list) {
            if (value > max) {
                max = value;
            }
        }
        return max;
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

    public static void main(String[] args) {

        File file = new File("./resources/BackupFile.csv");

        FileBackedTasksManager manager = new FileBackedTasksManager(file);

        String task1Name = "имя задачи1";
        String task1Description = "описание задачи1";
        String task2Name = "имя задачи2";
        String task2Description = "описание задачи2";

        Task task1 = manager.createTask(task1Name, task1Description);
        Task task2 = manager.createTask(task2Name, task2Description);

        String epic1Name = "Переезд";
        String epic1Description = "мы справимся!";
        String epic2Name = "Сдать фз6";
        String epic2Description = "отправить ревьюеру";

        Epic epic1 = manager.createEpic(epic1Name, epic1Description);
        Epic epic2 = manager.createEpic(epic2Name, epic2Description);

        String subtask11Name = "Собрать коробки";
        String subtask11Description = "на балконе тоже";
        String subtask12Name = "Упаковать кошку";
        String subtask12Description = "спрячется под кровать";
        String subtask13Name = "Проверить квартиру";
        String subtask13Description = "выключить электричество и перекрыть воду";
        String subtask21Name = "Тесты в main";
        String subtask21Description = "заполнить файл информацией";

        Subtask subtask11 = manager.createSubtask(subtask11Name, subtask11Description, epic1.getId());
        Subtask subtask12 = manager.createSubtask(subtask12Name, subtask12Description, epic1.getId());
        Subtask subtask13 = manager.createSubtask(subtask13Name, subtask13Description, epic1.getId());
        Subtask subtask21 = manager.createSubtask(subtask21Name, subtask21Description, epic2.getId());

        System.out.println("\n");
        System.out.println("задачи: " + manager.getAllTasks());
        System.out.println("эпики: " + manager.getAllEpics());
        System.out.println("подзадачи: " + manager.getAllSubtask());
        System.out.println("\n");

        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());
        manager.getEpicById(epic1.getId());
        manager.getSubtaskById(subtask13.getId());
        manager.getSubtaskById(subtask13.getId());
        manager.getSubtaskById(subtask12.getId());

        System.out.println("история просмотра1:");
        manager.getHistoryManager().getHistory().forEach(x -> System.out.println(x.toString()));
        System.out.println("\n");

        manager.getSubtaskById(subtask11.getId());
        manager.updateSubtask(Status.DONE, subtask21.getId());
        manager.updateSubtask(Status.DONE, subtask11.getId());
        manager.getSubtaskById(subtask21.getId());
        manager.getEpicById(epic2.getId());

        System.out.println("история просмотра2 - больше данных и проверка смены статусов:");
        manager.getHistoryManager().getHistory().forEach(x -> System.out.println(x.toString()));
        System.out.println("\n");
        System.out.println("последний id: " + manager.taskCounter);
        System.out.println("\n");

        System.out.println("---здесь восстанавливаем из файла---");
        FileBackedTasksManager newManager = loadFromFile(file);

        System.out.println("задачи после: " + newManager.getAllTasks());
        System.out.println("эпики после: " + newManager.getAllEpics());
        System.out.println("подзадачи после: " + newManager.getAllSubtask());
        System.out.println("\n");

        System.out.println("история просмотра после == истории2:");
        newManager.getHistoryManager().getHistory().forEach(x -> System.out.println(x.toString()));
        System.out.println("\n");

        System.out.println("последний id после: " + newManager.taskCounter);
    }

    public static class ManagerSaveException extends RuntimeException {

        public ManagerSaveException() {
        }
    }
}