package com.mnp.mobilenumberportability.repository;

import com.mnp.mobilenumberportability.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
}
