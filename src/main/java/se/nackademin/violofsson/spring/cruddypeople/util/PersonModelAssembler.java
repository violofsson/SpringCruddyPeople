package se.nackademin.violofsson.spring.cruddypeople.util;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import se.nackademin.violofsson.spring.cruddypeople.controller.ApiController;
import se.nackademin.violofsson.spring.cruddypeople.domain.Person;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PersonModelAssembler implements RepresentationModelAssembler<Person, EntityModel<Person>> {
    @Override
    public EntityModel<Person> toModel(Person entity) {
        return EntityModel.of(
                entity,
                linkTo(methodOn(ApiController.class).getById(entity.getId())).withSelfRel().withType("GET"),
                linkTo(methodOn(ApiController.class).update(entity.getId(), entity)).withRel("update").withType("PUT"),
                linkTo(methodOn(ApiController.class).remove(entity.getId())).withRel("remove").withType("DELETE"),
                linkTo(methodOn(ApiController.class).create(entity)).withRel("create").withType("POST"),
                linkTo(methodOn(ApiController.class).getAll()).withRel("people").withType("GET")
        );
    }
}
