# MiniLMS 📚
Spring Boot 기반 학습 관리 시스템 (LMS) 미니 프로젝트

---

## 📅 Development Log

### 2025-08-21
- **프로젝트 세팅**
  - Spring Boot 기본 프로젝트 생성
  - Gradle/Maven 의존성 관리
  - Thymeleaf 템플릿 뷰 엔진 설정

- **Controller**
  - `HomeController` 생성 → `/` 요청 시 `home.html` 반환
  - `LoginController` 생성 → `/login` 요청 시 `login.html` 반환

- **Spring Security**
  - Spring Security 의존성 추가
  - 기본 로그인/로그아웃 기능 적용
  - `/login` 뷰와 연동
  - 로그인 성공 시 홈(`/`)으로 리다이렉트 설정
  - 로그인 실패 시 `/login?error=true` 처리

- **TODO**
  - 사용자 인증용 `UserDetailsService` 구현
  - `PasswordEncoder` 적용 (BCrypt)
  - 로그인 세션 및 사용자 권한(Role) 관리
  - 회원가입 기능 추가
  - DB 연동 및 JPA 설정

---

## 🛠 Tech Stack
- **Back-end**: Spring Boot, Spring Security
- **Front-end**: Thymeleaf
- **Build Tool**: Gradle / Maven
- **Version Control**: Git, GitHub

---

## 🚀 실행 방법
1. 저장소 클론
   ```bash
   git clone https://github.com/<your-username>/MiniLMS.git
   cd MiniLMS
