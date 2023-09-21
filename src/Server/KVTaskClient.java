package Server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import static java.nio.charset.StandardCharsets.UTF_8;

public class KVTaskClient {

    private final String url;
    private final HttpClient client;
    private String apiToken;

    public KVTaskClient(String url) {
        this.url = url;
        client = HttpClient.newHttpClient();
        apiToken = registerAndGetToken(url);
    }

    private String registerAndGetToken(String url) {
        try {
            URI uri = URI.create(url + "register");
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(uri)
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
            if (response.statusCode() == 200) {
                apiToken = response.body();
            } else {
                System.out.println("Токен не получен, запрос вернулся со статусом: "
                        + response.statusCode());
            }
        } catch (IOException | InterruptedException exception) {
            System.out.println("ошибка отправки запроса регистрации: "
                    + exception.getMessage());
        }
        return apiToken;
    }

    public void put(String key, String json) {
        URI saveUrl = URI.create(url + "save/" + key + "/?API_TOKEN=" + apiToken);
        try {
            HttpRequest postRequest = HttpRequest.newBuilder()
                    .uri(saveUrl)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(postRequest,
                    HttpResponse.BodyHandlers.ofString(UTF_8));
            if (response.statusCode() == 200) {
                System.out.println("Данные успешно сохранены на сервере");
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: "
                        + response.statusCode());
            }
        } catch (NullPointerException | IOException | InterruptedException e) {
            System.out.println("ошибка запроса сохранения: " + e.getMessage());
        }
    }

    public String load(String key) {
        URI loadUrl = URI.create(url + "load/" + key + "/?API_TOKEN=" + apiToken);
        try {
            HttpRequest postRequest = HttpRequest.newBuilder()
                    .GET()
                    .uri(loadUrl)
                    .header("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response = client.send(postRequest,
                    HttpResponse.BodyHandlers.ofString(UTF_8));
            if (response.statusCode() == 200) {
                JsonElement jsonElement = JsonParser.parseString(response.body());
                System.out.println("Данные успешно выгружены с сервера");
                return jsonElement.toString();
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: "
                        + response.statusCode());
            }
        } catch (NullPointerException | IOException | InterruptedException e) {
            System.out.println("ошибка запроса загрузки: " + e.getMessage());
        }
        return "";
    }

}
