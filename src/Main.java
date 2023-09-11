import managers.FileBackedTasksManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.io.File;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {

        File file = new File("./resources/BackupFile.csv");

        LocalDateTime now = LocalDateTime.now();

        FileBackedTasksManager manager = new FileBackedTasksManager(file);

        Task task1 = manager.createTask("имя задачи1", "описание задачи1",
                now.plusHours(1), 30);
        Task task2 = manager.createTask("имя задачи2", "описание задачи2",
                now.plusMinutes(2), 15);

        System.out.println("\n");
        System.out.println("Задачи добавляются в список по времени старта:");
        System.out.println(manager.getPrioritizedTasks());


        Epic epic1 = manager.createEpic("Переезд", "мы справимся!", now.plusMinutes(5), 10);
        Epic epic2 = manager.createEpic("Сдать фз7", "отправить ревьюеру",
                now.plusMinutes(30), 20);

        Subtask subtask11 = manager.createSubtask("Собрать коробки", "на балконе тоже",
                now.plusHours(2), 55, epic1.getId());
        Subtask subtask12 = manager.createSubtask("Упаковать кошку", "спрячется под кровать",
                now.plusHours(3),20, epic1.getId());
        Subtask subtask13 = manager.createSubtask("Проверить квартиру", "выключить электричество и воду",
                now.plusHours(3).plusMinutes(30), 15, epic1.getId());
        Subtask subtask21 = manager.createSubtask("Тесты в main", "заполнить файл информацией",
                now.plusMinutes(40), 15, epic2.getId());

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
        manager.updateSubtaskStatus(Status.DONE, subtask21.getId());
        manager.updateSubtaskStatus(Status.DONE, subtask11.getId());
        manager.getSubtaskById(subtask21.getId());
        manager.getEpicById(epic2.getId());

        System.out.println("история просмотра2 - больше данных и проверка смены статусов:");
        manager.getHistoryManager().getHistory().forEach(x -> System.out.println(x.toString()));
        System.out.println("\n");
        System.out.println("последний id: " + manager.getTaskCounter());
        System.out.println("\n");

        System.out.println("Список проверенных по времени старта задач - 2,8,1,5,6,7:");
        System.out.println(manager.getPrioritizedTasks());
        System.out.println("\n");

        System.out.println("---здесь восстанавливаем из файла---");
        FileBackedTasksManager newManager = FileBackedTasksManager.loadFromFile(file);

        System.out.println("задачи после: " + newManager.getAllTasks());
        System.out.println("эпики после: " + newManager.getAllEpics());
        System.out.println("подзадачи после: " + newManager.getAllSubtask());
        System.out.println("\n");

        System.out.println("история просмотра после == истории2:");
        newManager.getHistoryManager().getHistory().forEach(x -> System.out.println(x.toString()));
        System.out.println("\n");

        System.out.println("последний id после: " + newManager.getTaskCounter());

        System.out.println("\n");
        System.out.println("Список проверенных по времени старта задач после восстановления - 2,8,1,5,6,7:");
        System.out.println(manager.getPrioritizedTasks());
    }
}
