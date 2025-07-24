package allday.minico.service.diary;

import allday.minico.dao.diary.DiaryDAO;
import allday.minico.dto.diary.Diary;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@RequiredArgsConstructor
public class DiaryService {
    private final DiaryDAO diaryDao = new DiaryDAO();

    public Diary getDiary(String memberId, LocalDate date) {
        return diaryDao.selectDiaryByDate(memberId, date);
    }
    public boolean registerDiary(Diary diary) {
        return diaryDao.insertDiary(diary);
    }
    public boolean editDiary(Diary diary) {
        return diaryDao.updateDiary(diary);
    }
    public String getImagePathFor(String memberId) {
        String path = DiaryDAO.findImagePathByMemberId(memberId);
        return (path == null || path.isBlank())
                ? "/allday/minico/images/character/민서미니미.png"
                : path;
    }



}