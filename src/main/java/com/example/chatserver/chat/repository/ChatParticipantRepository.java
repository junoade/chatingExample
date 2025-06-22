package com.example.chatserver.chat.repository;

import com.example.chatserver.chat.domain.ChatParticipant;
import com.example.chatserver.chat.domain.ChatRoom;
import com.example.chatserver.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {
    List<ChatParticipant> findByChatRoom(ChatRoom chatRoom);
    Optional<ChatParticipant> findByChatRoomAndMember(ChatRoom chatRoom, Member member);
    List<ChatParticipant> findAllByMember(Member member);

    @Query("""
        SELECT p1.chatRoom
        FROM ChatParticipant p1
            JOIN ChatParticipant p2
                ON p1.chatRoom.id = p2.chatRoom.id
        WHERE p1.member.id = :memberId AND p2.member.id = :otherMemberId
        AND p1.chatRoom.isGroupChat = 'N'
    """)
    Optional<ChatRoom> findChatRoomIdExistingPrivateRoom(@Param("memberId") Long memberId,
                                                         @Param("otherMemberId") Long otherMemberId);
}
