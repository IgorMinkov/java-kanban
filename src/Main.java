import managers.Manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

public class Main {

    public static void main(String[] args) {

        Manager taskManager = new Manager();

        Task task1 = new Task("имя задачи1", "описание задачи1");
        Task task2 = new Task("имя задачи2", "описание задачи2");

        Epic epic1 = new Epic("Переезд","мы справимся!");
        Subtask subtask11 = new Subtask("Собрать коробки", "на балконе тоже", 3);
        Subtask subtask12 = new Subtask("Упаковать кошку", "спрячется под кровать", 3);

        Epic epic2 = new Epic("Сдать фз3","отправить ревьюеру");
        Subtask subtask21 = new Subtask("Дописать main", "для первичного теста", 4);

        task1 = taskManager.createTask(task1);
        task2 = taskManager.createTask(task2);

        epic1 = taskManager.createEpic(epic1);
        epic2 = taskManager.createEpic(epic2);

        subtask11 = taskManager.createSubtask(subtask11);
        subtask12 = taskManager.createSubtask(subtask12);
        subtask21 = taskManager.createSubtask(subtask21);


        System.out.println(" ");
        System.out.println("задачи: " + taskManager.getAllTasks());
        System.out.println("эпики: " + taskManager.getAllEpics());
        System.out.println("подзадачи: " + taskManager.getAllSubtask());
        System.out.println("\n");


        Task modifiedTask1 = taskManager.getTaskById(task1.getId());
        System.out.println(modifiedTask1);
        modifiedTask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(modifiedTask1);
        System.out.println(taskManager.getTaskById(task1.getId()));
        System.out.println("\n");


        System.out.println(taskManager.getEpicById(epic1.getId()));
        Subtask modifiedSubtask11 = taskManager.getSubtaskById(subtask11.getId());
        System.out.println(modifiedSubtask11);
        modifiedSubtask11.setStatus(Status.DONE);
        taskManager.updateSubtask(modifiedSubtask11);
        System.out.println(taskManager.getSubtaskById(subtask11.getId()));
        System.out.println(taskManager.getEpicById(epic1.getId()));
        System.out.println("\n");

        System.out.println(taskManager.getEpicById(epic2.getId()));
        Subtask modifiedSubtask21 = taskManager.getSubtaskById(subtask21.getId());
        modifiedSubtask21.setStatus(Status.DONE);
        taskManager.updateSubtask(modifiedSubtask21);
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
    }

}
