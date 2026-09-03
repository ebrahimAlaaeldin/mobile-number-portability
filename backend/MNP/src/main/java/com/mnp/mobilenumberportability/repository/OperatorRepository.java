package com.mnp.mobilenumberportability.repository;

import com.mnp.mobilenumberportability.entity.Operator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OperatorRepository extends JpaRepository<Operator, Long> {

    /** Resolves the operator identified by the mocked `organization` request header. */
    Optional<Operator> findByOrganization(String organization);

    /** Resolves which operator originally owns a number, by its allocated range. */
    @Query("select o from Operator o where :number between o.rangeStart and o.rangeEnd")
    Optional<Operator> findByRange(@Param("number") long number);
}
