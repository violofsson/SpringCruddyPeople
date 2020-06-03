package se.nackademin.violofsson.spring.cruddypeople.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.nackademin.violofsson.spring.cruddypeople.domain.Person;

public interface PersonRepository extends JpaRepository<Person, Integer> {
    // Quicker and more idiomatic than findById(id).isPresent()
    boolean existsById(Integer id);
}
