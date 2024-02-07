package org.example;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final String API_KEY = "24e98a03207bba598dd0cbaf";
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int enterCustomer;
        do {
            System.out.println(
                    "1. Информация о государстве \n2. Прогноз погоды\n3. Обмен валют\n4. Выйти"
            );
            enterCustomer = scanner.nextInt();
            switch (enterCustomer) {
                case 1:
                    String title;
                    System.out.print("Введите континента: ");
                    title = scanner.next();
                    titleState(title);
                    break;
                case 2:
                    System.out.print("Введите название города: ");
                    String city = scanner.next();
                    weather(city);
                    break;
                case 3:
                    System.out.print("Введите код базовой валюты (например, USD): ");
                    String baseCurrency = scanner.next();

                    System.out.print("Введите код целевой валюты (например, EUR): ");
                    String targetCurrency = scanner.next();

                    System.out.print("Введите сумму для конвертации: ");
                    double amount = scanner.nextDouble();
                    currencyExchange(baseCurrency, targetCurrency, amount);
                    break;
                case 4:
                    System.out.println("Buy buy");
                    break;
            }
        } while (enterCustomer != 4);
        scanner.close();
    }


    static void titleState(String title) {

        try {
            URL url = new URL("https://restcountries.com/v3.1/name/" + title);
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

                JSONArray jsonArray = new JSONArray(response.toString());
                JSONObject jsonObject = jsonArray.getJSONObject(0);

                // Получение данных из объекта JSON
                String capital = jsonObject.getJSONArray("capital").getString(0);
                String flag = jsonObject.getString("flag");
                String region = jsonObject.getString("region");
                boolean independent = jsonObject.getBoolean("independent");
//                JSONObject postalCode = jsonObject.getJSONObject("postalCode");
                JSONObject currencies = jsonObject.getJSONObject("currencies");

                JSONArray borders = jsonObject.getJSONArray("borders");
                List<String> borderCountries = new ArrayList<>();
                for (int i = 0; i < borders.length(); i++) {
                    borderCountries.add(borders.getString(i));
                }

                System.out.println("Столица: " + capital);
                System.out.println("Регион: " + region);
                System.out.println("Флаг: " + flag);
                System.out.println("Независимость: " + independent);
                System.out.println("Граничащие страны: " + borders);

                Iterator<String> keys = currencies.keys();
                while (keys.hasNext()) {
                    String currencyCode = keys.next();
                    JSONObject currencyInfo = currencies.getJSONObject(currencyCode);
                    String currencyName = currencyInfo.getString("name");
                    String currencySymbol = currencyInfo.getString("symbol");

                    // Вывод полученных данных о валюте
                    System.out.println("Код валюты: " + currencyCode);
                    System.out.println("Название валюты: " + currencyName);
                    System.out.println("Символ валюты: " + currencySymbol);
                }

            } else {
                System.out.println("Error: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    static void currencyExchange (String baseCurrency, String targetCurrency, double amount){
        double exchangeRate = getExchangeRate(baseCurrency, targetCurrency);
        if (exchangeRate != -1) {
            double convertedAmount = amount * exchangeRate;
            System.out.println(amount + " " + baseCurrency + " = " + convertedAmount + " " + targetCurrency);
        } else {
            System.out.println("Не удалось получить курс обмена. Проверьте коды валют и подключение к сети.");
        }
    }

    private static double getExchangeRate(String baseCurrency, String targetCurrency) {
        try {
            URL url = new URL("https://open.er-api.com/v6/latest/" + baseCurrency + "?apikey=" + API_KEY);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Scanner scanner = new Scanner(url.openStream());
                StringBuilder response = new StringBuilder();

                while (scanner.hasNextLine()) {
                    response.append(scanner.nextLine());
                }

                scanner.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                return jsonResponse.getJSONObject("rates").getDouble(targetCurrency);
            } else {
                System.out.println("Ошибка при получении курса обмена. Код ответа: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    static void weather(String city ){
        WeatherApiClient weatherApiClient = new WeatherApiClient();

            String weatherData = weatherApiClient.getWeather(city);
            if (weatherData != null) {
                double temperature = extractTemperature(weatherData);
                System.out.println("Текущая температура в городе " + city + ": " + temperature + "°C");
            } else {
                System.out.println("Не удалось получить данные о погоде.");
            }
    }

    private static double extractTemperature(String weatherData) {
        JSONObject jsonData = new JSONObject(weatherData);
//        System.out.println(jsonData);
        if (jsonData.has("main")) {
            JSONObject mainData = jsonData.getJSONObject("main");

            if (mainData.has("temp")) {

                return mainData.getDouble("temp") - 273.15;
            }
        }
        return Double.NaN;
    }

}
