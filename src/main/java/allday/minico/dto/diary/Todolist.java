package allday.minico.dto.diary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Todolist {
    private long id;
    private String content;
    private boolean isDone;
}