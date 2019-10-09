package pl.pilionerzy.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import pl.pilionerzy.model.converter.RejectedAnswersConverter;

import javax.persistence.Convert;
import javax.persistence.Embeddable;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.util.Collection;

import static javax.persistence.EnumType.STRING;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode(of = {"type", "question"})
public class UsedLifeline {

    @NotNull(message = "lifeline must have type")
    @Enumerated(STRING)
    private Lifeline type;

    @NotNull(message = "lifeline must be linked with a question")
    @ManyToOne
    private Question question;

    @Convert(converter = RejectedAnswersConverter.class)
    private Collection<Prefix> rejectedAnswers;

}
