package allday.minico.dto.diary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// 김민서 파트
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Diary {
    private Long id;
    private String content;
    private LocalDate writtenAt;
    private String memberId;
}