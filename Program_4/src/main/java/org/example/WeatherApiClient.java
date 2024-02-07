package org.example;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherApiClient {
    private static final String API_KEY = "cbd9863021981b58cca7f35eea38aab1";

    public String getWeather(String city) {
        try {
            String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + API_KEY;
            URL url = new URL(apiUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();
                return response.toString();
            } else {
                System.out.println("Не удалось получить данные о погоде. Response code: " + responseCode);

                // Логируем тело ответа для неудачных запросов
                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String errorLine;

                    while ((errorLine = errorReader.readLine()) != null) {
                        errorResponse.append(errorLine);
                    }

                    System.out.println("Тело ответа при ошибке: " + errorResponse.toString());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}