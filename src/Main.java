import managers.FileBackedTasksManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.io.File;

public class Main {

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
        System.out.println("последний id: " + manager.getTaskCounter());
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
    }
}
