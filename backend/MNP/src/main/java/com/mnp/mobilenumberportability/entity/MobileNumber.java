package com.mnp.mobilenumberportability.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "mobile_numbers")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MobileNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "phone_number", nullable = false, unique = true, length = 11)
    private String phoneNumber;

    // Nullable: the porting flow never collects subscriber identity (no national ID /
    // name is submitted anywhere in the API), so this is left for future use rather
    // than populated today.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    private Person person;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "current_operator_id", nullable = false)
    private Operator currentOperator;

    // Null until the number is ported for the first time; it still belongs to
    // currentOperator by virtue of the original number range until then.
    @Column(name = "operator_since")
    private LocalDate operatorSince;

    /**
     * Materializes the row the first time a number is touched (a status lookup or a
     * porting request), starting it out with whichever operator its range belongs to.
     */
    public static MobileNumber provision(String phoneNumber, Operator originalOperator) {
        MobileNumber number = new MobileNumber();
        number.phoneNumber = phoneNumber;
        number.currentOperator = originalOperator;
        return number;
    }

    public void portTo(Operator newOperator, LocalDate portingDate) {
        this.currentOperator = newOperator;
        this.operatorSince = portingDate;
    }
}
