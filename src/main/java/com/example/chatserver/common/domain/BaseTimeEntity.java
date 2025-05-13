package com.example.chatserver.common.domain;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@MappedSuperclass
// 정의된 인스턴스 멤버를 상속받는 형식으로 처리한다.
// 물리적인 테이블 구조가 변경된다.
// BaseTimeEntity에 필드를 추가하면, 이를 상속한 엔티티의 테이블에 직접 해당 컬럼이 생성
@Getter
public class BaseTimeEntity {
    @CreationTimestamp
    private LocalDateTime createTime;

    @UpdateTimestamp
    private LocalDateTime updatedTime;
}
