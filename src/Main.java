import managers.Managers;
import managers.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

        String task1Name = "имя задачи1";
        String task1Description = "описание задачи1";
        String task2Name = "имя задачи2";
        String task2Description = "описание задачи2";

        Task task1 = taskManager.createTask(task1Name, task1Description);
        Task task2 = taskManager.createTask(task2Name, task2Description);

        String epic1Name = "Переезд";
        String epic1Description = "мы справимся!";
        String epic2Name = "Сдать фз3";
        String epic2Description = "отправить ревьюеру";

        String subtask11Name = "Собрать коробки";
        String subtask11Description = "на балконе тоже";
        String subtask12Name = "Упаковать кошку";
        String subtask12Description = "спрячется под кровать";
        String subtask21Name = "Дописать main";
        String subtask21Description = "для первичного теста";

        Epic epic1 = taskManager.createEpic(epic1Name, epic1Description);
        Epic epic2 = taskManager.createEpic(epic2Name, epic2Description);

        System.out.println("история просмотра1:" + taskManager.getHistoryManager().getHistory());
        System.out.println(" ");

        Subtask subtask11 = taskManager.createSubtask(subtask11Name, subtask11Description, epic1.getId());
        Subtask subtask12 = taskManager.createSubtask(subtask12Name, subtask12Description, epic1.getId());
        Subtask subtask21 = taskManager.createSubtask(subtask21Name, subtask21Description, epic2.getId());

        System.out.println("история просмотра2:");
        taskManager.getHistoryManager().getHistory().forEach(x -> System.out.println(x.toString()));
        System.out.println("\n");

        System.out.println(" ");
        System.out.println("задачи: " + taskManager.getAllTasks());
        System.out.println("эпики: " + taskManager.getAllEpics());
        System.out.println("подзадачи: " + taskManager.getAllSubtask());
        System.out.println("\n");


        System.out.println(taskManager.getTaskById(task1.getId()));

        System.out.println(" ");
        System.out.println("история просмотра3:");
        taskManager.getHistoryManager().getHistory().forEach(x -> System.out.println(x.toString()));
        System.out.println("\n");

        taskManager.updateTask(Status.IN_PROGRESS, task1.getId());
        taskManager.updateTask("новое имя задачи1", "", task1.getId());

        System.out.println(taskManager.getTaskById(task1.getId()));
        System.out.println("\n");


        System.out.println("история просмотра4:");
        taskManager.getHistoryManager().getHistory().forEach(x -> System.out.println(x.toString()));
        System.out.println("\n");


        System.out.println(taskManager.getEpicById(epic1.getId()));
        System.out.println(taskManager.getSubtaskById(subtask11.getId()));

        taskManager.updateSubtask(Status.DONE, subtask11.getId());

        System.out.println(taskManager.getSubtaskById(subtask11.getId()));
        System.out.println(taskManager.getEpicById(epic1.getId()));
        System.out.println("\n");


        System.out.println(taskManager.getEpicById(epic2.getId()));

        taskManager.updateSubtask(Status.DONE, subtask21.getId());

        System.out.println(taskManager.getEpicById(epic2.getId()));
        System.out.println("\n");


        taskManager.deleteSubtaskById(subtask12.getId());
        System.out.println(taskManager.getEpicById(epic1.getId())); // проверка смены статуса после удаления подзадачи
        System.out.println("\n");

        taskManager.deleteEpicById(epic2.getId());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtask());

        taskManager.deleteTaskById(task2.getId());
        System.out.println(taskManager.getAllTasks());

        System.out.println("\n");
        System.out.println("история просмотра5:");
        taskManager.getHistoryManager().getHistory().forEach(x -> System.out.println(x.toString()));
    }

}
