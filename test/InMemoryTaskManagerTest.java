import managers.InMemoryTaskManager;

import model.Epic;
import model.Status;
import model.Subtask;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    void updateEpicStatusTest() {
        assertEquals(Status.NEW,epic1.getStatus(), "некорректный статус созданного эпика");

        taskManager.updateSubtaskStatus(Status.IN_PROGRESS, subtask11.getId());
        assertEquals(Status.IN_PROGRESS,epic1.getStatus(), "некорректный статус эпика" +
                "при подзадаче IN_PROGRESS");

        taskManager.updateSubtaskStatus(Status.DONE, subtask11.getId());
        assertEquals(Status.IN_PROGRESS,epic1.getStatus(), "некорректный статус эпика" +
                "при подзадаче DONE");

        taskManager.updateSubtaskStatus(Status.DONE, subtask12.getId());
        taskManager.updateSubtaskStatus(Status.DONE, subtask13.getId());
        assertEquals(Status.DONE,epic1.getStatus(), "некорректный статус эпика" +
                "при всех подзадачах DONE");

        taskManager.updateSubtaskStatus(Status.DONE, subtask21.getId());
        assertEquals(Status.IN_PROGRESS,epic2.getStatus());

        taskManager.deleteSubtaskById(subtask22.getId());
        assertEquals(Status.DONE,epic2.getStatus(), "статус не пересчитывается при удалении задач");

        taskManager.deleteSubtaskById(subtask21.getId());
        assertEquals(Status.NEW,epic2.getStatus(), "не пересчитывается статус при пустом списке подзадач");
    }

    @Test
    void calculateEpicTimeTest() {
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

}