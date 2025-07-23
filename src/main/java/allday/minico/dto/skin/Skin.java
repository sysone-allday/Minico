package allday.minico.dto.skin;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class Skin {
    private String minimiType; //남자 여자 선택
    private int levelNo;  // 스킨별 레벨?
    private String imagePath; //이미지 경로
    private String memberId; //멤버 아이디
}