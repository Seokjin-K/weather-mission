package zerobase.weather.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import zerobase.weather.domain.Diary;
import zerobase.weather.service.DiaryService;

import java.time.LocalDate;
import java.util.List;

@RestController
public class DiaryController {

    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    @ApiOperation("일기 텍스트와 날씨를 이용해서 DB에 일기 저장")
    @PostMapping("/create/diary")
    public void createDiary(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @ApiParam(value = "일기를 쓸 날짜", example = "2024-09-28")
            LocalDate date,
            @RequestBody String text
    ) {
        diaryService.createDiary(date, text);
    }

    @ApiOperation("입력한 날짜의 모든 일기 데이터를 가져옵니다.")
    @GetMapping("/read/diary")
    public List<Diary> readDiary(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @ApiParam(value = "일기를 가져올 날짜", example = "2024-09-25")
            LocalDate date
    ) {
        return diaryService.readDiary(date);
    }

    @ApiOperation("입력한 기간의 모든 일기 데이터를 가져옵니다.")
    @GetMapping("/read/diaries")
    public List<Diary> readDiaries(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @ApiParam(value = "조회할 기간 시작 날", example = "2024-08-25")
            LocalDate startDate,
            
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @ApiParam(value = "조회할 기간 마지막 날", example = "2024-09-25")
            LocalDate endDate
    ) {
        return diaryService.readDiaries(startDate, endDate);
    }

    @ApiOperation("입력한 날짜의 일기를 수정")
    @PutMapping("/update/diary")
    public void updateDiary(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @ApiParam(value = "수정할 일기의 날짜", example = "2024-09-25")
            LocalDate date,
            @RequestBody String text
    ) {
        diaryService.updateDiary(date, text);
    }

    @ApiOperation("입력한 날짜의 일기를 삭제")
    @DeleteMapping("/delete/diary")
    public void deleteDiary(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @ApiParam(value = "삭제할 일기의 날짜", example = "2024-09-25")
            LocalDate date
    ) {
        diaryService.deleteDiary(date);
    }
}
