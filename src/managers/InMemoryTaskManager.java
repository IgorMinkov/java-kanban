package managers;

import model.Status;
import model.Task;
import model.Subtask;
import model.Epic;
import exceptions.ManagerTimeValidateException;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected Map<Integer, Task> taskStorage = new HashMap<>();
    protected Map<Integer, Subtask> subtaskStorage = new HashMap<>();
    protected Map<Integer, Epic> epicStorage = new HashMap<>();
    protected Set<Task> prioritizedTask = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    protected int taskCounter = 0;

    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    private Integer generateId() {
        return ++taskCounter;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(taskStorage.values());
    }

    @Override
    public void deleteAllTasks() {
        for (Task task: taskStorage.values()) {
            historyManager.remove(task.getId());
            prioritizedTask.remove(task);
        }
        taskStorage.clear();
    }

    @Override
    public Task getTaskById(Integer id) {
        if (id == null || !taskStorage.containsKey(id)) {
            System.out.println("передан несуществующий id: " + id);
            return null;
        }
        Task task = taskStorage.get(id);
        historyManager.addTask(task);
        return task;
    }

    @Override
    public Task createTask(String name, String description, LocalDateTime startTime) {
        if (name == null || name.isBlank()) {
            System.out.println("не задано или пустое имя задачи");
            return null;
        }
        Task task = new Task(name, description, startTime);
        int id = generateId();
        task.setId(id);
        addPrioritizeTask(task);
        taskStorage.put(id, task);
        return task;
    }

    @Override
    public Task createTask(String name, String description, LocalDateTime startTime, Integer duration) {
        if (name == null || name.isBlank()) {
            System.out.println("не задано или пустое имя задачи");
            return null;
        }
        Task task = new Task(name, description, startTime);
        int id = generateId();
        task.setId(id);
        task.setDuration(duration);
        addPrioritizeTask(task);
        taskStorage.put(id, task);
        return task;
    }

    @Override
    public void updateTask(Task task) {
        if (task == null) {
            System.out.println("для обновления задачи передан null");
            return;
        }
        addPrioritizeTask(task);
        taskStorage.put(task.getId(), task);
    }

    @Override
    public Task updateTaskStatus(Status status, Integer id) {
        Task modifiedTask = getTaskById(id);
        if (modifiedTask == null) {
            System.out.println("нет такой задачи: " + id);
            return null;
        }
        modifiedTask.setStatus(status);
        return taskStorage.put(modifiedTask.getId(), modifiedTask);
    }

    @Override
    public Task renameTask(String name, String description, Integer id) {
        if (name.isBlank() && description.isBlank()) {
            System.out.println("Не передан текст для изменения задачи");
            return null;
        }
        Task modifiedTask = getTaskById(id);
        if (modifiedTask == null) {
            System.out.println("нет такой задачи: " + id);
            return null;
        }
        if (description.isBlank()) {
            modifiedTask.setName(name);
        }
        if (name.isBlank()) {
            modifiedTask.setDescription(description);
        }
        return taskStorage.put(modifiedTask.getId(), modifiedTask);
    }

    @Override
    public void deleteTaskById(Integer id) {
        Task task = taskStorage.get(id);
        if (task == null) {
            System.out.println("нет такой задачи: " + id);
            return;
        }
        prioritizedTask.remove(task);
        taskStorage.remove(id);
        historyManager.remove(id);
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epicStorage.values());
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic: epicStorage.values()) {
            historyManager.remove(epic.getId());
            prioritizedTask.remove(epic);
        }
        epicStorage.clear();
        for (Subtask subtask: subtaskStorage.values()) {
            historyManager.remove(subtask.getId());
            prioritizedTask.remove(subtask);
        }
        subtaskStorage.clear();
    }

    @Override
    public Epic getEpicById(Integer id) {
        if (id == null || !epicStorage.containsKey(id)) {
            System.out.println("передан несуществующий id: " + id);
            return null;
        }
        Epic epic = epicStorage.get(id);
        historyManager.addTask(epic);
        return epic;
    }

    @Override
    public Epic createEpic(String name, String description, LocalDateTime startTime) {
        if (name == null || name.isBlank()) {
            System.out.println("не задано или пустое имя эпика");
            return null;
        }
        Epic epic = new Epic(name, description, startTime);
        int id = generateId();
        epic.setId(id);
        epicStorage.put(id, epic);
        return epic;
    }

    @Override
    public Epic createEpic(String name, String description, LocalDateTime startTime, Integer duration) {
        if (name == null || name.isBlank()) {
            System.out.println("не задано или пустое имя эпика");
            return null;
        }
        Epic epic = new Epic(name, description, startTime);
        int id = generateId();
        epic.setId(id);
        epic.setDuration(duration);
        epicStorage.put(id, epic);
        return epic;
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null) {
            System.out.println("нет такого эпика");
            return;
        }
        epicStorage.put(epic.getId(), epic);
    }

    @Override
    public Epic renameEpic(String name, String description, Integer id) {
        if (name.isBlank() && description.isBlank()) {
            System.out.println("Не передан текст для изменения эпика");
            return null;
        }
        Epic modifiedEpic = getEpicById(id);
        if (modifiedEpic == null) {
            System.out.println("нет такого эпика: " + id);
            return null;
        }
        if (description.isBlank()) {
            modifiedEpic.setName(name);
        }
        if (name.isBlank()) {
            modifiedEpic.setDescription(description);
        }
        return epicStorage.put(modifiedEpic.getId(), modifiedEpic);
    }

    @Override
    public void deleteEpicById(Integer id) {
        Epic epic = epicStorage.get(id);
        if (epic == null) {
            System.out.println("нет такого эпика: "+ id);
            return;
        }
        for (Integer i : epicStorage.get(id).getSubtaskIdList()) {
            subtaskStorage.remove(i);
            historyManager.remove(i);
        }
        epicStorage.get(id).getSubtaskIdList().clear();
        prioritizedTask.remove(epic);
        epicStorage.remove(id);
        historyManager.remove(id);
    }

    @Override
    public ArrayList<Subtask> getAllSubtask() {
        return new ArrayList<>(subtaskStorage.values());
    }

    @Override
    public void deleteAllSubtask() {
        for (Subtask subtask : subtaskStorage.values()) {
            historyManager.remove(subtask.getId());
            prioritizedTask.remove(subtask);
        }
        subtaskStorage.clear();
        for (Epic epic : epicStorage.values()) {
            epic.setDuration(0);
        }
    }

    @Override
    public Subtask getSubtaskById(Integer id) {
        if (id == null || !subtaskStorage.containsKey(id)) {
            System.out.println("передан несуществующий id: " + id);
            return null;
        }
        Subtask subtask = subtaskStorage.get(id);
        historyManager.addTask(subtask);
        return subtask;
    }

    @Override
    public Subtask createSubtask(String name, String description, LocalDateTime startTime, Integer epicId) {
        if (name == null || name.isBlank()) {
            System.out.println("не задано или пустое имя подзадачи");
            return null;
        }
        if (epicId == null) {
            System.out.println("не найден эпик-id для подзадачи");
            return null;
        }
        Subtask subtask = new Subtask(name, description, startTime, epicId);
        int id = generateId();
        subtask.setId(id);
        addPrioritizeTask(subtask);
        subtaskStorage.put(id, subtask);
        getEpicById(subtask.getEpicId()).getSubtaskIdList().add(subtask.getId());
        calculateEpicTime(subtask.getEpicId());
        return subtask;
    }

    @Override
    public Subtask createSubtask(String name, String description, LocalDateTime startTime,
                                 Integer duration, Integer epicId) {
        if (name == null || name.isBlank()) {
            System.out.println("не задано или пустое имя подзадачи");
            return null;
        }
        if (epicId == null) {
            System.out.println("не найден эпик для подзадачи");
            return null;
        }
        Subtask subtask = new Subtask(name, description, startTime, epicId);
        int id = generateId();
        subtask.setId(id);
        subtask.setDuration(duration);
        addPrioritizeTask(subtask);
        subtaskStorage.put(id, subtask);
        getEpicById(subtask.getEpicId()).getSubtaskIdList().add(subtask.getId());
        calculateEpicTime(subtask.getEpicId());
        return subtask;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null) {
            System.out.println("нет такой подзадачи");
            return;
        }
        addPrioritizeTask(subtask);
        subtaskStorage.put(subtask.getId(), subtask);
        updateEpicStatus(subtask.getEpicId());
        calculateEpicTime(subtask.getEpicId());
    }

    @Override
    public Subtask updateSubtaskStatus(Status status, Integer id) {
        Subtask modifiedSubtask = getSubtaskById(id);
        if (modifiedSubtask == null) {
            System.out.println("нет такой подзадачи");
            return null;
        }
        modifiedSubtask.setStatus(status);
        updateEpicStatus(modifiedSubtask.getEpicId());
        return subtaskStorage.put(modifiedSubtask.getId(), modifiedSubtask);
    }

    @Override
    public Subtask renameSubtask(String name, String description, Integer id) {
        if (name.isBlank() && description.isBlank()) {
            System.out.println("Не передан текст для изменения подзадачи");
            return null;
        }
        Subtask modifiedSubtask = getSubtaskById(id);
        if (modifiedSubtask == null) {
            System.out.println("нет такого эпика");
            return null;
        }
        if (!Objects.equals(name, "")) {
            modifiedSubtask.setName(name);
        }
        if (!Objects.equals(description, "")) {
            modifiedSubtask.setDescription(description);
        }
        return subtaskStorage.put(modifiedSubtask.getId(), modifiedSubtask);
    }

    @Override
    public void deleteSubtaskById(Integer id) {
        Subtask subtask = subtaskStorage.get(id);
        if (subtask == null) {
            System.out.println("нет такой подзадачи: "+ id);
            return;
        }
        getEpicById(subtask.getEpicId()).getSubtaskIdList().remove(subtask.getId());
        updateEpicStatus(subtask.getEpicId());
        calculateEpicTime(subtask.getEpicId());
        prioritizedTask.remove(subtask);
        subtaskStorage.remove(id);
        historyManager.remove(id);
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTask);
    }

    private void updateEpicStatus(Integer epicId) {
        if (!epicStorage.containsKey(epicId)) {
            System.out.println("нет эпика с таким id: " + epicId);
            return;
        }
        int newCounter = 0;
        int doneCounter = 0;

        for (Integer id : epicStorage.get(epicId).getSubtaskIdList()) {
            switch (subtaskStorage.get(id).getStatus()) {
                case NEW:
                    newCounter++;
                    break;
                case DONE:
                    doneCounter++;
                    break;
            }
        }
        if (newCounter == epicStorage.get(epicId).getSubtaskIdList().size()) {
            epicStorage.get(epicId).setStatus(Status.NEW);
        } else if (doneCounter == epicStorage.get(epicId).getSubtaskIdList().size()) {
            epicStorage.get(epicId).setStatus(Status.DONE);
        } else {
            epicStorage.get(epicId).setStatus(Status.IN_PROGRESS);
        }
    }

    private void calculateEpicTime(Integer epicId) {
        if (!epicStorage.containsKey(epicId)) {
            System.out.println("нет эпика с таким id: " + epicId);
            return;
        }
        Epic epic = epicStorage.get(epicId);
        List<Subtask> subtasks = new ArrayList<>();
        ArrayList<Integer> subtaskIds = epicStorage.get(epicId).getSubtaskIdList();
        for (Integer id: subtaskIds) {
            subtasks.add(subtaskStorage.get(id));
        }
        LocalDateTime startTime;
        Integer duration;
        LocalDateTime endTime;
        if (subtasks.isEmpty()) {
            startTime = epic.getStartTime();
            duration = epic.getDuration();
            endTime = startTime.plusMinutes(duration);
        } else {
            startTime = subtasks.get(0).getStartTime();
            endTime = subtasks.get(0).getEndTime();
            duration = 0;
            for (Subtask subtask : subtasks) {
                if (subtask.getStartTime().isBefore(startTime)) {
                    startTime = subtask.getStartTime();
                }
                if (subtask.getEndTime().isAfter(endTime)) {
                   endTime = subtask.getEndTime();
                }
                duration += subtask.getDuration();
            }
        }
        epic.setStartTime(startTime);
        epic.setDuration(duration);
        epic.setEndTime(endTime);
    }

    private void addPrioritizeTask(Task task) {
        if (task == null) {
            System.out.println("в метод addPrioritizeTask подан null");
            return;
        }
        if (task.getStartTime() == null) {
            task.setStartTime(LocalDateTime.now().plusYears(2));
        }
        try {
            validateTask(task);
            prioritizedTask.add(task);
        } catch (ManagerTimeValidateException e) {
            System.out.println(e.getMessage());
        }
    }

    private void validateTask(Task task) {
        if (task == null) {
            System.out.println("в метод validate подан null");
            return;
        }
        List<Task> tasks = getPrioritizedTasks();
        if (tasks.isEmpty()) {
            return;
        }
        LocalDateTime checkStartTime = task.getStartTime();
        LocalDateTime checkEndTime = task.getEndTime();

        for (Task t : tasks) {
            LocalDateTime oldStartTime = t.getStartTime();
            LocalDateTime oldEndTime = t.getEndTime();

            if (checkStartTime.isAfter(oldStartTime) && checkStartTime.isBefore(oldEndTime)
                    || checkEndTime.isAfter(oldStartTime) && checkEndTime.isBefore(oldEndTime)) {
                throw new ManagerTimeValidateException("Задача id " + task.getId() + " " + task.getName()
                        + " пересекается с задачей id" + t.getId() + " " + t.getName());
            }
        }
    }

}