package se.nackademin.violofsson.spring.cruddypeople.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
// Allows Jackson to skip unknown fields (i.e. links) while deserialisng,
// allowing clients to post the same object they received with minimal fuss
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Person {
    // ID must be nullable, at least temporarily, in order to correctly
    // validate submitted persons before persisting (and subsequent ID setting)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    private String name;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Gender gender;

    @NotBlank
    private String occupation;

    @NotBlank
    private String city;

    // Synchronises date format between frontend and backend
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate birthday;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Arcana arcana;
}
