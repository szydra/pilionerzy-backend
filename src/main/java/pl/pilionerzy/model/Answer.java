package pl.pilionerzy.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"prefix", "question"})
public class Answer {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull(message = "answer must be linked with a question")
    private Question question;

    @Enumerated(STRING)
    @NotNull(message = "answer must have prefix")
    private Prefix prefix;

    @NotNull(message = "answer must have content")
    @Size(min = 1, max = 1023, message = "answer content length must be between 1 and 1023")
    private String content;

    @Override
    public String toString() {
        return content;
    }
}
