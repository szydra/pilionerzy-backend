package pl.pilionerzy.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "businessId")
public class Question {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(unique = true, length = 32, nullable = false)
    private String businessId;

    @NotNull(message = "question must have content")
    @Size(min = 4, max = 1023, message = "question content length must be between 4 and 1023")
    private String content;

    @NotNull(message = "question must have answers")
    @Size(min = 4, max = 4, message = "question must have exactly 4 answers")
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @OrderBy("prefix ASC")
    private List<Answer> answers;

    @NotNull(message = "question must have correct answer")
    @Enumerated(STRING)
    private Prefix correctAnswer;

    @NotNull(message = "question must be active or inactive")
    private Boolean active;

    public void activate() {
        if (TRUE.equals(active)) {
            throw new IllegalStateException("Active question cannot be activated");
        }
        active = true;
    }

    public void deactivate() {
        if (FALSE.equals(active)) {
            throw new IllegalStateException("Inactive question cannot be deactivated");
        }
        active = false;
    }

    @Override
    public String toString() {
        return content;
    }
}
