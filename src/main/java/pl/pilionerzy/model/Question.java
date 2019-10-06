package pl.pilionerzy.model;

import org.hibernate.validator.constraints.Length;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Length(min = 4, max = 1023, message = "question content length must be between 4 and 1023")
    private String content;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Size(min = 4, max = 4)
    @OrderBy("prefix ASC")
    private List<Answer> answers;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Prefix correctAnswer;

    @NotNull
    private Boolean active;

    public void activate() {
        if (Boolean.TRUE.equals(active)) {
            throw new IllegalStateException("Active question cannot be activated");
        }
        active = true;
    }

    @Override
    public String toString() {
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Question question = (Question) o;
        return Objects.equals(id, question.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
