package pl.pilionerzy.model;

import lombok.Getter;
import lombok.Setter;
import pl.pilionerzy.model.converter.RejectedAnswersConverter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class UsedLifeline {

    @NotNull
    @Enumerated(EnumType.STRING)
    private Lifeline type;

    @NotNull
    @ManyToOne
    private Question question;

    @Convert(converter = RejectedAnswersConverter.class)
    private Collection<Prefix> rejectedAnswers;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsedLifeline that = (UsedLifeline) o;
        return type == that.type &&
                Objects.equals(question, that.question);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, question);
    }
}
