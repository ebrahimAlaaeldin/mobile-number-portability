package com.mnp.mobilenumberportability.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "persons")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "national_id", nullable = false, unique = true, length = 20)
    private String nationalId;

    @Column(nullable = false, length = 150)
    private String name;
}