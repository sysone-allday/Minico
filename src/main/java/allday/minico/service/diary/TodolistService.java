package allday.minico.service.diary;

import allday.minico.dao.diary.TodolistDAO;
import allday.minico.dto.diary.Todolist;

import java.time.LocalDate;
import java.util.List;

public class TodolistService {
    private final TodolistDAO todoDao = new TodolistDAO();

    // 투두리스트 전체 조회
    public List<Todolist> getTodos(String memberId, LocalDate date) {
        return todoDao.getTodosByDate(memberId, date);
    }

    // 새로운 투두 항목 추가 후, 생성된 투두 ID 반환
    public long addTodo(String content, LocalDate date, String memberId) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("할 일을 입력하세요.");
        }
        return todoDao.insertTodo(content, date, memberId);
    }

    // 특정 투두의 완료 여부 상태 변경
    public void setDone(long id, boolean done) {
        if (id <= 0) throw new IllegalArgumentException("유효하지 않은 ID입니다.");
        todoDao.updateDone(id, done);
    }

    // 특정 투두의 내용 수정
    public void setContent(long id, String content) {
        todoDao.updateContent(id, content);
    }

    // 특정 투두 삭제
    public void remove(long id) {
        todoDao.delete(id);
    }

    // 특정 날짜에 대한 잡초 비율(달성률 기반)을 PL/SQL 함수로 계산해 반환
    public double getWeedRatio(String id, LocalDate date) {
        return todoDao.getWeedRatio(id, date);
    }
}