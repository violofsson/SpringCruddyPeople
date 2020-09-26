package se.nackademin.violofsson.spring.cruddypeople.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import se.nackademin.violofsson.spring.cruddypeople.domain.Person;
import se.nackademin.violofsson.spring.cruddypeople.service.PeopleService;
import se.nackademin.violofsson.spring.cruddypeople.util.PersonModelAssembler;

import javax.validation.Valid;

@RestController
// We don't really need to specify that we're consuming/producing json
// But it clarifies the intended usage and limitations thereof
@RequestMapping(value = "/api/people", consumes = "/application/json", produces = "application/json")
@Validated
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
    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid Person p) {
        if (p.getId() != null && peopleService.idTaken(p.getId())) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                    .body(Problem.create()
                            .withTitle("ID Conflict")
                            .withDetail("The requested ID is already taken.")
                    );
        }
        EntityModel<Person> entityModel = modelAssembler.toModel(peopleService.save(p));
        return ResponseEntity.created(
                entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()
        ).body(entityModel);
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Person>>> getAll() {
        return ResponseEntity.ok(modelAssembler.toCollectionModel(peopleService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Person>> getById(@PathVariable int id) {
        return ResponseEntity.of(peopleService.get(id).map(modelAssembler::toModel));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable int id) {
        return peopleService.remove(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    // If the json data doesn't include an ID, the request path ID will be used
    // If json and URI disagree, it's treated as an error
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody @Valid Person updated) {
        // Null check before comparison, since the JVM will attempt to cast
        // getId() to int. equals() would also require a null check
        if (updated.getId() != null && id != updated.getId()) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                    .body(Problem.create()
                            .withTitle("ID Conflict")
                            .withDetail("Path ID does not match entity ID.")
                    );
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
        // The updated person is already validated and we will always replace
        // the whole entity, so we might as well take a Hibernate-approved
        // shortcut
        if (peopleService.idTaken(id)) {
            updated.setId(id);
            return ResponseEntity.ok(modelAssembler.toModel(peopleService.save(updated)));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
