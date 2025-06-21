package com.example.chatserver.chat.service;

import com.example.chatserver.chat.domain.ChatMessage;
import com.example.chatserver.chat.domain.ChatParticipant;
import com.example.chatserver.chat.domain.ChatRoom;
import com.example.chatserver.chat.domain.ReadStatus;
import com.example.chatserver.chat.dto.ChatMessageDto;
import com.example.chatserver.chat.dto.ChatRoomListResDto;
import com.example.chatserver.chat.dto.MyChatListResDto;
import com.example.chatserver.chat.repository.ChatMessageRepository;
import com.example.chatserver.chat.repository.ChatParticipantRepository;
import com.example.chatserver.chat.repository.ChatRoomRepository;
import com.example.chatserver.chat.repository.ReadStatusRepository;
import com.example.chatserver.member.domain.Member;
import com.example.chatserver.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MemberRepository memberRepository;

    public ChatService(ChatRoomRepository chatRoomRepository, ChatParticipantRepository chatParticipantRepository, ChatMessageRepository chatMessageRepository, ReadStatusRepository readStatusRepository, MemberRepository memberRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatParticipantRepository = chatParticipantRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.readStatusRepository = readStatusRepository;
        this.memberRepository = memberRepository;
    }

    /**
     * 1) 채팅방 조회
     * 2) 보낸사람 조회
     * 3) 메시지 저장
     * 4) 사용자별 읽음 여부 저장
     * @param roomId
     * @param dto
     */
    public void saveMessage(Long roomId, ChatMessageDto dto) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("해당 채팅방이 없거나 잘못된 채팅방입니다."));
        Member sender = memberRepository.findByEmail(dto.getSenderEmail()).orElseThrow(() -> new EntityNotFoundException("해당 회원 이메일이 없거나 유효하지 않습니다."));

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .member(sender)
                .content(dto.getMessage())
                .build();
        chatMessageRepository.save(chatMessage);

        // TODO - 개선포인트; 메시지 하나가 만들어지면 N명의 참여자들에 대해 ReadStatus 테이블의 행을 insert 해줘야함
        List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);
        for (ChatParticipant c : chatParticipants) {
            ReadStatus readStatus = ReadStatus.builder()
                    .chatRoom(chatRoom)
                    .member(c.getMember())
                    .chatMessage(chatMessage)
                    .isRead(c.getMember().equals(sender))
                    .build();
            readStatusRepository.save(readStatus);
        }


    }

    public void createGroupRoom(String roomName) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(userEmail).orElseThrow(() -> new EntityNotFoundException("Email 주소를 확인하세요."));

        ChatRoom chatRoom = ChatRoom.builder()
                .name(roomName)
                .isGroupChat("Y")
                .build();
        chatRoomRepository.save(chatRoom);

        ChatParticipant chatParticipant = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .member(member)
                .build();
        chatParticipantRepository.save(chatParticipant);
    }

    public List<ChatRoomListResDto> getGroupChatRooms() {
        List<ChatRoom> chatRooms = chatRoomRepository.findByIsGroupChat("Y");
        List<ChatRoomListResDto> resDtoList = new ArrayList<>();
        for (ChatRoom chatRoom : chatRooms) {
            ChatRoomListResDto resDto = ChatRoomListResDto.builder()
                    .roomId(chatRoom.getId())
                    .roomName(chatRoom.getName())
                    .build();
            resDtoList.add(resDto);
        }

        return resDtoList;
    }

    public void joinGroupChat(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("지정된 채팅방이 없습니다."));

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(userEmail).orElseThrow(() -> new EntityNotFoundException("Email 주소를 확인하세요."));

        // 기존 채팅방 참여 여부 확인
        Optional<ChatParticipant> participant = chatParticipantRepository.findByChatRoomAndMember(chatRoom, member);
        if(!participant.isPresent()) {
            addParticipant(chatRoom, member);
        }

    }


    private void addParticipant(ChatRoom chatRoom, Member member) {
        ChatParticipant chatParticipant = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .member(member)
                .build();
        chatParticipantRepository.save(chatParticipant);
    }

    public List<ChatMessageDto> getChatHistory(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("지정된 채팅방이 없습니다."));

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(userEmail).orElseThrow(() -> new EntityNotFoundException("Email 주소를 확인하세요."));

        List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);

        boolean hasMember = false;
        for (ChatParticipant c : chatParticipants) {
            if (c.getMember().equals(member)) {
                hasMember = true;
                break;
            }
        }

        if(!hasMember) {
            throw new IllegalArgumentException("채팅방 접근 권한이 없습니다");
        }

        List<ChatMessage> chatMessages = chatMessageRepository.findByChatRoomOrderByCreateTimeAsc(chatRoom);
        List<ChatMessageDto> chatMessageDtos = new ArrayList<>();
        for (ChatMessage message : chatMessages) {
            ChatMessageDto dto = ChatMessageDto.builder()
                    .message(message.getContent())
                    .senderEmail(message.getMember().getEmail())
                    .build();
            chatMessageDtos.add(dto);
        }

        return chatMessageDtos;
    }

    public boolean isRoomParticipant(String email, Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("지정된 채팅방이 없습니다."));
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("Email 주소를 확인하세요."));

        List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);
        for(ChatParticipant c : chatParticipants) {
            if(c.getMember().equals(member)) {
                return true;
            }
        }
        return false;
    }


    public void messageRead(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("지정된 채팅방이 없습니다."));

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(userEmail).orElseThrow(() -> new EntityNotFoundException("Email 주소를 확인하세요."));

        List<ReadStatus> readStatuses = readStatusRepository.findByChatRoomAndMember(chatRoom, member);
        for(ReadStatus r : readStatuses) {
            r.updateIsRead(true); // dirty checking
        }
    }

    public List<MyChatListResDto> getMyChatRooms() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(userEmail).orElseThrow(() -> new EntityNotFoundException("Email 주소를 확인하세요."));
        // 요청자가 참여중인 모든 활성화된 채팅방
        List<ChatParticipant> chatParticipants = chatParticipantRepository.findAllByMember(member);
        List<MyChatListResDto> dtos = new ArrayList<>();
        for(ChatParticipant c : chatParticipants) {
            Long count = readStatusRepository.countByChatRoomAndMemberAndIsReadFalse(c.getChatRoom(), member);
            MyChatListResDto dto = MyChatListResDto.builder()
                    .roomId(c.getChatRoom().getId())
                    .roomName(c.getChatRoom().getName())
                    .isGroupChat(c.getChatRoom().getIsGroupChat())
                    .unReadCount(count)
                    .build();
            dtos.add(dto);
        }

        return dtos;
    }

    public void leaveGroupChatRoom(Long roomId) {
        // 채팅방 참여자에선 삭제, 모든 참여자가 나간 경우 Room, Message도 삭제
        // c.f) 특정 컬럼만 update 등
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("지정된 채팅방이 없습니다."));
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(userEmail).orElseThrow(() -> new EntityNotFoundException("Email 주소를 확인하세요."));

        if (chatRoom.getIsGroupChat().equals("N")) {
            throw new IllegalArgumentException("그룹채팅방이 아닙니다.");
        }

        ChatParticipant chatParticipant = chatParticipantRepository.findByChatRoomAndMember(chatRoom, member)
                .orElseThrow(() -> new EntityNotFoundException("참여자를 찾을 수 없습니다."));

        chatParticipantRepository.delete(chatParticipant);

        List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);
        if (chatParticipants.isEmpty()) {
            // CASCADE DELETE
            // ChatRoom -> {ChatParticipant, ChatMessage -> {ReadStatus}}
            chatRoomRepository.delete(chatRoom);
        }

    }
}




