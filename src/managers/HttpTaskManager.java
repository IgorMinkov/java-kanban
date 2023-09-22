package managers;

import server.KVTaskClient;
import server.LocalDateTimeAdapter;
import com.google.gson.*;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {

    public static final String TASKS_KEY = "tasks";
    public static final String EPICS_KEY = "epics";
    public static final String SUBTASKS_KEY = "subtasks";
    public static final String HISTORY_KEY = "history";

    private static KVTaskClient taskClient;
    private static Gson gson;

    public HttpTaskManager(File file, String url) {
        super(file);
        taskClient = new KVTaskClient(url);
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    @Override
    public void save() {
        String jsonTasks = gson.toJson(getAllTasks());
        taskClient.put(TASKS_KEY, jsonTasks);

        String jsonEpics = gson.toJson(getAllEpics());
        taskClient.put(EPICS_KEY, jsonEpics);

        String jsonSubtasks = gson.toJson(getAllSubtask());
        taskClient.put(SUBTASKS_KEY, jsonSubtasks);

        List<Integer> historyIds = getHistoryManager().getHistory().stream()
                .map(Task::getId)
                .collect(Collectors.toList());
        String jsonHistory = gson.toJson(historyIds);
        taskClient.put(HISTORY_KEY, jsonHistory);
    }

    public void loadFromServer(HttpTaskManager httpManager) {
        List<Integer> idStorage = new ArrayList<>();

        stepOneLoadTasks(idStorage);
        stepTwoLoadEpics(idStorage);
        stepThreeLoadSubtasksAfterEpics(idStorage);
        httpManager.taskCounter = httpManager.findMax(idStorage);

        JsonElement jsonHistory = JsonParser.parseString(taskClient.load(HISTORY_KEY));
        System.out.println("история для обработки" + jsonHistory);
        if (!jsonHistory.isJsonNull()) {
            JsonArray jsonHistoryArray = jsonHistory.getAsJsonArray();
            for (JsonElement jsonTaskId : jsonHistoryArray) {
                Integer id = jsonTaskId.getAsInt();
                if (httpManager.taskStorage.containsKey(id)) {
                    httpManager.historyManager.addTask(httpManager.getTaskById(id));
                } else if (httpManager.epicStorage.containsKey(id)) {
                    httpManager.historyManager.addTask(httpManager.getEpicById(id));
                } else if (httpManager.subtaskStorage.containsKey(id)) {
                    httpManager.historyManager.addTask(httpManager.getSubtaskById(id));
                } else {
                    System.out.println("для добавления в историю не найдена задача с id " + id);
                }
            }
        }
    }

    private void stepOneLoadTasks(List<Integer> idStorage) {
        String loadTasks = taskClient.load(TASKS_KEY);
        System.out.println("задачи для обработки" + loadTasks);
        JsonElement jsonTasks = JsonParser.parseString(loadTasks);
        if (!jsonTasks.isJsonNull()) {
            JsonArray jsonTasksArray = jsonTasks.getAsJsonArray();
            for (JsonElement jsonTask : jsonTasksArray) {
                Task task = gson.fromJson(jsonTask, Task.class);
                if (task != null) {
                    idStorage.add(task.getId());
                    addPrioritizeTask(task);
                    taskStorage.put(task.getId(), task);
                }
            }
        }
    }

    private void stepTwoLoadEpics(List<Integer> idStorage) {
        String loadEpics = taskClient.load(EPICS_KEY);
        System.out.println("эпики для обработки" + loadEpics);
        JsonElement jsonEpics = JsonParser.parseString(loadEpics);
        if (!jsonEpics.isJsonNull()) {
            JsonArray jsonEpicsArray = jsonEpics.getAsJsonArray();
            for (JsonElement jsonEpic : jsonEpicsArray) {
                Epic epic = gson.fromJson(jsonEpic, Epic.class);
                if (epic != null) {
                    idStorage.add(epic.getId());
                    epicStorage.put(epic.getId(), epic);
                }
            }
        }
    }

    private void stepThreeLoadSubtasksAfterEpics(List<Integer> idStorage) {
        String loadSubtasks = taskClient.load(SUBTASKS_KEY);
        System.out.println("сабтаски для обработки" + loadSubtasks);
        JsonElement jsonSubtasks = JsonParser.parseString(loadSubtasks);
        if (!jsonSubtasks.isJsonNull()) {
            JsonArray jsonSubtasksArray = jsonSubtasks.getAsJsonArray();
            for (JsonElement jsonSubtask : jsonSubtasksArray) {
                Subtask subtask = gson.fromJson(jsonSubtask, Subtask.class);
                if (subtask != null) {
                    idStorage.add(subtask.getId());
                    addPrioritizeTask(subtask);
                    subtaskStorage.put(subtask.getId(), subtask);
                }
            }
        }
    }

}
