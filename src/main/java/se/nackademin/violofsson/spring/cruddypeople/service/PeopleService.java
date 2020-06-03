package se.nackademin.violofsson.spring.cruddypeople.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.nackademin.violofsson.spring.cruddypeople.domain.Person;
import se.nackademin.violofsson.spring.cruddypeople.repository.PersonRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PeopleService {
    private final PersonRepository repository;

    @Autowired
    public PeopleService(PersonRepository repository) {
        this.repository = repository;
    }

    public Optional<Person> get(int id) {
        return repository.findById(id);
    }

    public List<Person> getAll() {
        return repository.findAll();
    }

    public boolean idTaken(Integer id) {
        return repository.existsById(id);
    }

    // Returns a Collection-style boolean for whether the deleted person existed
    @Transactional
    public boolean remove(int id) {
        if (idTaken(id)) {
            repository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public Person save(Person p) {
        return repository.save(p);
    }
}
