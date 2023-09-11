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

        final Task wrongIdTask = taskManager.getTaskById(999);
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

        final List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks);
        assertTrue(tasks.contains(task));
    }

    @Test
    void updateTask() {
        Task task = addTask();
        task.setStatus(Status.DONE);
        taskManager.updateTask(task);
        final Task updatedTask = taskManager.getTaskById(task.getId());
        assertEquals(Status.DONE, updatedTask.getStatus());
        assertEquals(task.getId(), updatedTask.getId());
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

        final Epic wrongIdEpic = taskManager.getEpicById(999);
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

        final Subtask wrongIdSubtask = taskManager.getSubtaskById(999);
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

        List<Subtask> subtasks = taskManager.getAllSubtask();
        assertNotNull(subtasks);
        assertTrue(subtasks.contains(subtask));
        assertTrue(epic.getSubtaskIdList().contains(subtask.getId()));
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
    void deleteSubtaskById() {
        Epic epic = addEpic();
        Subtask subtask = addSubtask(epic);
        taskManager.deleteSubtaskById(subtask.getId());
        final List<Subtask> subtasks = taskManager.getAllSubtask();
        assertFalse(subtasks.contains(subtask));
        assertFalse(epic.getSubtaskIdList().contains(subtask.getId()));
    }
}