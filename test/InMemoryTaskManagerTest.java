import exceptions.ManagerTimeValidateException;
import managers.InMemoryTaskManager;

import model.Epic;
import model.Status;
import model.Subtask;

import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    private Epic epic1;
    private Epic epic2;
    private Epic epic3;
    private Subtask subtask11;
    private Subtask subtask12;
    private Subtask subtask13;
    private Subtask subtask21;
    private Subtask subtask22;

    @Override
    public InMemoryTaskManager createManager() {
        taskManager =  new InMemoryTaskManager();
        return taskManager;
    }

    @BeforeEach
    void setUp() {
        epic1 = taskManager.createEpic("Переезд", "мы справимся!",
                time.plusMinutes(5), 10);
        epic2 = taskManager.createEpic("Сдать фз7", "отправить ревьюеру",
                time.plusMinutes(30), 20);
        epic3 = taskManager.createEpic("эпик без подзадач", "описание эпика",
                time.plusMinutes(4), 20);

        subtask11 = taskManager.createSubtask("Собрать коробки", "на балконе тоже",
                time.plusHours(2), 55, epic1.getId());
        subtask12 = taskManager.createSubtask("Упаковать кошку", "спрячется под кровать",
                time.plusHours(3),20, epic1.getId());
        subtask13 = taskManager.createSubtask("Проверить квартиру", "перекрыть воду",
                time.plusHours(3).plusMinutes(30), 15, epic1.getId());
        subtask21 = taskManager.createSubtask("Тесты статуса", "заполнить файл информацией",
                time.plusMinutes(40), 15, epic2.getId());
        subtask22 = taskManager.createSubtask("Удаление этой подзадачи", "изменит статус эпика",
                time.plusMinutes(40), 15, epic2.getId());
    }

    @Test
    void updateEpicStatus() {
        assertEquals(Status.NEW,epic1.getStatus(), "некорректный статус созданного эпика");

        subtask11.setStatus(Status.IN_PROGRESS);
        taskManager.updateEpicStatus(epic1.getId());
        assertEquals(Status.IN_PROGRESS,epic1.getStatus(), "некорректный статус эпика" +
                "при подзадаче IN_PROGRESS");

        subtask11.setStatus(Status.DONE);
        taskManager.updateEpicStatus(epic1.getId());
        assertEquals(Status.IN_PROGRESS,epic1.getStatus(), "некорректный статус эпика" +
                "при подзадаче DONE");

        subtask12.setStatus(Status.DONE);
        subtask13.setStatus(Status.DONE);
        taskManager.updateEpicStatus(epic1.getId());
        assertEquals(Status.DONE,epic1.getStatus(), "некорректный статус эпика" +
                "при всех подзадачах DONE");

        subtask21.setStatus(Status.DONE);
        taskManager.updateEpicStatus(epic2.getId());
        assertEquals(Status.IN_PROGRESS,epic2.getStatus());

        taskManager.deleteSubtaskById(subtask22.getId());
        taskManager.updateEpicStatus(epic2.getId());
        assertEquals(Status.DONE,epic2.getStatus(), "статус не пересчитывается при удалении задач");

        taskManager.deleteSubtaskById(subtask21.getId());
        taskManager.updateEpicStatus(epic2.getId());
        assertEquals(Status.NEW,epic2.getStatus(), "не пересчитывается статус при пустом списке подзадач");
    }

    @Test
    void calculateEpicTime() {
        assertEquals(time.plusMinutes(4), epic3.getStartTime(),
                "ошибка расчета времени начала эпика без подзадач");
        assertEquals(20, epic3.getDuration(),
                "ошибка расчета длительности эпика без подзадач");

        assertEquals(subtask11.getStartTime(), epic1.getStartTime(),
                "время начала эпика не равно времени начала самой ранней его подзадачи");

        final Integer epic1Duration = subtask11.getDuration() + subtask12.getDuration() + subtask13.getDuration();
        assertEquals(epic1Duration, epic1.getDuration(),
                "длительность эпика не равна сумме длительностей его подзадач");

        assertEquals(subtask13.getEndTime(), epic1.getEndTime(),
                "время окончания эпика не равно времени окончания самой поздней его подзадачи");
    }

    @Test
    void getPrioritizedTasks() {
        final List<Task> tasks = taskManager.getPrioritizedTasks();
        assertNotNull(tasks);
    }

    @Test
    void addPrioritizeTask() {
        Task testTask1 = addTask();
        taskManager.addPrioritizeTask(testTask1);
        Task testTask2 = addTask();
        final List<Task> oneTestTask = taskManager.getPrioritizedTasks();
        assertTrue(oneTestTask.contains(testTask1));

        testTask2.setStartTime(time.plusHours(7));
        taskManager.addPrioritizeTask(testTask2);
        final List<Task> bothTestTasks = taskManager.getPrioritizedTasks();
        assertTrue(bothTestTasks.contains(testTask2));
    }

    @Test
    void validateTask() {
        Task testTask1 = addTask();
        taskManager.addPrioritizeTask(testTask1);
        Task testTask2 = addTask();
        testTask2.setStartTime(time.plusMinutes(10));
        final ManagerTimeValidateException exception = assertThrows(ManagerTimeValidateException.class,
                () -> taskManager.validateTask(testTask2));
        assertEquals("Задача id " + testTask2.getId() + " " + testTask2.getName()
                        + " пересекается с задачей id" + testTask1.getId() + " " + testTask1.getName(),
                exception.getMessage(),
                "ошбика - успешная валидация одинаковых задач");
    }
}