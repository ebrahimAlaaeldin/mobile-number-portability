package com.mnp.mobilenumberportability.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "operators")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Operator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String organization;

    // Inclusive bounds of the number range this operator was originally allocated
    // (e.g. Vodafone owns 01000000000-01099999999). Used to work out who a number
    // belonged to before it was ever ported.
    @Column(name = "range_start", nullable = false)
    private Long rangeStart;

    @Column(name = "range_end", nullable = false)
    private Long rangeEnd;
}
