package com.example.chatserver.member.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // varchar2
    @Column(nullable = false, unique = true)
    private String email;
    private String password;

    @Enumerated(EnumType.STRING) // 그냥 enum 이면 상수로 들어감
    @Builder.Default // Entity 객체 생성시, Builder
    private Role role = Role.USER; // default


}
