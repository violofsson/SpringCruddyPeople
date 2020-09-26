package se.nackademin.violofsson.spring.cruddypeople.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.nackademin.violofsson.spring.cruddypeople.domain.Person;
import se.nackademin.violofsson.spring.cruddypeople.service.PeopleService;
import se.nackademin.violofsson.spring.cruddypeople.util.PersonModelAssembler;

import javax.validation.Valid;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/people")
// We don't really need to specify that we're consuming/producing json
// But it clarifies the intended usage and limitations thereof
public class ApiController {
    private final PeopleService peopleService;
    private final PersonModelAssembler modelAssembler;

    @Autowired
    public ApiController(PeopleService peopleService, PersonModelAssembler modelAssembler) {
        this.peopleService = peopleService;
        this.modelAssembler = modelAssembler;
    }

    // If the json data does not specify an ID, one will be automatically generated
    // Otherwise, the server will attempt to keep the given ID, throwing an error
    // if it's already taken
    @PostMapping(value = "", consumes = "application/json")
    public ResponseEntity<?> create(@RequestBody @Valid Person p) {
        if (p.getId() != null && peopleService.idTaken(p.getId())) {
            return ResponseEntity.badRequest().body("Requested ID is already taken.");
        }
        peopleService.save(p);
        return ResponseEntity.created(linkTo(methodOn(ApiController.class).getById(p.getId())).toUri()).build();
    }

    @GetMapping(value = "", produces = "application/json")
    public ResponseEntity<CollectionModel<EntityModel<Person>>> getAll() {
        return ResponseEntity.ok(CollectionModel.of(
                peopleService.getAll().stream()
                        .map(modelAssembler::toModel)
                        .collect(Collectors.toList()),
                linkTo(methodOn(ApiController.class).getAll()).withSelfRel().withType("GET"),
                linkTo(methodOn(ApiController.class).create(new Person())).withRel("create").withType("POST"))
        );
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<EntityModel<Person>> getById(@PathVariable int id) {
        return peopleService.get(id)
                .map(person -> ResponseEntity.ok(modelAssembler.toModel(person)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> remove(@PathVariable int id) {
        return peopleService.remove(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    // If the json data doesn't include an ID, the request path ID will be used
    // If json and URI disagree, it's treated as an error
    @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody @Valid Person updated) {
        // Null check before comparison, since the JVM will attempt to cast getId() to int
        // equals() would also require a null check
        if (updated.getId() != null && id != updated.getId()) {
            return ResponseEntity.badRequest().body("Existing ID does not match updated ID.");
        }
        /*Optional<Person> potential = peopleService.get(id);
        if (potential.isPresent()) {
            Person existing = potential.get();
            existing.setName(updated.getName());
            existing.setGender(updated.getGender());
            existing.setOccupation(updated.getOccupation());
            existing.setCity(updated.getCity());
            existing.setBirthday(updated.getBirthday());
            existing.setArcana(updated.getArcana());
            return ResponseEntity.ok(personModel(peopleService.save(existing)));*/
        // The updated person is already validated and we will always replace the whole entity
        // So we might as well take a Hibernate-approved shortcut
        if (peopleService.idTaken(id)) {
            updated.setId(id);
            return ResponseEntity.ok(modelAssembler.toModel(peopleService.save(updated)));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
