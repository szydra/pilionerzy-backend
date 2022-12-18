package pl.pilionerzy.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import pl.pilionerzy.validation.OneCorrectAnswer;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static javax.persistence.GenerationType.IDENTITY;

/**
 * <p>An {@link Entity} class which represents a question that can be used during a game play.
 * It is asked only when marked as active. A newly created one is marked as inactive and has
 * to be activated manually.</p>
 *
 * <p>Two {@link Question} objects are consider equal when they have the same hash that
 * is calculated as a hash of the content and the content of answers.</p>
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "hash")
@OneCorrectAnswer
public class Question {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotNull(message = "question must have hash")
    @Column(unique = true, length = 32, nullable = false)
    private String hash;

    @NotNull(message = "question must have content")
    @Size(min = 4, max = 1023, message = "question content length must be between 4 and 1023")
    private String content;

    @NotNull(message = "question must have answers")
    @Size(min = 4, max = 4, message = "question must have exactly 4 answers")
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @OrderBy("prefix ASC")
    private List<Answer> answers;

    @NotNull(message = "question must be active or inactive")
    private Boolean active;

    public void activate() {
        checkState(!TRUE.equals(active), "Active question cannot be activated");
        active = true;
    }

    public void deactivate() {
        checkState(!FALSE.equals(active), "Inactive question cannot be deactivated");
        active = false;
    }

    public Answer getCorrectAnswer() {
        return answers.stream()
                .filter(answer -> TRUE.equals(answer.getCorrect()))
                .findAny()
                .orElseThrow(() -> new IllegalStateException(format("Question '%s' does not have correct answer", this)));
    }

    @Override
    public String toString() {
        return content;
    }
}
