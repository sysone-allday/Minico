# 🎮 Minico

![minico_logo](https://github.com/user-attachments/assets/5fe5912e-28eb-4e03-af74-bf99737f84e5)

> 가상의 미니룸 게임을 즐길 수 있는 JavaFX 기반 데스크톱 애플리케이션

![Java](https://img.shields.io/badge/Java-21-orange.svg)
![JavaFX](https://img.shields.io/badge/JavaFX-21-brightgreen.svg)

## 📖 프로젝트 소개

Minico는 사용자들이 가상의 미니룸에서 다양한 게임과 소셜 기능을 즐길 수 있는 데스크톱 애플리케이션입니다.
JavaFX를 기반으로 제작되었으며, 친구와의 소통, 게임, 다이어리 작성 등 다양한 기능을 제공합니다.

### ✨ 주요 기능

- 🏠 **미니룸**: 개인 공간에서 유저들과 소통 (채팅, 캐릭터 움직임) 
- 🎯 **OX 게임**: 실시간 퀴즈 게임
- ⌨️ **타이핑 게임**: 타자 실력 향상 게임
- 📝 **다이어리**: 개인 일기 작성 및 관리
- 💌 **단어장**: 타이핑 게임에서의 문제 다시 풀이
- 👥 **친구 관리**: 친구 추가, 검색, 정보 확인

## 🛠️ 기술 스택

### Frontend

- **JavaFX 21**: 사용자 인터페이스 프레임워크
- **FXML**: UI 레이아웃 정의
- **CSS**: 스타일링

### Backend

- **Java 21**: 메인 프로그래밍 언어
- **Oracle Database**: 데이터베이스
- **JDBC**: 데이터베이스 연결

### Build Tool

- **Maven**: 의존성 관리 및 빌드 도구

### Architecture

- **MVC Pattern**: Model-View-Controller 아키텍처
- **DAO Pattern**: 데이터 액세스 객체 패턴
- **DTO Pattern**: 데이터 전송 객체 패턴

## 📁 프로젝트 구조

```text
minico/
├── src/main/java/allday/minico/
│   ├── Main.java                 # 애플리케이션 진입점
│   ├── controller/               # 컨트롤러
│   │   ├── diary/               # 다이어리 관련 컨트롤러
│   │   ├── member/              # 회원 관리 컨트롤러
│   │   ├── miniroom/            # 미니룸 컨트롤러
│   │   ├── note/                # 단어장 컨트롤러
│   │   ├── oxgame/              # OX게임 컨트롤러
│   │   └── typinggame/          # 타이핑게임 컨트롤러
│   ├── dao/                     # 데이터 액세스 객체
│   ├── dto/                     # 데이터 전송 객체
│   ├── service/                 # 비즈니스 로직
│   ├── network/                 # 네트워크 통신
│   ├── utils/                   # 유틸리티 클래스
│   └── ui/                      # UI 관련 클래스
├── src/main/resources/
│   ├── allday/minico/
│   │   ├── view/                # FXML 파일들
│   │   ├── css/                 # 스타일
│   │   ├── images/              # 이미지 리소스
│   │   ├── audio/               # 오디오 파일
│   │   └── fonts/               # 폰트 파일
│   └── database.properties      # 데이터베이스 설정
├── lib/                         #
└── pom.xml                      # Maven 설정 파일
```

## 🚀 시작하기

### 필수 요구사항

- **Java 21** 
- **Maven 3.6** 
- **Oracle Database** (연결 설정 필요)
- **JavaFX** (Java 21에 포함)

### 실행

1. **저장소 클론**

   ```bash
   git clone https://github.com/sysone-allday/Minico.git
   cd Minico
   ```

2. **데이터베이스 설정**

   ```properties
   # src/main/resources/database.properties 파일 수정
   db.url=jdbc:oracle:thin:@localhost:1521:xe
   db.username=your_username
   db.password=your_password
   ```

4. **애플리케이션 실행**

src\main\java\allday\minico\Main.java 실행

### 개발 환경 설정

1. **IDE 설정** (IntelliJ IDEA 권장)
   - JavaFX 플러그인 설치
   - Java 21로 설정

## 🎯 사용법

### 회원가입 및 로그인

1. 애플리케이션 실행 시 로그인 화면이 나타납니다
2. 신규 사용자는 회원가입을 통해 계정을 생성합니다
3. 기존 사용자는 아이디/비밀번호로 로그인합니다

### 주요 기능 사용법

- **미니룸 입장**: 메인 화면에서 미니룸 방문 버튼 클릭
- **게임 참여**: 메뉴에서 원하는 게임 선택
- **친구 추가**: 친구 검색 기능을 통해 친구 추가
- **다이어리 작성**: 다이어리 메뉴에서 새 일기 작성

## 😍 팀원 소개

| 이름 | 역할 | GitHub | 담당 기능 |
|------|------|--------|-----------|
| [정소영] | 팀장/풀스택 개발 | [@so2043](https://github.com/so2043) | UI 디자인, 타자게임, 빈칸게임, OCI관리|
| [김대호] | 풀스택 개발 | [@DHowor1d](https://github.com/DHowor1d) | UX 설계, 네트워크 통신, 미니룸, UI 구현 |
| [김민서] | 풀스택 개발 | [@mimmimkim](https://github.com/mimmimkim) | 다이어리 기능 구현, 일기장, 날씨, 오늘의 기분 |
| [김슬기] | 풀스택 개발 | [@ksks1234](https://github.com/ksks1234) | 데이터베이스 설계, OX게임, UI/UX 디자인 |
| [최온유] | 풀스택 개발 | [@onu96](https://github.com/onu96) | 회원가입 및 회원관련 로직 구현 |

## 📋 개발

주요 이슈:

- 네트워크 연결 불안정 시 재연결 로직 개선 필요

## 📆 프로젝트 기간 

2025년 7월 14일 ~ 7월 25일  

기획 및 설계: 2025.07.14 ~ 2025.07.16  

개발: 2025.07.16 ~ 2025.07.23  

버그 수정 및 산출물 정리: 2025.07.23 ~ 2025.01.25  

---

## 📞 연락처

프로젝트와 관련된 문의사항이 있으시면 언제든 연락주세요:

- **프로젝트 저장소**: [https://github.com/sysone-allday/Minico](https://github.com/sysone-allday/Minico)
- **이메일**: [zkzkasd@jbnu.ac.kr]

⭐ 이 프로젝트가 마음에 드신다면 Star를 눌러주세요!