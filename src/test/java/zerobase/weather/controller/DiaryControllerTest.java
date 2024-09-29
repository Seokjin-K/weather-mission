package zerobase.weather.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import zerobase.weather.service.DiaryService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import zerobase.weather.domain.Diary;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(DiaryController.class)
class DiaryControllerTest {

    @MockBean
    private DiaryService diaryService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void successCreateDiary() throws Exception {
        //given
        LocalDate date = LocalDate.of(2024, 9, 28);
        String text = "This is a test diary entry.";
        doNothing().when(diaryService).createDiary(date, text); // 반환값 없음

        // when
        mockMvc.perform(post("/create/diary")
                        .param("date", date.toString())
                        .content(text)
                        .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isOk())
                .andDo(print());

        // 1번 호출되었는지 확인
        verify(diaryService, times(1))
                .createDiary(date, text);
    }

    @Test
    void failCreateDiary() throws Exception {
        // given
        LocalDate date = LocalDate.of(2024, 9, 28);
        String text = "text";

        doThrow(new RuntimeException())
                .when(diaryService).createDiary(date, text);

        // when
        mockMvc.perform(post("/create/diary")
                        .param("date", date.toString())
                        .content(text)
                        .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isInternalServerError())
                .andDo(print());

        verify(diaryService, times(1))
                .createDiary(date, text);
    }

    @Test
    void successReadDiary() throws Exception {
        // given
        LocalDate date = LocalDate.of(2024, 9, 25);
        Diary diary = Diary.builder()
                .id(1)
                .weather("clear")
                .icon("icon")
                .temperature(300.0)
                .text("Sample diary entry.")
                .date(date)
                .build();
        List<Diary> diaryList = Collections.singletonList(diary);

        given(diaryService.readDiary(date)).willReturn(diaryList);

        // when
        mockMvc.perform(get("/read/diary")
                        .param("date", date.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].text")
                        .value("Sample diary entry."))
                .andExpect(jsonPath("$[0].date")
                        .value((date.toString())))
                .andDo(print());

        verify(diaryService, times(1))
                .readDiary(date);
    }

    @Test
    void failReadDiary() throws Exception {
        // given
        LocalDate date = LocalDate.of(2024, 9, 25);

        given(diaryService.readDiary(date))
                .willThrow(new RuntimeException());

        // when
        mockMvc.perform(get("/read/diary")
                        .param("date", date.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isInternalServerError())
                .andDo(print());

        verify(diaryService, times(1))
                .readDiary(date);
    }

    @Test
    void successReadDiaries() throws Exception {
        // given
        LocalDate startDate = LocalDate.of(2024, 8, 25);
        LocalDate endDate = LocalDate.of(2024, 9, 25);
        List<Diary> diaryList = Arrays.asList(
                Diary.builder().id(1).weather("clear").icon("icon")
                        .temperature(300.0).text("Sample diary entry1.")
                        .date(startDate).build(),
                Diary.builder().id(2).weather("clear").icon("icon")
                        .temperature(300.0).text("Sample diary entry2.")
                        .date(endDate).build()
        );
        given(diaryService.readDiaries(startDate, endDate))
                .willReturn(diaryList);

        // when
        mockMvc.perform(get("/read/diaries")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].text")
                        .value("Sample diary entry1."))
                .andExpect(jsonPath("$[1].text")
                        .value("Sample diary entry2."))
                .andDo(print());

        verify(diaryService, times(1))
                .readDiaries(startDate, endDate);
    }

    @Test
    void successUpdateDiary() throws Exception {
        // given
        LocalDate date = LocalDate.of(2024, 9, 25);
        String text = "This is a test update diary entry.";
        doNothing().when(diaryService).updateDiary(date, text);

        // when
        mockMvc.perform(put("/update/diary")
                        .param("date", date.toString())
                        .content(text)
                        .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isOk())
                .andDo(print());

        verify(diaryService, times(1))
                .updateDiary(date, text);
    }

    @Test
    void failUpdateDiary() throws Exception {
        // given
        LocalDate date = LocalDate.of(2024, 9, 25);
        String updatedText = "Updated diary entry";

        doThrow(new RuntimeException())
                .when(diaryService).updateDiary(date, updatedText);

        // when
        mockMvc.perform(put("/update/diary")
                        .param("date", date.toString())
                        .content(updatedText)
                        .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isInternalServerError())
                .andDo(print());

        verify(diaryService, times(1))
                .updateDiary(date, updatedText);
    }

    @Test
    void successDeleteDiary() throws Exception {
        // given
        LocalDate date = LocalDate.of(2024, 9, 25);
        doNothing().when(diaryService).deleteDiary(date);

        // when
        mockMvc.perform(delete("/delete/diary")
                        .param("date", date.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isOk())
                .andDo(print());

        verify(diaryService, times(1))
                .deleteDiary(date);
    }

    @Test
    void failDeleteDiary() throws Exception {
        // given
        LocalDate date = LocalDate.of(2024, 9, 25);

        doThrow(new RuntimeException()).when(diaryService).deleteDiary(date);

        // when
        mockMvc.perform(delete("/delete/diary")
                        .param("date", date.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isInternalServerError())
                .andDo(print());

        verify(diaryService, times(1))
                .deleteDiary(date);
    }
}