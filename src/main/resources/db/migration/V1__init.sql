CREATE DATABASE IF NOT EXISTS MiniLMS
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;
USE MiniLMS;
drop table member;
drop table enrollment;
-- 테이블 생성

-- 회원 정보 테이블
CREATE TABLE IF NOT EXISTS member(
     id BIGINT AUTO_INCREMENT PRIMARY KEY,
     member_id     VARCHAR(50)  NOT NULL UNIQUE,   -- 로그인 ID(업무 키)
     password_hash VARCHAR(100) NOT NULL,
     nickname      VARCHAR(30)  NOT NULL UNIQUE,
     email         VARCHAR(320) NOT NULL UNIQUE,
     birth_year    INT,
     enabled       TINYINT(1) NOT NULL DEFAULT 1,
     avatar_url    VARCHAR(255) NULL,
     created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
     updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS course (
      course_id BIGINT AUTO_INCREMENT PRIMARY KEY,
      title VARCHAR(200) NOT NULL,
      description TEXT,
      instructor VARCHAR(100) NOT NULL,
      category VARCHAR(50),
      status ENUM('OPEN','CLOSED') NOT NULL DEFAULT 'OPEN',
      created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
ALTER TABLE course ADD COLUMN thumbnail_url VARCHAR(255);

CREATE TABLE enrollment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_pk BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    status       VARCHAR(12)  NOT NULL DEFAULT 'ENROLLED',
    enrolled_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    canceled_at  DATETIME NULL,
    updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE KEY uq_enrollment_member_course (member_pk, course_id),
    KEY idx_enrollment_member_pk (member_pk),
    KEY idx_enrollment_course (course_id),
    CONSTRAINT fk_enroll_member_pk FOREIGN KEY (member_pk) REFERENCES member(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_enroll_course FOREIGN KEY (course_id) REFERENCES course(course_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS progress (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_pk BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    progress_percent DOUBLE NOT NULL DEFAULT 0,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_progress_member_course (member_pk, course_id),
    KEY idx_progress_member_pk (member_pk),
    KEY idx_progress_course (course_id),
    CHECK (progress_percent >= 0 AND progress_percent <= 100),
    CONSTRAINT fk_progress_member_pk FOREIGN KEY (member_pk) REFERENCES member(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_progress_course FOREIGN KEY (course_id) REFERENCES course(course_id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_pk BIGINT NOT NULL,
    message VARCHAR(255) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_notif_member_pk_created (member_pk, created_at),
    CONSTRAINT fk_notif_member_pk FOREIGN KEY (member_pk) REFERENCES member(id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
ALTER TABLE member
    CONVERT TO CHARACTER SET utf8mb4
        COLLATE utf8mb4_0900_ai_ci;

ALTER TABLE course
    MODIFY instructor VARCHAR(100)
        CHARACTER SET utf8mb4
        COLLATE utf8mb4_0900_ai_ci
        NOT NULL;

INSERT INTO course (title, description, instructor, category, status, created_at)
VALUES
-- BACKEND
('스프링 부트 입문',
 '스프링 부트를 활용해 웹 애플리케이션을 처음부터 구현해보는 과정입니다.',
 '홍길동',
 'BACKEND',
 'open',
 NOW() - INTERVAL 20 DAY),

('JPA 완전정복',
 'Entity 매핑, 연관관계, 성능 최적화까지 JPA 핵심 내용을 다룹니다.',
 '이몽룡',
 'BACKEND',
 'open',
 NOW() - INTERVAL 15 DAY),

('REST API 설계와 보안',
 'RESTful API를 설계하고 JWT, OAuth2 인증을 적용하는 방법을 학습합니다.',
 '김철수',
 'BACKEND',
 'closed',
 NOW() - INTERVAL 10 DAY),

('Spring Security 심화',
 '실무에서 활용되는 보안 패턴과 권한 제어를 스프링 시큐리티로 구현합니다.',
 '박영희',
 'BACKEND',
 'open',
 NOW() - INTERVAL 5 DAY),

-- FRONTEND
('HTML/CSS 기본기',
 '웹 개발을 시작하는 입문자를 위한 필수 HTML/CSS 강좌입니다.',
 '성춘향',
 'FRONTEND',
 'open',
 NOW() - INTERVAL 25 DAY),

('모던 자바스크립트',
 'ES6+ 문법과 최신 자바스크립트 기능을 중심으로 프로젝트에 적용합니다.',
 '최우식',
 'FRONTEND',
 'closed',
 NOW() - INTERVAL 18 DAY),

('React로 배우는 SPA',
 '리액트를 활용해 싱글 페이지 애플리케이션을 제작합니다.',
 '한소희',
 'FRONTEND',
 'open',
 NOW() - INTERVAL 12 DAY),

('Vue.js 빠른 시작',
 'Vue.js 3 버전으로 컴포넌트 기반 웹을 구축하는 방법을 학습합니다.',
 '장원영',
 'FRONTEND',
 'open',
 NOW() - INTERVAL 7 DAY),

-- DATA
('데이터 분석 입문',
 'Python, Pandas, Numpy를 활용한 데이터 분석 기본기를 다집니다.',
 '강호동',
 'DATA',
 'open',
 NOW() - INTERVAL 30 DAY),

('SQL 기초와 활용',
 'SELECT, JOIN, GROUP BY 등 SQL 기본 문법을 학습합니다.',
 '유재석',
 'DATA',
 'open',
 NOW() - INTERVAL 20 DAY),

('빅데이터 처리와 하둡',
 'Hadoop, Spark를 활용한 빅데이터 처리 파이프라인 구축 과정입니다.',
 '이수근',
 'DATA',
 'closed',
 NOW() - INTERVAL 14 DAY),

('머신러닝 기초',
 'Scikit-learn으로 분류, 회귀 모델을 실습하며 머신러닝 기초를 배웁니다.',
 '양세형',
 'DATA',
 'open',
 NOW() - INTERVAL 3 DAY),

-- ETC
('UI/UX 디자인 원리',
 '사용자 친화적인 UI와 UX를 설계하기 위한 원리를 다룹니다.',
 '김민정',
 'ETC',
 'open',
 NOW() - INTERVAL 22 DAY),

('Git/GitHub 협업',
 '버전 관리와 팀 협업을 위한 Git, GitHub 활용법을 학습합니다.',
 '이도현',
 'ETC',
 'closed',
 NOW() - INTERVAL 16 DAY),

('클라우드 컴퓨팅 개요',
 'AWS, GCP, Azure의 기본 서비스와 클라우드 컴퓨팅 개념을 다룹니다.',
 '박보검',
 'ETC',
 'open',
 NOW() - INTERVAL 11 DAY),

('Docker & Kubernetes',
 '컨테이너 기반 배포 환경을 구축하고 운영 자동화를 학습합니다.',
 '정해인',
 'ETC',
 'open',
 NOW() - INTERVAL 6 DAY),

('알고리즘과 자료구조',
 '기초 자료구조와 정렬, 탐색 알고리즘을 학습합니다.',
 '아이유',
 'ETC',
 'closed',
 NOW() - INTERVAL 28 DAY),

('테스트 주도 개발',
 'TDD(Test Driven Development)의 개념과 JUnit 실습을 진행합니다.',
 '박서준',
 'BACKEND',
 'open',
 NOW() - INTERVAL 9 DAY),

('네트워크 기본기',
 'TCP/IP, HTTP, HTTPS 등 웹 개발자가 꼭 알아야 할 네트워크 기본 지식.',
 '공유',
 'BACKEND',
 'closed',
 NOW() - INTERVAL 4 DAY),

('DevOps 파이프라인 구축',
 'CI/CD 파이프라인을 Jenkins와 GitHub Actions로 구축합니다.',
 '수지',
 'ETC',
 'open',
 NOW() - INTERVAL 2 DAY);
