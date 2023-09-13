import managers.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    protected LocalDateTime time = LocalDateTime.of(2000, 1, 1, 1, 1);

    public abstract T createManager();

    @BeforeEach
    void getManager () {
        taskManager = createManager();
    }

    protected Task addTask() {
        return taskManager.createTask("имя задачи1", "описание задачи1",
                time.plusMinutes(30), 30);
    }

    protected Epic addEpic() {
        return taskManager.createEpic("имя эпика", "описание эпика",
                time.plusHours(5), 40);
    }

    protected Subtask addSubtask(Epic epic) {
        return taskManager.createSubtask("имя подзадачи", "описание подзадачи",
                time.plusHours(2), 55, epic.getId());
    }

    @Test
    void getAllTasks() {
        Task task = addTask();
        final List<Task> taskList = taskManager.getAllTasks();
        assertNotNull(taskList);
        assertFalse(taskList.isEmpty());
        assertTrue(taskList.contains(task));
    }

    @Test
    void deleteAllTasks() {
        taskManager.deleteAllTasks();
        final List<Task> tasks = taskManager.getAllTasks();
        assertTrue(tasks.isEmpty());
    }

    @Test
    void getTaskById() {
        Task task = addTask();
        final Task savedTask = taskManager.getTaskById(task.getId());
        assertNotNull(savedTask);
        assertEquals(task, savedTask);

        final Task wrongIdTask = taskManager.getTaskById(-1);
        assertNull(wrongIdTask);

        final Task nullTask = taskManager.getTaskById(null);
        assertNull(nullTask);
    }

    @Test
    void createTask() {
        Task task = addTask();
        assertNotNull(task.getId());
        assertNotNull(task.getStatus());
        assertEquals(Status.NEW, task.getStatus());

        assertTrue(taskManager.getPrioritizedTasks().contains(task));

        final List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks);
        assertTrue(tasks.contains(task));

        Task taskTwo = taskManager.createTask("", "описание",
                time.plusMinutes(11), 11);
        assertNull(taskTwo, "создалась задача с пустым именем");

        Task taskThree = taskManager.createTask(null, "описание",
                time.plusMinutes(11) );
        assertNull(taskThree, "создалась задача с null-именем");
    }

    @Test
    void updateTask() {
        Task task = addTask();
        task.setStatus(Status.DONE);
        taskManager.updateTask(task);
        final Task updatedTask = taskManager.getTaskById(task.getId());
        assertEquals(Status.DONE, updatedTask.getStatus());
        assertEquals(task.getId(), updatedTask.getId());

        assertTrue(taskManager.getPrioritizedTasks().contains(task));
    }

    @Test
    void updateTaskStatus() {
        Task task = addTask();
        final Task updatedTask = taskManager.updateTaskStatus(Status.IN_PROGRESS, task.getId());
        assertEquals(task.getId(), updatedTask.getId(), "ошибка обновления статуса задачи");

        final Task nullTask = taskManager.updateTaskStatus(Status.DONE, null);
        assertNull(nullTask);
    }

    @Test
    void renameTask() {
        Task task = addTask();
        final Task nullTask = taskManager.renameTask("имя", "описание", null);
        assertNull(nullTask);

        final Task blankTask = taskManager.renameTask("", "", task.getId());
        assertNull(blankTask);

        final Task renamedTask = taskManager.renameTask("Новое имя", "", task.getId());
        assertEquals(task.getId(), renamedTask.getId(), "ошибка переименования задачи");
    }

    @Test
    void deleteTaskById() {
        Task task = addTask();
        taskManager.deleteTaskById(task.getId());
        final List<Task> tasks = taskManager.getAllTasks();
        assertFalse(tasks.contains(task));
    }

    @Test
    void getAllEpics() {
        Epic epic = addEpic();
        final List<Epic> epicList = taskManager.getAllEpics();
        assertNotNull(epicList);
        assertFalse(epicList.isEmpty());
        assertTrue(epicList.contains(epic));
    }

    @Test
    void deleteAllEpics() {
        taskManager.deleteAllEpics();
        final List<Epic> epics = taskManager.getAllEpics();
        assertTrue(epics.isEmpty());

        final List<Subtask> subtasks = taskManager.getAllSubtask();
        assertTrue(subtasks.isEmpty());
    }

    @Test
    void getEpicById() {
        Epic epic = addEpic();
        final Epic savedEpic = taskManager.getEpicById(epic.getId());
        assertNotNull(savedEpic);
        assertEquals(epic,savedEpic);

        final Epic wrongIdEpic = taskManager.getEpicById(-1);
        assertNull(wrongIdEpic);

        final Epic nullEpic = taskManager.getEpicById(null);
        assertNull(nullEpic);
    }

    @Test
    void createEpic() {
        Epic epic = addEpic();
        assertNotNull(epic.getId());
        assertNotNull(epic.getStatus());
        assertEquals(Status.NEW, epic.getStatus());

        final List<Epic> epics = taskManager.getAllEpics();
        assertNotNull(epics);
        assertTrue(epics.contains(epic));
        assertTrue(epic.getSubtaskIdList().isEmpty());

        Epic epicTwo = taskManager.createEpic("", "описание",
                time.plusMinutes(11), 11);
        assertNull(epicTwo, "создался эпик с пустым именем");

        Epic epicThree = taskManager.createEpic(null, "описание",
                time.plusMinutes(11) );
        assertNull(epicThree, "создался эпик с null-именем");
    }

    @Test
    void updateEpic() {
        Epic epic = addEpic();
        epic.setName("Новое имя эпика");
        taskManager.updateEpic(epic);
        final Task updatedEpic = taskManager.getEpicById(epic.getId());
        assertEquals("Новое имя эпика", updatedEpic.getName());
        assertEquals(epic.getId(), updatedEpic.getId());
    }

    @Test
    void renameEpic() {
        Epic epic = addEpic();
        final Epic nullEpic = taskManager.renameEpic("имя", "описание", null);
        assertNull(nullEpic);

        final Epic blankEpic = taskManager.renameEpic("", "", epic.getId());
        assertNull(blankEpic);

        final Epic renamedEpic = taskManager.renameEpic("Новое имя", "", epic.getId());
        assertEquals(epic.getId(), renamedEpic.getId(), "ошибка переименования эпика");
    }

    @Test
    void deleteEpicById() {
        Epic epic = addEpic();
        taskManager.deleteEpicById(epic.getId());
        final List<Epic> epics = taskManager.getAllEpics();
        assertFalse(epics.contains(epic));

        boolean IsHadSubtasks = false;
        for (Subtask subtask : taskManager.getAllSubtask()) {
            if (subtask.getEpicId() == epic.getId()) {
                IsHadSubtasks = true;
                break;
            }
        }
        assertFalse(IsHadSubtasks);
    }

    @Test
    void getAllSubtask() {
        Epic epic = addEpic();
        Subtask subtask = addSubtask(epic);
        final List<Subtask> subtaskList = taskManager.getAllSubtask();
        assertNotNull(subtaskList);
        assertFalse(subtaskList.isEmpty());
        assertTrue(subtaskList.contains(subtask));
    }

    @Test
    void deleteAllSubtask() {
        taskManager.deleteAllSubtask();
        final List<Subtask> subtasks = taskManager.getAllSubtask();
        assertTrue(subtasks.isEmpty());
    }

    @Test
    void getSubtaskById() {
        Epic epic = addEpic();
        Subtask subtask = addSubtask(epic);
        final Subtask savedSubtask = taskManager.getSubtaskById(subtask.getId());
        assertNotNull(savedSubtask);
        assertEquals(subtask,savedSubtask);

        final Subtask wrongIdSubtask = taskManager.getSubtaskById(-1);
        assertNull(wrongIdSubtask);

        final Subtask nullSubtask = taskManager.getSubtaskById(null);
        assertNull(nullSubtask);
    }

    @Test
    void createSubtask() {
        Epic epic = addEpic();
        Subtask subtask = addSubtask(epic);
        assertNotNull(subtask.getId());
        assertNotNull(subtask.getStatus());
        assertEquals(Status.NEW, subtask.getStatus());
        assertEquals(epic.getId(), subtask.getEpicId());

        assertSame(epic.getStartTime(), subtask.getStartTime());

        List<Subtask> subtasks = taskManager.getAllSubtask();
        assertNotNull(subtasks);
        assertTrue(subtasks.contains(subtask));
        assertTrue(epic.getSubtaskIdList().contains(subtask.getId()));

        Subtask subtaskTwo = taskManager.createSubtask("", "описание",
                time.plusMinutes(11), epic.getId());
        assertNull(subtaskTwo, "создалась подзадача с пустым именем");

        Subtask subtaskThree = taskManager.createSubtask(null, "описание",
                time.plusMinutes(11), epic.getId());
        assertNull(subtaskThree, "создалась подзадача с null-именем");
    }

    @Test
    void updateSubtask() {
        Epic epic = addEpic();
        Subtask subtask = addSubtask(epic);
        subtask.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask);
        final Subtask updatedSubtask = taskManager.getSubtaskById(subtask.getId());
        assertEquals(Status.DONE, updatedSubtask.getStatus());
        assertEquals(subtask.getId(), updatedSubtask.getId());
        assertNotSame(epic.getStatus(), Status.NEW);
    }

    @Test
    void renameSubtask() {
        Epic epic = addEpic();
        Subtask subtask = addSubtask(epic);
        final Subtask nullSubtask = taskManager.renameSubtask("имя", "описание", null);
        assertNull(nullSubtask);

        final Subtask blankSubtask = taskManager.renameSubtask("", "", subtask.getId());
        assertNull(blankSubtask);

        final Subtask renamedSubtask = taskManager.renameSubtask("Новое имя",
                "", subtask.getId());
        assertEquals(subtask.getId(), renamedSubtask.getId(), "ошибка переименования задачи");
    }

    @Test
    void deleteSubtaskById() {
        Epic epic = addEpic();
        Subtask subtask = addSubtask(epic);
        taskManager.deleteSubtaskById(subtask.getId());
        final List<Subtask> subtasks = taskManager.getAllSubtask();
        assertFalse(subtasks.contains(subtask));
        assertFalse(epic.getSubtaskIdList().contains(subtask.getId()));
    }

    @Test
    void getPrioritizedTasks() {
        final List<Task> tasks = taskManager.getPrioritizedTasks();
        assertNotNull(tasks);
    }
}