package pl.pilionerzy.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "businessId")
public class Game {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotNull(message = "game must have business id")
    @Column(unique = true, length = 32, nullable = false)
    private String businessId;

    @CreationTimestamp
    private LocalDateTime startTime;

    @NotNull(message = "game must be active or inactive")
    private Boolean active;

    @NotNull(message = "game must have level")
    private Integer level;

    @ManyToMany
    private Set<Question> askedQuestions;

    @ManyToOne(fetch = LAZY)
    private Question lastAskedQuestion;

    @ElementCollection
    @OrderColumn
    private List<UsedLifeline> usedLifelines;

    public void activate() {
        if (TRUE.equals(active)) {
            throw new IllegalStateException("Active game cannot be activated");
        }
        active = true;
    }

    public void deactivate() {
        if (FALSE.equals(active)) {
            throw new IllegalStateException("Inactive game cannot be deactivated");
        }
        active = false;
    }

    public void initLevel() {
        if (level != null) {
            throw new IllegalStateException("Level is already set");
        }
        level = 0;
    }

    @Override
    public String toString() {
        return "Game with id " + id;
    }
}
