package se.nackademin.violofsson.spring.cruddypeople.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import se.nackademin.violofsson.spring.cruddypeople.domain.Arcana;
import se.nackademin.violofsson.spring.cruddypeople.domain.Gender;
import se.nackademin.violofsson.spring.cruddypeople.domain.Person;
import se.nackademin.violofsson.spring.cruddypeople.service.PeopleService;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@ControllerAdvice
public class AppController {
    private static final Logger logger = LoggerFactory.getLogger(AppController.class);
    private final PeopleService peopleService;

    @Autowired
    public AppController(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @RequestMapping("/new")
    public String add(Model model) {
        model.addAttribute("toEdit", new Person());
        // Inserting and updating are similar enough that we can reuse the page
        return "edit";
    }

    // Broadly useful values can be added to the model automatically
    @ModelAttribute
    public void addGenericAttributes(Model model) {
        model.addAttribute("allGenders", Gender.values());
        model.addAttribute("allArcana", Arcana.values());
    }

    @RequestMapping("/delete")
    public String delete(@RequestParam int id) {
        peopleService.remove(id);
        return "redirect:/";
    }

    @RequestMapping("/")
    public String displayAll(Model model) {
        model.addAttribute("people", peopleService.getAll());
        return "index";
    }

    @RequestMapping("/edit")
    public String edit(@RequestParam int id, Model model) {
        Optional<Person> person = peopleService.get(id);
        if (person.isPresent()) {
            model.addAttribute("toEdit", person.get());
            return "edit";
        } else {
            logger.error("There is no person with id " + id);
            return "error";
        }
    }

    @RequestMapping("/save")
    public String save(@Valid Person person, BindingResult result) {
        if (result.hasErrors()) {
            logger.error(result.getAllErrors().toString());
            return "error";
        }
        peopleService.save(person);
        return "redirect:/";
    }
}
