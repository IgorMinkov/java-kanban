package server;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import managers.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static model.Task.FORMATTER;

public class HttpTaskServer {

    public static final int PORT = 8080;
    public static final String TASK_PATH = "task";
    public static final String EPIC_PATH = "epic";
    public static final String SUBTASK_PATH = "subtask";
    public static final String HISTORY_PATH = "history";
    public static final String PRIORITIZED_PATH = "prioritized";

    private final HttpServer httpServer;
    private final TaskManager taskManager;
    private final Gson gson;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress("localhost",PORT), 0);
        httpServer.createContext("/tasks/", this::taskHandler);
        this.taskManager = manager;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    private void taskHandler(HttpExchange exchange) {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            String query = exchange.getRequestURI().getQuery();

            Endpoint endpoint = getEndpoint(path, method, query);
            System.out.println("Processing end-point: " + endpoint);

            switch (endpoint) {
                case GET_ALL_TASKS: handleGetAllTasks(exchange);
                    break;
                case DELETE_ALL_TASKS: handleDeleteAllTasks(exchange);
                    break;
                case GET_TASK: handleGetTaskById(exchange);
                    break;
                case POST_TASK: handleCreateTask(exchange);
                    break;
                case UPDATE_TASK: handleUpdateTask(exchange);
                    break;
                case DELETE_TASK: handleDeleteTaskById(exchange);
                    break;
                case GET_ALL_EPICS: handleGetAllEpics(exchange);
                    break;
                case DELETE_ALL_EPICS: handleDeleteAllEpics(exchange);
                    break;
                case GET_EPIC: handleGetEpic(exchange);
                    break;
                case POST_EPIC: handleCreateEpic(exchange);
                    break;
                case UPDATE_EPIC: handleUpdateEpic(exchange);
                    break;
                case DELETE_EPIC: handleDeleteEpicById(exchange);
                    break;
                case GET_ALL_SUBTASKS: handleGetAllSubtasks(exchange);
                    break;
                case DELETE_ALL_SUBTASKS: handleDeleteAllSubtasks(exchange);
                    break;
                case GET_SUBTASK: handleGetSubtaskById(exchange);
                    break;
                case POST_SUBTASK: handleCreateSubtask(exchange);
                    break;
                case UPDATE_SUBTASK: handleUpdateSubtask(exchange);
                    break;
                case DELETE_SUBTASK: handleDeleteSubtaskById(exchange);
                    break;
                case GET_HISTORY: handleGetHistory(exchange);
                    break;
                case GET_PRIORITIZED: handleGetPrioritizedTasks(exchange);
                    break;
                case GET_EPIC_SUBTASKS: handleGetEpicSubtasks(exchange);
                    break;
                case UNKNOWN_ENDPOINT:
                    sendResponse(exchange,
                        "Запрос не распознан", 405);
                    break;
                default: System.out.println("неизвестная ошибка хэндлера");
            }
        } catch (Exception exception) {
            System.out.println("Ошибка обработки запроса" + exception.getMessage());
        } finally {
            exchange.close();
        }
    }

    private Endpoint getEndpoint(String path, String method, String query) {
        String shorterPath = path.replaceFirst("/tasks", "");
        String[] pathParts = shorterPath.split("/");
        Endpoint endpoint;

        switch (method) {
            case "GET":
                endpoint = getEndpointForGet(pathParts, query);
                break;
            case "POST":
                endpoint = getEndpointForPost(pathParts, query);
                break;
            case "DELETE":
                endpoint = getEndpointForDelete(pathParts, query);
                break;
            default: endpoint = Endpoint.UNKNOWN_ENDPOINT;
        }
        return endpoint;
    }

    private Endpoint getEndpointForGet(String[] pathParts, String query) {
        switch (pathParts[1]) {
            case TASK_PATH:
                if (pathParts.length == 2 && query == null) {
                    return Endpoint.GET_ALL_TASKS;
                } else {
                    return Endpoint.GET_TASK;
                }
            case EPIC_PATH:
                if (pathParts.length == 2 && query == null) {
                    return Endpoint.GET_ALL_EPICS;
                } else {
                    return Endpoint.GET_EPIC;
                }
            case SUBTASK_PATH:
                if (pathParts.length == 2 && query == null) {
                    return Endpoint.GET_ALL_SUBTASKS;
                } else if (pathParts.length == 3 && pathParts[2].equals(EPIC_PATH)) {
                    return Endpoint.GET_EPIC_SUBTASKS;
                } else {
                    return Endpoint.GET_SUBTASK;
                }
            case HISTORY_PATH:
                return Endpoint.GET_HISTORY;
            case PRIORITIZED_PATH:
                return Endpoint.GET_PRIORITIZED;
        }
        return Endpoint.UNKNOWN_ENDPOINT;
    }

    private Endpoint getEndpointForPost(String[] pathParts, String query) {
        switch (pathParts[1]) {
            case TASK_PATH:
                if (pathParts.length == 2 && query == null) {
                    return Endpoint.POST_TASK;
                } else {
                    return Endpoint.UPDATE_TASK;
                }
            case EPIC_PATH:
                if (pathParts.length == 2 && query == null) {
                    return Endpoint.POST_EPIC;
                } else {
                    return Endpoint.UPDATE_EPIC;
                }
            case SUBTASK_PATH:
                if (pathParts.length == 2 && query == null) {
                    return Endpoint.POST_SUBTASK;
                } else {
                    return Endpoint.UPDATE_SUBTASK;
                }
        }
        return Endpoint.UNKNOWN_ENDPOINT;
    }

    private Endpoint getEndpointForDelete(String[] pathParts, String query) {
        switch (pathParts[1]) {
            case TASK_PATH:
                if (pathParts.length == 2 && query == null) {
                    return Endpoint.DELETE_ALL_TASKS;
                } else {
                    return Endpoint.DELETE_TASK;
                }
            case EPIC_PATH:
                if (pathParts.length == 2 && query == null) {
                    return Endpoint.DELETE_ALL_EPICS;
                } else {
                    return Endpoint.DELETE_EPIC;
                }
            case SUBTASK_PATH:
                if (pathParts.length == 2 && query == null) {
                    return Endpoint.DELETE_ALL_SUBTASKS;
                } else {
                    return Endpoint.DELETE_SUBTASK;
                }
        }
        return Endpoint.UNKNOWN_ENDPOINT;
    }

    private void sendResponse(HttpExchange exchange, String response, int status) throws IOException {
        if(response.isBlank()) {
            exchange.sendResponseHeaders(status,0);
        } else {
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(status, response.length());

            byte[] bytes = response.getBytes(UTF_8);
            exchange.getResponseBody().write(bytes);
        }
    }

    private Optional<Integer> getIdFromRequest(HttpExchange exchange) {
        String query = exchange.getRequestURI().getQuery();
        String id = query.substring(3);
        try {
            return Optional.of(Integer.parseInt(id));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    private void handleGetAllTasks(HttpExchange exchange) throws IOException {
        if (taskManager.getAllTasks().isEmpty()) {
            sendResponse(exchange, "В хранилище задач пусто.", 404);
        }
        String response = taskManager.getAllTasks().toString();
        sendResponse(exchange, response, 200);
    }

    private void handleDeleteAllTasks(HttpExchange exchange) throws IOException {
        taskManager.deleteAllTasks();
        sendResponse(exchange, "все задачи успешно удалены", 200);
    }

    private void handleGetTaskById(HttpExchange exchange) throws IOException {
        Optional<Integer> oplId = getIdFromRequest(exchange);
        if (oplId.isEmpty()) {
            sendResponse(exchange, "Некорректный id задачи", 400);
            return;
        }
        Integer id = oplId.get();
        if (taskManager.getTaskById(id) == null) {
            sendResponse(exchange, "Задача с id " + id + " не найдена", 404);
        }
        String response = gson.toJson(taskManager.getTaskById(id));
        sendResponse(exchange, response, 200);
    }

    private void handleCreateTask(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), UTF_8);
        try {
            JsonObject jsonTask = JsonParser.parseString(body).getAsJsonObject();
            String name = jsonTask.get("name").getAsString();
            String description = jsonTask.get("description").getAsString();
            LocalDateTime startTime = LocalDateTime.parse(jsonTask.get("startTime").getAsString(), FORMATTER);
            Integer duration = jsonTask.get("duration").getAsInt();
            Task task = taskManager.createTask(name, description, startTime, duration);
            sendResponse(exchange, "Задача успешно создана, присвоен id " + task.getId(), 201);
        } catch (JsonSyntaxException e) {
            sendResponse(exchange, "Ошибка обработки Json при содании задачи", 400);
        }
    }

    private void handleUpdateTask(HttpExchange exchange) throws IOException {
        Optional<Integer> oplId = getIdFromRequest(exchange);
        if (oplId.isEmpty()) {
            sendResponse(exchange, "Некорректный id задачи для обновления", 400);
            return;
        }
        Integer id = oplId.get();
        if (taskManager.getTaskById(id) == null) {
            sendResponse(exchange, "Задача с id " + id + " не найдена для обновления", 404);
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), UTF_8);
        try {
            JsonElement jsonTask = JsonParser.parseString(body);
            Task task = gson.fromJson(jsonTask, Task.class);
            taskManager.updateTask(task);
            sendResponse(exchange, "Задача с id " + task.getId() + " обновлена", 201);
        } catch (JsonSyntaxException e) {
            sendResponse(exchange,"Ошибка обработки Json при обновлении задачи", 400);
        }
    }

    private void handleDeleteTaskById(HttpExchange exchange) throws IOException {
        Optional<Integer> oplId = getIdFromRequest(exchange);
        if (oplId.isEmpty()) {
            sendResponse(exchange, "Некорректный id задачи для удаления", 400);
            return;
        }
        Integer id = oplId.get();
        if (taskManager.getTaskById(id) == null) {
            sendResponse(exchange, "Задача с id " + id + " не найдена", 404);
        }
        taskManager.deleteTaskById(id);
        sendResponse(exchange, "Задача с id" + id + " успешно удалена", 200);
    }

    private void handleGetAllEpics(HttpExchange exchange) throws IOException {
        if (taskManager.getAllEpics().isEmpty()) {
            sendResponse(exchange, "В хранилище эпиков пусто.", 404);
        }
        String response = taskManager.getAllEpics().toString();
        sendResponse(exchange, response, 200);
    }

    private void handleDeleteAllEpics(HttpExchange exchange) throws IOException {
        taskManager.deleteAllEpics();
        sendResponse(exchange, "все эпики и подзадачи успешно удалены", 200);
    }

    private void handleGetEpic(HttpExchange exchange) throws IOException {
        Optional<Integer> oplId = getIdFromRequest(exchange);
        if (oplId.isEmpty()) {
            sendResponse(exchange, "Некорректный id эпика", 400);
            return;
        }
        Integer id = oplId.get();
        if (taskManager.getEpicById(id) == null) {
            sendResponse(exchange, "Эпик с id " + id + " не найден", 404);
        }
        String response = gson.toJson(taskManager.getEpicById(id));
        sendResponse(exchange, response, 200);
    }

    private void handleCreateEpic(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), UTF_8);
        try {
            JsonObject jsonEpic = JsonParser.parseString(body).getAsJsonObject();
            String name = jsonEpic.get("name").getAsString();
            String description = jsonEpic.get("description").getAsString();
            LocalDateTime startTime = LocalDateTime.parse(jsonEpic.get("startTime").getAsString(), FORMATTER);
            Integer duration = jsonEpic.get("duration").getAsInt();
            Epic epic = taskManager.createEpic(name, description, startTime, duration);
            sendResponse(exchange, "Эпик успешно создан, присвоен id " + epic.getId(), 201);
        } catch (JsonSyntaxException e) {
            sendResponse(exchange, "Ошибка обработки Json при содании эпика", 400);
        }
    }

    private void handleUpdateEpic(HttpExchange exchange) throws IOException {
        Optional<Integer> oplId = getIdFromRequest(exchange);
        if (oplId.isEmpty()) {
            sendResponse(exchange, "Некорректный id эпика для обновления", 400);
            return;
        }
        Integer id = oplId.get();
        if (taskManager.getEpicById(id) == null) {
            sendResponse(exchange, "Эпик с id " + id + " не найден", 404);
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), UTF_8);
        try {
            JsonElement jsonEpic = JsonParser.parseString(body);
            Epic epic = gson.fromJson(jsonEpic, Epic.class);
            taskManager.updateEpic(epic);
            sendResponse(exchange, "Эпик с id " + epic.getId() + " обновлен", 201);
        } catch (JsonSyntaxException e) {
            sendResponse(exchange, "Ошибка обработки Json при обновлении эпика", 400);
        }
    }

    private void handleDeleteEpicById(HttpExchange exchange) throws IOException {
        Optional<Integer> oplId = getIdFromRequest(exchange);
        if (oplId.isEmpty()) {
            sendResponse(exchange, "Некорректный id эпика для удаления", 400);
            return;
        }
        Integer id = oplId.get();
        if (taskManager.getEpicById(id) == null) {
            sendResponse(exchange, "Эпик с id " + id + " не найден", 404);
        }
        taskManager.deleteEpicById(id);
        sendResponse(exchange, "Эпик с id" + id + " успешно удален", 200);
    }

    private void handleGetAllSubtasks(HttpExchange exchange) throws IOException {
        if (taskManager.getAllSubtask().isEmpty()) {
            sendResponse(exchange, "В хранилище подзадач пусто.", 404);
        }
        String response = taskManager.getAllSubtask().toString();
        sendResponse(exchange, response, 200);
    }

    private void handleDeleteAllSubtasks(HttpExchange exchange) throws IOException {
        taskManager.deleteAllSubtask();
        sendResponse(exchange, "все подзадачи успешно удалены", 200);
    }

    private void handleGetSubtaskById(HttpExchange exchange) throws IOException {
        Optional<Integer> oplId = getIdFromRequest(exchange);
        if (oplId.isEmpty()) {
            sendResponse(exchange, "Некорректный id подзадачи", 400);
            return;
        }
        Integer id = oplId.get();
        if (taskManager.getSubtaskById(id) == null) {
            sendResponse(exchange, "Подзадача с id " + id + " не найдена", 404);
        }
        String response = gson.toJson(taskManager.getSubtaskById(id));
        sendResponse(exchange, response, 200);
    }

    private void handleCreateSubtask(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), UTF_8);
        try {
            JsonObject jsonSubtask = JsonParser.parseString(body).getAsJsonObject();
            String name = jsonSubtask.get("name").getAsString();
            String description = jsonSubtask.get("description").getAsString();
            LocalDateTime startTime = LocalDateTime.parse(jsonSubtask.get("startTime").getAsString(), FORMATTER);
            Integer duration = jsonSubtask.get("duration").getAsInt();
            Integer epicId = jsonSubtask.get("epicId").getAsInt();
            Subtask subtask = taskManager.createSubtask(name, description, startTime, duration, epicId);
            sendResponse(exchange, "Подзадача успешно создана, присвоен id " + subtask.getId(), 201);
        } catch (JsonSyntaxException e) {
            sendResponse(exchange, "Ошибка обработки Json при содании подзадачи", 400);
        }
    }

    private void handleUpdateSubtask(HttpExchange exchange) throws IOException {
        Optional<Integer> oplId = getIdFromRequest(exchange);
        if (oplId.isEmpty()) {
            sendResponse(exchange, "Некорректный id подзадачи для обновления", 400);
            return;
        }
        Integer id = oplId.get();
        if (taskManager.getSubtaskById(id) == null) {
            sendResponse(exchange, "Подзадача с id " + id + " не найдена", 404);
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), UTF_8);
        try {
            JsonElement jsonSubtask = JsonParser.parseString(body);
            Subtask subtask = gson.fromJson(jsonSubtask, Subtask.class);
            taskManager.updateSubtask(subtask);
            sendResponse(exchange, "Подзадача с id " + subtask.getId() + " обновлена", 201);
        } catch (JsonSyntaxException e) {
            sendResponse(exchange, "Ошибка обработки Json при обновлении подзадачи", 400);
        }
    }

    private void handleDeleteSubtaskById(HttpExchange exchange) throws IOException {
        Optional<Integer> oplId = getIdFromRequest(exchange);
        if (oplId.isEmpty()) {
            sendResponse(exchange, "Некорректный id подзадачи для удаления", 400);
            return;
        }
        Integer id = oplId.get();
        if (taskManager.getSubtaskById(id) == null) {
            sendResponse(exchange, "Подзадача с id " + id + " не найдена", 404);
        }
        taskManager.deleteSubtaskById(id);
        sendResponse(exchange, "Подзадача с id" + id + " успешно удалена", 200);
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        if (taskManager.getHistoryManager().getHistory().isEmpty()) {
            sendResponse(exchange, "История просмотров пуста.", 404);
        }
        String response = taskManager.getHistoryManager().getHistory().toString();
        sendResponse(exchange, response, 200);
    }

    private void handleGetPrioritizedTasks(HttpExchange exchange) throws IOException {
        if (taskManager.getPrioritizedTasks().isEmpty()) {
            sendResponse(exchange, "Список задач по приоритету пуст.", 404);
        }
        String response = taskManager.getPrioritizedTasks().toString();
        sendResponse(exchange, response, 200);
    }

    private void handleGetEpicSubtasks(HttpExchange exchange) throws IOException {
        Optional<Integer> oplId = getIdFromRequest(exchange);
        if (oplId.isEmpty()) {
            sendResponse(exchange, "Некорректный id эпика для поиска подзадач", 400);
            return;
        }
        Integer id = oplId.get();
        if (taskManager.getEpicById(id) == null) {
            sendResponse(exchange, "Эпик с id " + id + " не найден", 404);
        }
        if ( taskManager.getEpicById(id).getSubtaskIdList().isEmpty()) {
            sendResponse(exchange, "У эпика с id " + id + " нет подзадач", 404);
        }
        List<Subtask> epicSubtasks = taskManager.getEpicById(id).getSubtaskIdList().stream()
                        .map(taskManager::getSubtaskById)
                        .collect(Collectors.toList());
        sendResponse(exchange, epicSubtasks.toString(), 200);
    }

    public void start() {
        System.out.println("HTTP TaskServer starts on " + PORT + " port.");
        System.out.println("Запросы идут на адрес: http://localhost:" + PORT + "/tasks/");
        httpServer.start();
    }

    public void stop(int delay) {
        httpServer.stop(delay);
        System.out.println("HTTP TaskServer stopped.");
    }

}
