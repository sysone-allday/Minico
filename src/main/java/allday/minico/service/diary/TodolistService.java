package allday.minico.service.diary;

import allday.minico.dao.diary.TodolistDAO;
import allday.minico.dto.diary.Todolist;

import java.time.LocalDate;
import java.util.List;

public class TodolistService {
    private final TodolistDAO todoDao = new TodolistDAO();

    public List<Todolist> getTodos(String memberId, LocalDate date) {
        return todoDao.getTodosByDate(memberId, date);
    }

    public long addTodo(String content, LocalDate date, String memberId) {
        return todoDao.insertTodo(content, date, memberId);
    }

    public void setDone(long id, boolean done) {
        todoDao.updateDone(id, done);
    }

    public void setContent(long id, String content) {
        todoDao.updateContent(id, content);
    }

    public void remove(long id) {
        todoDao.delete(id);
    }
}