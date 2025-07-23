package allday.minico.service.skin;

import allday.minico.dao.skin.SkinDAO;
import allday.minico.dto.skin.Skin;
import allday.minico.session.AppSession;

import java.sql.SQLException;
import java.util.List;

public class SkinService {

    private final SkinDAO skinDAO = SkinDAO.getInstance();
    private static final SkinService instance = new SkinService();

    private SkinService() {}

    public static SkinService getInstance() {
        return instance;
    }

    // 사용자의 모든 스킨 조회
    public List<Skin> getUserSkins(String memberId) {
        try {
            return skinDAO.selectUserSkins(memberId);
        } catch (SQLException e) {
            System.out.println("스킨 목록 조회 중 예외 발생");
            e.printStackTrace();
            return null;
        }
    }

    // 특정 미니미 타입의 스킨 조회
    public List<Skin> getSkinsByType(String memberId, String minimiType) {
        try {
            return skinDAO.selectSkinsByType(memberId, minimiType);
        } catch (SQLException e) {
            System.out.println("미니미 타입별 스킨 조회 중 예외 발생");
            e.printStackTrace();
            return null;
        }
    }

    // 새로운 스킨 획득
    public boolean acquireSkin(String minimiType, int levelNo, String imagePath) {
        String memberId = AppSession.getLoginMember().getMemberId();

        try {
            // 이미 보유한 스킨인지 확인
            if (skinDAO.checkSkinOwnership(memberId, minimiType, levelNo)) {
                // System.out.println("이미 보유한 스킨입니다.");
                return false;
            }

            Skin skin = new Skin();
            skin.setMemberId(memberId);
            skin.setMinimiType(minimiType);
            skin.setLevelNo(levelNo);
            skin.setImagePath(imagePath);

            return skinDAO.insertSkin(skin);
        } catch (SQLException e) {
            System.out.println("스킨 획득 중 예외 발생");
            e.printStackTrace();
            return false;
        }
    }

    // 스킨 소유 여부 확인
    public boolean hasSkin(String memberId, String minimiType, int levelNo) {
        try {
            return skinDAO.checkSkinOwnership(memberId, minimiType, levelNo);
        } catch (SQLException e) {
            System.out.println("스킨 소유 여부 확인 중 예외 발생");
            e.printStackTrace();
            return false;
        }
    }
    
    // 회원가입 시 선택한 미니미의 기본 스킨 생성
    public boolean createDefaultSkin(String memberId, String minimiType, String minimiVariant) {
        try {
            // 남/여 모두 동일한 파일명 규칙: {캐릭터명}_front.png
            String imagePath = String.format("/allday/minico/images/char/%s/%s_front.png", 
                               minimiType.toLowerCase(), minimiVariant);

            String uniqueMinimiType = minimiType + "_" + memberId;
            
            System.out.println(String.format("[SkinService] 기본 스킨 생성: %s, %s, %s", memberId, uniqueMinimiType, imagePath));
            
            // 레벨은 일단 1로 고정
            boolean result = skinDAO.insertDefaultSkin(memberId, uniqueMinimiType, imagePath);
            
            if (result) {
                System.out.println("[SkinService] 기본 스킨 생성 성공");
            } else {
                System.out.println("[SkinService] 기본 스킨 생성 실패");
            }
            
            return result;
        } catch (SQLException e) {
            System.out.println("기본 스킨 생성 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // 사용자의 현재 레벨에 맞는 스킨 조회
    public Skin getCurrentSkin(String memberId) {
        try {
            return skinDAO.selectCurrentSkin(memberId);
        } catch (SQLException e) {
            System.out.println("현재 스킨 조회 중 예외 발생");
            e.printStackTrace();
            return null;
        }
    }
    
    // 레벨업 시 자동으로 새 스킨 해금 및 적용
    public boolean unlockLevelUpSkin(String memberId, int newLevel, String minimiType, String minimiVariant) {
        try {
            // 레벨 2, 3일 때만 새 스킨 해금
            if (newLevel == 2 || newLevel == 3) {
                String imagePath = String.format("/allday/minico/char/%s/%s_lv%d.png", 
                                   minimiType.toLowerCase(), minimiVariant.toLowerCase(), newLevel);
                
                return skinDAO.insertLevelUpSkin(memberId, newLevel, imagePath);
            }
            return false;
        } catch (SQLException e) {
            System.out.println("레벨업 스킨 해금 중 예외 발생");
            e.printStackTrace();
            return false;
        }
    }
    
    // 미니룸에서 현재 스킨 이미지 경로 반환
    public String getCurrentSkinImagePath(String memberId) {
        Skin currentSkin = getCurrentSkin(memberId);
        return currentSkin != null ? currentSkin.getImagePath() : null;
    }
}