package zerobase.weather.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import zerobase.weather.domain.Diary;
import zerobase.weather.repository.DiaryRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DiaryService {

    // 스프링 부트에 이미 지정되어 있는 변수 중에서 openweathermap.key값을
    // openweathermap.key값을 가져와서 apiKey 변수에 넣는다.
    @Value("${openweathermap.key}")
    private String apiKey;

    private final DiaryRepository diaryRepository;

    public DiaryService(DiaryRepository diaryRepository) {
        this.diaryRepository = diaryRepository;
    }

    public void createDiary(LocalDate date, String text) {
        // open weather map에서 날씨 데이터 가져오기
        String weatherString = getWeatherString();

        // 받아온 날씨 json 파싱하기
        Map<String, Object> parsedWeather = parseWeather(weatherString);

        // 파싱된 데이터 + 일기 값 DB에 넣기
        DiarySave(date, text, parsedWeather);
    }

    public List<Diary> readDiary(LocalDate date) {
        return diaryRepository.findAllByDate(date);
    }

    public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {
        return diaryRepository.findAllByDateBetween(startDate, endDate);
    }

    public void updateDiary(LocalDate date, String text) {
        Diary firstByDate = diaryRepository.getFirstByDate(date);
        firstByDate.setText(text);
        diaryRepository.save(firstByDate);
    }

    public void deleteDiary(LocalDate date) {
        diaryRepository.deleteAllByDate(date);
    }

    private String getWeatherString() {
        final String cityName = "seoul";
        final String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q="
                + cityName + "&appid=" + this.apiKey;
        try {
            BufferedReader br = getBufferedReader(apiUrl);
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            return response.toString();
        } catch (Exception e) {
            return "failed to get response";
        }
    }

    private static BufferedReader getBufferedReader(String apiUrl) throws IOException {
        URL url = new URL(apiUrl); // String to URL
        HttpURLConnection connection = (HttpURLConnection) url.openConnection(); // URL to Http
        connection.setRequestMethod("GET"); // GET 요청

        int responseCode = connection.getResponseCode(); // 응답 결과 코드
        BufferedReader br;

        if (responseCode == 200) {
            br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } else {
            br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        }
        return br;
    }

    private Map<String, Object> parseWeather(String jsonString) {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;

        try {
            jsonObject = (JSONObject) jsonParser.parse(jsonString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Map<String, Object> resultMap = new HashMap<>();

        JSONObject mainData = (JSONObject) jsonObject.get("main");
        resultMap.put("temp", mainData.get("temp"));

        JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
        JSONObject weatherData = (JSONObject) weatherArray.get(0);
        resultMap.put("main", weatherData.get("main"));
        resultMap.put("icon", weatherData.get("icon"));

        return resultMap;
    }

    private void DiarySave(LocalDate date, String text, Map<String, Object> parsedWeather) {
        Diary nowDiary = Diary.builder()
                .weather(parsedWeather.get("main").toString())
                .icon(parsedWeather.get("icon").toString())
                .temperature((Double) parsedWeather.get("temp"))
                .text(text)
                .date(date)
                .build();
        diaryRepository.save(nowDiary);
    }


}
