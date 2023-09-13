import managers.HistoryManager;
import managers.Managers;
import model.Task;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private Task task1;
    private Task task2;
    private Task task3;
    private Task task4;

    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
        LocalDateTime time = LocalDateTime.of(2000, 1, 1, 12, 0);
        task1 = new Task("имя задачи1", "описание задачи1", time);
        task1.setId(1);
        task2 = new Task("имя задачи2", "описание задачи2", time);
        task2.setId(2);
        task3 = new Task("имя задачи3", "описание задачи3", time);
        task3.setId(3);
        task4 = null;
    }

    @Test
    void addTask() {
        historyManager.addTask(task1);
        final List<Task> historyOfOne = historyManager.getHistory();
        assertNotNull(historyOfOne, "не создается история задач");
        assertEquals(1, historyOfOne.size(),"добавленная задача не отображается в истории");

        historyManager.addTask(task4);
        final List<Task> historyTryNull = historyManager.getHistory();
        assertEquals(1, historyTryNull.size(),"в историю добавился null");

        historyManager.addTask(task1);
        final List<Task> historyNotDoubled  = historyManager.getHistory();
        assertEquals(1, historyNotDoubled.size(),"задача задвоилась в истории");
    }

    @Test
    void remove() {
        historyManager.addTask(task1);
        historyManager.addTask(task2);
        historyManager.remove(task2.getId());
        final List<Task> historyOfTask1 = historyManager.getHistory();
        assertEquals(1, historyOfTask1.size(),"ошибка удаления задачи из конца истории");

        historyManager.addTask(task3);
        historyManager.addTask(task2);
        historyManager.remove(task3.getId());
        final List<Task> historyOfTask1And2 = historyManager.getHistory();
        assertEquals(2, historyOfTask1And2.size(),"ошибка удаления задачи из середины истории");

        historyManager.addTask(task3);
        historyManager.remove(task1.getId());
        final List<Task> historyOfTask2And3 = historyManager.getHistory();
        assertEquals(2, historyOfTask2And3.size(),"ошибка удаления задачи из начала истории");

        historyManager.remove(99);
        assertEquals(2, historyManager.getHistory().size(),"задача с несуществующим id удалена");
    }

    @Test
    void getHistory() {
        final List<Task> emptyHistory = historyManager.getHistory();
        assertNotNull(emptyHistory, "метод не возвращает историю задач");
        assertTrue(emptyHistory.isEmpty(), "история не пустая");
    }
}