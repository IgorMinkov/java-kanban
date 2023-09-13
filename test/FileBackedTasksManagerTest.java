import managers.FileBackedTasksManager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager>{

    private File file = new File("./resources/TestSaveToBackupFile.csv");

    @Override
    public FileBackedTasksManager createManager() {
        taskManager = new FileBackedTasksManager(file);
        return taskManager;
    }

    @Test
    void loadFromFile() {
        file = new File("./resources/TestBackupFile.csv");
        FileBackedTasksManager newManager = FileBackedTasksManager.loadFromFile(file);
        final ArrayList<Task> tasks = newManager.getAllTasks();
        assertEquals(1, tasks.size(), "не прочитались задачи из файла");

        final ArrayList<Epic> epics = newManager.getAllEpics();
        assertEquals(2, epics.size(), "не прочитались эпики из файла");

        final ArrayList<Subtask> subtasks = newManager.getAllSubtask();
        assertEquals(3, subtasks.size(), "не прочитались подзадачи из файла");

        final List<Task> history = newManager.getHistoryManager().getHistory();
        final List<Integer> historyIds = history.stream().map(Task::getId).collect(Collectors.toList());
        assertEquals("[1, 2, 3, 6, 4, 5]", historyIds.toString(), "не прочиталась история из файла");

        assertEquals(6, newManager.getTaskCounter(), "некорректно восстановился счетчик задач");

        file = new File("./resources/TestEmptyBackupFile.csv");
        FileBackedTasksManager emptyTaskManager = FileBackedTasksManager.loadFromFile(file);
        assertEquals(FileBackedTasksManager.class, emptyTaskManager.getClass(),
                "ошибка при восстановлении из пустого файла");

        file = new File("./resources/TestNoTaskBackupFile.csv");
        FileBackedTasksManager noTaskManager = FileBackedTasksManager.loadFromFile(file);
        assertEquals(FileBackedTasksManager.class, noTaskManager.getClass(),
                "ошибка при восстановлении из файла без задач");

        file = new File("./resources/TestEmptyHistoryBackupFile.csv");
        FileBackedTasksManager noHistoryManager = FileBackedTasksManager.loadFromFile(file);
        assertEquals(FileBackedTasksManager.class, noHistoryManager.getClass(),
                "ошибка при восстановлении из файла без истории");
        final ArrayList<Task> tasksOnly = noHistoryManager.getAllTasks();
        assertEquals(2, tasksOnly.size(), "не прочитались задачи из файла без истории");
    }

    @Test
    void save() throws IOException {
        FileBackedTasksManager saveManager = new FileBackedTasksManager(file);

        LocalDateTime time = LocalDateTime.of(2000, 1, 1, 1, 1);

        Task task = saveManager.createTask("имя задачи1", "описание задачи1",
                time.plusHours(1), 30);
        Epic epic = saveManager.createEpic("имя эпика1", "описание эпика1",
                time.plusHours(6), 30);
        Subtask subtask = saveManager.createSubtask("имя подзадачи", "описание подзадачи",
                time.plusMinutes(40), 15, epic.getId());

        saveManager.getTaskById(task.getId());

        saveManager.save();

        final List<String> raws = Files.readAllLines(Path.of(file.getPath()));
        assertEquals("1,TASK,имя задачи1,NEW,описание задачи1,01.01.2000 02:01,30", raws.get(1));
        assertEquals("2,1", raws.get(5));

        saveManager.deleteAllTasks();
        saveManager.deleteAllEpics();
        saveManager.save();

        final List<String> headerOnly = Files.readAllLines(Path.of(file.getPath()));
        assertEquals("id,type,name,status,description,startTime,duration,epicId", headerOnly.get(0));
    }
}