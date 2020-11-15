package pl.pilionerzy.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import pl.pilionerzy.model.converter.RejectedAnswersConverter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode(of = "type")
public class UsedLifeline {

    @NotNull(message = "lifeline must have type")
    @Enumerated(STRING)
    @Column(name = "lifeline_type")
    private Lifeline type;

    @NotNull(message = "lifeline must be linked with a question")
    @ManyToOne(fetch = LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_used_lifeline_question"))
    private Question question;

    @Convert(converter = RejectedAnswersConverter.class)
    private Collection<Prefix> rejectedAnswers;
}
