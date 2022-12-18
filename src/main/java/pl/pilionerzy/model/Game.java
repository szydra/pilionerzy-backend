package pl.pilionerzy.model;

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
public class Game {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @CreationTimestamp
    private LocalDateTime startTime;

    @NotNull(message = "game must be active or inactive")
    private Boolean active;

    @NotNull(message = "game must have level")
    private Integer level;

    @ManyToMany
    @JoinTable(
            name = "game_asked_question",
            joinColumns = @JoinColumn(name = "game_id", foreignKey = @ForeignKey(name = "fk_asked_question_game")),
            inverseJoinColumns = @JoinColumn(name = "question_id", foreignKey = @ForeignKey(name = "fk_asked_question"))
    )
    private Set<Question> askedQuestions;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_game_last_asked_question"))
    private Question lastAskedQuestion;

    @ElementCollection
    @CollectionTable(
            name = "used_lifeline",
            joinColumns = @JoinColumn(foreignKey = @ForeignKey(name = "fk_used_lifeline_game"))
    )
    @OrderColumn(name = "request_order")
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
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof Game)) {
            return false;
        }
        Game game = (Game) other;
        return id != null && id.equals(game.getId());
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Game with id " + id;
    }
}
