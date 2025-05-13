package com.example.chatserver.chat.domain;

import com.example.chatserver.common.domain.BaseTimeEntity;
import com.example.chatserver.member.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ChatParticipant extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false) //
    private ChatRoom chatRoom;

    // FetchType.Eager // 참조하는 대상 무조건
    // FetchType.Lazy // 참조하는 대상을 실제로 조회해야할 때 조회
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false) // fk
    private Member member; // 해당 타입에 대해 참조
}
