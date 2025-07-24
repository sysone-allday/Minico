package allday.minico.utils.member;

/// 회원가입 양식 검증 클래스

public class Validator {

    public static boolean isInfoFill(String... memberInfo){ // 멤버 정보가 모두 입력되었으면 true
        for(String info : memberInfo){
            if(info == null || info.isEmpty()) return false;
        }
        return true;
    }
    public static boolean isValidEmail(String email) {// 이메일 양식 체크
        // ^                   문자열 시작
        // ([a-zA-Z0-9._%+-]+) 대소문자,숫자,특수기호 1개이상
        // @                   골뱅이 필수
        // ([a-zA-Z0-9.-]+)    대소문자,숫자,-]+ 1개이상
        // \.                  마침표 필수
        // ([a-z]{2,})         소문자 2자이상
        // (?:\.([a-z]{2,}))*  (마침표필수 + 소문자 2개이상) 0개 이상
        // $                   문자열 끝

        if(email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-z]{2,6}$")) {System.out.println("이메일 양식 적합함"); return true;}
        else {System.out.println("이메일 양식 부적합"); return false;}
    }
    public static boolean isPasswordMatch(String password, String confirmPassword) { // "비밀번호" 와 "비밀번호 확인' 이 같으면 true
        if (password.equals(confirmPassword)) {System.out.println("비밀번호 확인 체크");return true;}
        else {System.out.println("비밀번호와 비밀번호 확인이 불일치"); return false;}
    }

    public static boolean isIdChecked(String id, String checkAvailabilityStatus) { // 입력 id와 중복체크 시 id가 같을 경우 true
        if (id.equals(checkAvailabilityStatus)) {return true;}
        else { System.out.println("ID 중복확인을 하지않음"); return false;}
    }

    public static boolean isIdFormat(String memberId) { // ID 는 소문자와 숫자 혼용, 8-30자 조건
        if (memberId.matches("^(?=.*[a-z])(?=.*[0-9])[a-z0-9]{8,30}$")) {return true;}
        else {return false;}
    }

    public static boolean isPwFormatMatch(String password) { // 비밀번호는 소문자와 숫자를 포함하고 8~20자 이내
        if (password.matches("^(?=.*[a-z])(?=.*[0-9])[a-z0-9]{8,20}$")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isNicknameFormatMatch(String nickname){ // 닉네임은 대,소문자,한글,숫자 2~10 자
        if(nickname.matches("^[a-zA-Z0-9가-힣]{2,10}$")) {return true;}
        else {return false;}
    }

    public static boolean isPwHintFormatMatch(String text) { // 비밀번호 힌트는 특수문자,영어,한글 20자 이하
        if (text != null && text.matches("^.{1,20}$")) {
            return true;
        } else {
            return false;
        }
    }
}
