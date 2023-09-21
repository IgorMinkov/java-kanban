import Server.KVServer;
import managers.HttpTaskManager;
import model.Epic;
import model.Subtask;
import model.Task;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static Server.HttpTaskServer.PORT;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {

    private final File file = new File("./resources/TestSaveToBackupFile.csv");
    private final String url = "http://localhost:" + PORT + "/";
    private KVServer kvServ;
    private Epic epic;
    private Subtask subtask;

    @Override
    public HttpTaskManager createManager() {
        try {
            kvServ = new KVServer();
            kvServ.start();
            taskManager = new HttpTaskManager(file, url);
        } catch (IOException e) {
            System.out.println("Ошибка при создании менеджера");
        }
        return taskManager;
    }

    @BeforeEach
    void setUp() {
        LocalDateTime time = LocalDateTime.of(2000, 1, 1, 1, 1);
        Task task = taskManager.createTask("имя задачи1", "описание задачи1",
                time.plusHours(1), 30);
        epic = taskManager.createEpic("имя эпика1", "описание эпика1",
                time.plusHours(6), 30);
        subtask = taskManager.createSubtask("имя подзадачи", "описание подзадачи",
                time.plusMinutes(40), 15, epic.getId());

        taskManager.getTaskById(task.getId());
        taskManager.getSubtaskById(subtask.getId());
        taskManager.getTaskById(task.getId());
    }

    @Test
    void loadFromServer() {
        System.out.println("------------восстанавливаем с сервера-----------");

        HttpTaskManager newManager = new HttpTaskManager(file, url);
        newManager.loadFromServer(newManager);

        final ArrayList<Task> tasks = newManager.getAllTasks();
        assertEquals(1, tasks.size(), "не вернулись задачи с сервера");

        final ArrayList<Epic> epics = newManager.getAllEpics();
        assertEquals(1, epics.size(), "не вернулись эпики с сервера");

        final ArrayList<Subtask> subtasks = newManager.getAllSubtask();
        assertEquals(1, subtasks.size(), "не вернулись подзадачи с сервера");

        final List<Task> history = newManager.getHistoryManager().getHistory();
        final List<Integer> historyIds = history.stream().map(Task::getId).collect(Collectors.toList());
        assertEquals("[2, 3, 1]", historyIds.toString(), "не вернулась история с сервера");

        assertEquals(3, newManager.getTaskCounter(), "некорректно восстановился счетчик задач");

        final Epic newEpic = taskManager.getEpicById(epic.getId());
        assertEquals(epic,newEpic, "некорректное восстановление с сервера");
        assertEquals("[3]", newEpic.getSubtaskIdList().toString(),
                "эпик не видит подзадачу после восстановления");

        final Subtask mewSubtask = taskManager.getSubtaskById(subtask.getId());
        assertEquals(newEpic.getId(),mewSubtask.getEpicId(), "подзадача не видит" +
                " эпик после восстановления");
    }

    @AfterEach
    void tearDown() {
        kvServ.stop(2);
    }
}