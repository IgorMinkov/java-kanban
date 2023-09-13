import managers.CSVFileOperator;
import managers.HistoryManager;
import managers.InMemoryHistoryManager;
import model.Status;
import model.Task;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

class CSVFileOperatorTest {

    private Task task1;
    private Task task2;
    private LocalDateTime time;

    @BeforeEach
    void setUp() {
        time = LocalDateTime.of(2000, 1, 1, 1, 1);
        task1 = new Task("имя задачи1", "описание задачи1", Status.NEW,
                time, 30);
        task1.setId(1);

        task2 = new Task("имя задачи2", "описание задачи2", Status.NEW,
                time.plusHours(6), 30);
        task2.setId(2);
    }

    @Test
    void testTaskToString() {
        final String line = CSVFileOperator.toString(task1);
        assertEquals("1,TASK,имя задачи1,NEW,описание задачи1,01.01.2000 01:01,30", line,
                "некорректное отображение задачи после перевода в строку");

        final String line2 = CSVFileOperator.toString(null);
        assertNull(line2, "не пройден тест на входящий null в методе ToString");

        final Task task3 = new Task("имя задачи3", "описание задачи3", time);
        task3.setId(3);
        final String line3 = CSVFileOperator.toString(task3);
        assertEquals("3,TASK,имя задачи3,NEW,описание задачи3,01.01.2000 01:01,0", line3,
                "ошибка в методе ToString при работе с устаревшим конструктором задачи");
    }

    @Test
    void fromString() {
        final String testLine = "1,TASK,имя задачи1,NEW,описание задачи1,01.01.2000 01:01,30";
        final Task testTASK = CSVFileOperator.fromString(testLine);
        assertEquals(task1, testTASK, "некорректный парсинг задачи из строки");

        final Task testTask2 = CSVFileOperator.fromString("");
        assertNull(testTask2);

        final Task testTask3 = CSVFileOperator.fromString("       ");
        assertNull(testTask3);

        final NumberFormatException exception = assertThrows(NumberFormatException.class,
                () -> CSVFileOperator.fromString("random raw"));
        assertEquals("For input string: \"random raw\"", exception.getMessage(),
                "ошибка в тесте при проверке неподходящей строки");

        final String testLine2 = "1,TEST,имя задачи1,NEW,описание задачи1,01.01.2000 01:01,30";
        final IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> CSVFileOperator.fromString(testLine2));
        assertEquals("No enum constant model.TaskType.TEST", ex.getMessage(),
                "ошибка в тесте при проверке неизвестного типа задачи");
    }

    @Test
    void historyToString() {
        final HistoryManager historyManager = new InMemoryHistoryManager();
        historyManager.addTask(task1);
        historyManager.addTask(task2);
        final String testLine = CSVFileOperator.historyToString(historyManager);
        assertEquals("1,2", testLine, "ошибка при переводе истории задач в строку");

        final HistoryManager emptyHistoryManager = new InMemoryHistoryManager();
        final String emptyTestLine = CSVFileOperator.historyToString(emptyHistoryManager);
        assertTrue(emptyTestLine.isEmpty());
    }

    @Test
    void historyFromString() {
        final List<Integer> history = CSVFileOperator.historyFromString("1,2");
        assertEquals(1, history.get(0));

        final List<Integer> emptyHistory = CSVFileOperator.historyFromString("");
        assertTrue(emptyHistory.isEmpty());

        final List<Integer> blankHistory = CSVFileOperator.historyFromString("       ");
        assertTrue(blankHistory.isEmpty());
    }
}