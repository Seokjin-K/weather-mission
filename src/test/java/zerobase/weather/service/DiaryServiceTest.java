package zerobase.weather.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import zerobase.weather.domain.DateWeather;
import zerobase.weather.domain.Diary;
import zerobase.weather.repository.DateWeatherRepository;
import zerobase.weather.repository.DiaryRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiaryServiceTest {

    @Mock
    private DiaryRepository diaryRepository;

    @Mock
    private DateWeatherRepository dateWeatherRepository;

    @InjectMocks
    private DiaryService diaryService;

    @Test
    void createDiaryWhenWeatherExistsInDB() {
        // given
        LocalDate date = LocalDate.of(2024, 9, 28);
        String text = "Sunny day";

        DateWeather dateWeather = new DateWeather();
        given(dateWeatherRepository.findAllByDate(date))
                .willReturn(Collections.singletonList(dateWeather));

        // when
        diaryService.createDiary(date, text);

        // then
        verify(diaryRepository, times(1)).
                save(any(Diary.class));
    }

    @Test
    void createDiaryWhenWeatherDoesNotExistInDB() {
        //given
        LocalDate date = LocalDate.of(2024, 9, 28);
        String text = "Cloudy day";

        ReflectionTestUtils.setField(diaryService,
                "apiKey", "f17662ca45dcc80a6d48951b6d3db1fd");

        given(dateWeatherRepository.findAllByDate(any(LocalDate.class)))
                .willReturn(Collections.emptyList());
        given(diaryRepository.save(any(Diary.class)))
                .willReturn(new Diary());

        // when
        diaryService.createDiary(date, text);

        // then
        verify(diaryRepository, times(1))
                .save(any(Diary.class));
    }

    @Test
    void readDiary() {
        // given
        LocalDate date = LocalDate.of(2024, 9, 28);
        Diary diary = new Diary();
        diary.setText("Test Diary");

        when(diaryRepository.findAllByDate(date))
                .thenReturn(Collections.singletonList(diary));

        // when
        List<Diary> diaries = diaryService.readDiary(date);

        // then
        assertEquals(1, diaries.size());
        assertEquals("Test Diary", diaries.get(0).getText());
    }

    @Test
    void readDiaries() {
        // given
        LocalDate startDate =
                LocalDate.of(2024, 9, 1);
        LocalDate endDate =
                LocalDate.of(2024, 9, 30);

        Diary diary1 = new Diary();
        diary1.setText("Diary 1");
        Diary diary2 = new Diary();
        diary2.setText("Diary 2");

        given(diaryRepository.findAllByDateBetween(startDate, endDate))
                .willReturn(Arrays.asList(diary1, diary2));

        // when
        List<Diary> diaries = diaryService.readDiaries(startDate, endDate);

        // then
        assertEquals(2, diaries.size());
        assertEquals("Diary 1", diaries.get(0).getText());
        assertEquals("Diary 2", diaries.get(1).getText());
    }

    @Test
    void updateDiary() {
        // given
        LocalDate date = LocalDate.of(2024, 9, 28);
        String updatedText = "Updated Diary";

        Diary existingDiary = new Diary();
        existingDiary.setText("Old Diary");

        given(diaryRepository.getFirstByDate(date))
                .willReturn(existingDiary);

        // when
        diaryService.updateDiary(date, updatedText);

        // then
        assertEquals("Updated Diary", existingDiary.getText());
        verify(diaryRepository, times(1))
                .save(existingDiary);
    }

    @Test
    void testDeleteDiary() {
        // given
        LocalDate date = LocalDate.of(2024, 9, 28);

        // when
        diaryService.deleteDiary(date);

        // then
        verify(diaryRepository, times(1))
                .deleteAllByDate(date);
    }
}