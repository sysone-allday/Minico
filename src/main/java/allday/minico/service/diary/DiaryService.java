package allday.minico.service.diary;

import allday.minico.dao.diary.DiaryDAO;
import allday.minico.dto.diary.Diary;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@RequiredArgsConstructor
public class DiaryService {
    private final DiaryDAO diaryDao = new DiaryDAO();

    // 특정 회원의 특정 날짜 일기 조회
    public Diary getDiary(String memberId, LocalDate date) {
        return diaryDao.selectDiaryByDate(memberId, date);
    }

    // 새로운 일기 등록
    public boolean registerDiary(Diary diary) {
        return diaryDao.insertDiary(diary);
    }

    // 일기 수정
    public boolean editDiary(Diary diary) {
        return diaryDao.updateDiary(diary);
    }

    // 회원 ID에 따른 이미지 경로 조회 (기본 경로 제공)
    public String getImagePathFor(String memberId) {
        String path = DiaryDAO.findImagePathByMemberId(memberId);
        return (path == null || path.isBlank())
                ? "/allday/minico/images/character/민서미니미.png"
                : path;
    }



}