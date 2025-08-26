CREATE DATABASE IF NOT EXISTS MiniLMS
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;
USE MiniLMS;
drop table member;

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

CREATE TABLE IF NOT EXISTS enrollment (
      id BIGINT AUTO_INCREMENT PRIMARY KEY,
      member_pk BIGINT NOT NULL,
      course_id BIGINT NOT NULL,
      enrolled_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
      UNIQUE KEY uq_enrollment_member_course (member_pk, course_id),
      KEY idx_enrollment_member_pk (member_pk),
      KEY idx_enrollment_course (course_id),
      CONSTRAINT fk_enroll_member_pk FOREIGN KEY (member_pk) REFERENCES member(id)
          ON DELETE CASCADE ON UPDATE CASCADE,
      CONSTRAINT fk_enroll_course FOREIGN KEY (course_id) REFERENCES course(course_id)
          ON DELETE CASCADE ON UPDATE CASCADE
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
