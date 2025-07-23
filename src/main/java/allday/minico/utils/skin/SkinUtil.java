package allday.minico.utils.skin;

import allday.minico.dto.member.Member;
import allday.minico.service.member.MemberService;
import allday.minico.service.skin.SkinService;
import allday.minico.session.AppSession;
import java.util.HashMap;
import java.util.Map;


public class SkinUtil {
    
    private static final SkinService skinService = SkinService.getInstance();
    private static final MemberService memberService = new MemberService();
    
    // 캐릭터 정보 캐싱을 위한 맵 (memberId -> {gender, characterName})
    private static final Map<String, CharacterInfo> characterCache = new HashMap<>();
    
    // 캐릭터 정보를 저장하는 내부 클래스
    private static class CharacterInfo {
        final String gender;
        final String characterName;
        
        CharacterInfo(String gender, String characterName) {
            this.gender = gender;
            this.characterName = characterName;
        }
    }
    
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
    

    public static String getCurrentSkinImagePath() {
        Member loginMember = AppSession.getLoginMember();
        if (loginMember != null) {
            return skinService.getCurrentSkinImagePath(loginMember.getMemberId());
        }
        return null;
    }
    

    private static String getMinimiVariant(String memberId, String minimiType) {
        return getCurrentUserCharacterName(memberId);
    }
    
    /**
     * 현재 사용자의 캐릭터 이름을 SKIN 테이블에서 조회 (캐싱 적용)
     * 공통 메서드로 여러 클래스에서 사용 가능
     */
    public static String getCurrentUserCharacterName(String memberId) {
        // 캐시에서 먼저 확인
        CharacterInfo cached = characterCache.get(memberId);
        if (cached != null) {
            // System.out.println("[SkinUtil] getCurrentUserCharacterName 캐시에서 반환: " + cached.characterName + " (memberId: " + memberId + ")");
            return cached.characterName;
        }
        
        // System.out.println("[SkinUtil] getCurrentUserCharacterName DB 조회 시작 (memberId: " + memberId + ")");
        
        // 캐시에 없으면 DB에서 조회하고 캐시에 저장
        try {
            String imagePath = skinService.getCurrentSkinImagePath(memberId);
            // System.out.println("[SkinUtil] DB에서 조회한 이미지 경로: " + imagePath);
            
            if (imagePath != null) {
                // 경로에서 파일명 추출: /allday/minico/images/char/male/온유_front.png → 온유_front.png
                String fileName = imagePath.substring(imagePath.lastIndexOf('/') + 1);
                // 파일명에서 캐릭터명 추출: 온유_front.png → 온유
                String characterName = fileName.split("_")[0];
                
                // 성별 정보 추출: /allday/minico/images/char/male/온유_front.png → male
                String gender = imagePath.contains("/male/") ? "male" : "female";
                
                // 캐시에 저장
                characterCache.put(memberId, new CharacterInfo(gender, characterName));
                
                // System.out.println("[SkinUtil] DB에서 조회한 캐릭터명: " + characterName + " (성별: " + gender + ", 캐시 저장됨)");
                return characterName;
            }
        } catch (Exception e) {
            // System.out.println("[SkinUtil] DB에서 캐릭터명 조회 실패: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 조회 실패 시 기본 캐릭터
        // System.out.println("[SkinUtil] 기본 캐릭터 반환: 대호");
        return "대호";
    }
    
    /**
     * 캐싱된 캐릭터 정보를 기반으로 방향별 이미지 경로 생성 (성능 최적화)
     * @param memberId 사용자 ID
     * @param direction 방향 (LEFT, RIGHT, UP, DOWN, front)
     * @return 캐릭터 이미지 경로
     */
    public static String getCharacterImagePath(String memberId, String direction) {
        // 캐시에서 캐릭터 정보 조회 (없으면 DB에서 조회하여 캐시)
        CharacterInfo characterInfo = getOrCacheCharacterInfo(memberId);
        
        if (characterInfo != null) {
            String directionSuffix = getDirectionSuffix(direction);
            String imagePath = String.format("/allday/minico/images/char/%s/%s_%s.png", 
                                           characterInfo.gender, characterInfo.characterName, directionSuffix);
            return imagePath;
        }
        
        // 기본 캐릭터 경로
        String directionSuffix = getDirectionSuffix(direction);
        return "/allday/minico/images/char/male/대호_" + directionSuffix + ".png";
    }

    /**
     * 전달받은 캐릭터 정보를 직접 사용하여 이미지 경로 생성 (방문자용)
     * @param memberId 사용자 ID (null 가능)
     * @param characterInfo 캐릭터 정보 (형식: "Male:온유" 또는 "Female:민서")
     * @param direction 방향 (LEFT, RIGHT, UP, DOWN, front)
     * @return 캐릭터 이미지 경로
     */
    public static String getCharacterImagePath(String memberId, String characterInfo, String direction) {
        // System.out.println("[SkinUtil] getCharacterImagePath 호출 - memberId: " + memberId + ", characterInfo: " + characterInfo + ", direction: " + direction);
        
        try {
            // characterInfo가 완전한 형식("Male:온유")인 경우
            if (characterInfo != null && characterInfo.contains(":")) {
                String[] parts = characterInfo.split(":");
                if (parts.length >= 2 && !parts[1].trim().isEmpty()) {
                    String gender = parts[0].toLowerCase(); // "male" 또는 "female"
                    String characterName = parts[1]; // "온유", "민서" 등
                    
                    // "Variant"가 포함된 잘못된 캐릭터명이면 기본값으로 대체
                    if (characterName.toLowerCase().contains("variant")) {
                        characterName = gender.equals("male") ? "대호" : "민서";
                        // System.out.println("[SkinUtil] 잘못된 캐릭터명 감지, 기본값으로 대체: " + characterName);
                    }
                    
                    String directionSuffix = getDirectionSuffix(direction);
                    String imagePath = String.format("/allday/minico/images/char/%s/%s_%s.png", 
                                                     gender, characterName, directionSuffix);
                    // System.out.println("[SkinUtil] 완전한 캐릭터 정보로 생성된 경로: " + imagePath);
                    return imagePath;
                }
            }
            
            // characterInfo가 성별만 있는 경우("Female", "Male") - DB에서 전체 정보 조회
            if (characterInfo != null && (characterInfo.equals("Male") || characterInfo.equals("Female"))) {
                // System.out.println("[SkinUtil] 성별만 있는 경우, 기본 캐릭터 사용");
                // 성별만 있는 경우 기본 캐릭터 사용
                String gender = characterInfo.toLowerCase();
                String defaultCharacter = gender.equals("male") ? "대호" : "민서";
                String directionSuffix = getDirectionSuffix(direction);
                String imagePath = String.format("/allday/minico/images/char/%s/%s_%s.png", 
                                                 gender, defaultCharacter, directionSuffix);
                // System.out.println("[SkinUtil] 성별 기반 기본 캐릭터 경로: " + imagePath);
                return imagePath;
            }
            
            // characterInfo가 유효하지 않으면 memberId로 캐시된 정보 사용
            if (memberId != null) {
                // System.out.println("[SkinUtil] memberId로 캐시된 정보 사용");
                return getCharacterImagePath(memberId, direction);
            }
            
        } catch (Exception e) {
            // System.out.println("[SkinUtil] 방문자 캐릭터 이미지 경로 생성 실패: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 실패 시 기본 캐릭터
        String directionSuffix = getDirectionSuffix(direction);
        String defaultPath = "/allday/minico/images/char/male/대호_" + directionSuffix + ".png";
        // System.out.println("[SkinUtil] 기본 캐릭터 경로 반환: " + defaultPath);
        return defaultPath;
    }
    
    /**
     * 캐릭터 정보를 캐시에서 가져오거나, 없으면 DB에서 조회하여 캐시에 저장
     */
    private static CharacterInfo getOrCacheCharacterInfo(String memberId) {
        // 캐시에서 먼저 확인
        CharacterInfo cached = characterCache.get(memberId);
        if (cached != null) {
            return cached;
        }
        
        // 캐시에 없으면 getCurrentUserCharacterName을 호출하여 캐시 생성
        getCurrentUserCharacterName(memberId);
        return characterCache.get(memberId);
    }
    
    /**
     * 방향 문자열을 파일명 접미사로 변환
     */
    private static String getDirectionSuffix(String direction) {
        switch (direction) {
            case "LEFT": return "left";
            case "RIGHT": return "right";
            case "UP": return "back";
            case "DOWN":
            case "front":
            default: return "front";
        }
    }
    
    /**
     * 캐릭터 정보 캐시 초기화 (사용자 로그아웃 시 호출)
     */
    public static void clearCharacterCache(String memberId) {
        characterCache.remove(memberId);
        // System.out.println("[SkinUtil] " + memberId + "의 캐릭터 캐시 제거됨");
    }
    
    /**
     * 전체 캐릭터 정보 캐시 초기화
     */
    public static void clearAllCharacterCache() {
        characterCache.clear();
        // System.out.println("[SkinUtil] 모든 캐릭터 캐시 초기화됨");
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
    
    /**
     * 현재 사용자의 캐릭터 정보를 반환 (형식: "Male:온유")
     * @param memberId 멤버 ID
     * @return 캐릭터 정보 문자열
     */
    public static String getCurrentUserCharacterInfo(String memberId) {
        // 캐시에서 먼저 확인
        CharacterInfo cached = characterCache.get(memberId);
        if (cached != null) {
            // 첫글자 대문자로 변환: male -> Male
            String genderCapitalized = cached.gender.substring(0, 1).toUpperCase() + cached.gender.substring(1);
            String result = genderCapitalized + ":" + cached.characterName;
            // System.out.println("[SkinUtil] 캐시에서 캐릭터 정보 반환: " + result + " (memberId: " + memberId + ")");
            return result;
        }
        
        // System.out.println("[SkinUtil] 캐시 없음, DB에서 조회 시작 (memberId: " + memberId + ")");
        
        // 캐시에 없으면 getCurrentUserCharacterName을 호출하여 캐시 생성
        getCurrentUserCharacterName(memberId);
        
        // 다시 캐시에서 확인
        cached = characterCache.get(memberId);
        if (cached != null) {
            String genderCapitalized = cached.gender.substring(0, 1).toUpperCase() + cached.gender.substring(1);
            String result = genderCapitalized + ":" + cached.characterName;
            // System.out.println("[SkinUtil] 캐시 생성 후 캐릭터 정보 반환: " + result + " (memberId: " + memberId + ")");
            return result;
        }
        
        // 실패 시 기본값 반환
        // System.out.println("[SkinUtil] 캐릭터 정보 조회 실패, 기본값 반환: Male:대호 (memberId: " + memberId + ")");
        return "Male:대호";
    }
    
    /**
     * 닉네임으로 해당 사용자의 캐릭터 정보를 조회 (소켓 통신용)
     * @param nickname 사용자 닉네임
     * @return 캐릭터 정보 (형식: "Male:온유")
     */
    public static String getCharacterInfoByNickname(String nickname) {
        try {
            // Member 테이블에서 nickname으로 memberId 조회
            String memberId = getMemberIdByNickname(nickname);
            if (memberId != null) {
                return getCurrentUserCharacterInfo(memberId);
            }
        } catch (Exception e) {
            // System.out.println("[SkinUtil] 닉네임으로 캐릭터 정보 조회 실패: " + e.getMessage());
        }
        
        // 실패 시 기본값
        return "Male:대호";
    }
    
    /**
     * 닉네임으로 memberId 조회 (간단한 구현)
     */
    private static String getMemberIdByNickname(String nickname) {
        try {
            // 현재 로그인한 사용자와 비교
            Member loginMember = AppSession.getLoginMember();
            if (loginMember != null && nickname.equals(loginMember.getNickname())) {
                return loginMember.getMemberId();
            }
            
            // DB에서 닉네임으로 멤버 조회
            Member member = memberService.getMemberByNickname(nickname);
            if (member != null) {
                return member.getMemberId();
            }
            
        } catch (Exception e) {
            // System.out.println("[SkinUtil] 닉네임으로 memberId 조회 실패: " + e.getMessage());
        }
        
        return null;
    }
}
