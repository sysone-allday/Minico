package allday.minico.utils.skin;

import allday.minico.dto.member.Member;
import allday.minico.service.skin.SkinService;
import allday.minico.session.AppSession;

/**
 * 스킨 관련 유틸리티 클래스
 * 레벨업 시 자동 스킨 해금 및 미니룸 캐릭터 업데이트
 */
public class SkinUtil {
    
    private static final SkinService skinService = SkinService.getInstance();
    
    public static void handleLevelUp(Member member, int newLevel) {
        String memberId = member.getMemberId();
        String minimiType = member.getMinimi();
        
        // 레벨 2, 3 달성 시에만 새 스킨 해금
        if (newLevel == 2 || newLevel == 3) {
            // 미니미 변형 정보가 필요함 (추후 Member 테이블에 추가하거나 별도 조회)
            // 임시로 기본값 사용
            String minimiVariant = getMinimiVariant(memberId, minimiType);
            
            boolean unlocked = skinService.unlockLevelUpSkin(memberId, newLevel, minimiType, minimiVariant);
            
            if (unlocked) {
                System.out.println(String.format("레벨 %d 달성! 새로운 스킨이 해금되었습니다.", newLevel));
            }
        }
    }
    
    /**
     * 현재 사용자의 스킨 이미지 경로 반환 (미니룸에서 사용)
     * @return 현재 레벨에 맞는 스킨 이미지 경로
     */
    public static String getCurrentSkinImagePath() {
        Member loginMember = AppSession.getLoginMember();
        if (loginMember != null) {
            return skinService.getCurrentSkinImagePath(loginMember.getMemberId());
        }
        return null;
    }
    
    /**
     * 멤버의 미니미 변형 정보 조회
     * SKIN 테이블에서 사용자의 실제 캐릭터 이름을 조회
     */
    private static String getMinimiVariant(String memberId, String minimiType) {
        return getCurrentUserCharacterName(memberId);
    }
    
    /**
     * 현재 사용자의 캐릭터 이름을 SKIN 테이블에서 조회
     * 공통 메서드로 여러 클래스에서 사용 가능
     */
    public static String getCurrentUserCharacterName(String memberId) {
        try {
            String imagePath = skinService.getCurrentSkinImagePath(memberId);
            
            if (imagePath != null) {
                // 경로에서 파일명 추출: /allday/minico/images/char/male/온유_front.png → 온유_front.png
                String fileName = imagePath.substring(imagePath.lastIndexOf('/') + 1);
                // 파일명에서 캐릭터명 추출: 온유_front.png → 온유
                String characterName = fileName.split("_")[0];
                System.out.println("[SkinUtil] DB에서 조회한 캐릭터명: " + characterName);
                return characterName;
            }
        } catch (Exception e) {
            System.out.println("[SkinUtil] DB에서 캐릭터명 조회 실패: " + e.getMessage());
        }
        
        // 조회 실패 시 기본 캐릭터
        return "대호";
    }
    
    /**
     * 기본 스킨 이미지 경로 생성 (현재는 레벨 없이)
     */
    public static String generateSkinImagePath(String minimiType, String variant) {
        return String.format("/allday/minico/images/char/%s/%s.png", 
                           minimiType.toLowerCase(), variant.toLowerCase());
    }
    
    /**
     * 레벨에 따른 스킨 이미지 경로 생성 (추후 레벨 시스템 구현 시 사용)
     */
    public static String generateLevelSkinImagePath(String minimiType, String variant, int level) {
        return String.format("/allday/minico/images/char/%s/%s_lv%d.png", 
                           minimiType.toLowerCase(), variant.toLowerCase(), level);
    }
    
    /**
     * 현재 레벨에 맞는 스킨 레벨 계산
     */
    public static int calculateSkinLevel(int playerLevel) {
        if (playerLevel >= 3) return 3;
        if (playerLevel >= 2) return 2;
        return 1;
    }
}
