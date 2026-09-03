package com.mnp.mobilenumberportability.repository;

import com.mnp.mobilenumberportability.entity.MobileNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MobileNumberRepository extends JpaRepository<MobileNumber, Long> {

    Optional<MobileNumber> findByPhoneNumber(String phoneNumber);
}
