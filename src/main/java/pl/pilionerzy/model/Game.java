package pl.pilionerzy.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private LocalDateTime startTime;

    @NotNull
    private Boolean active;

    @NotNull
    private Integer level;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Question> askedQuestions;

    @ManyToOne
    private Question lastAskedQuestion;

    @ElementCollection
    @OrderColumn
    private List<UsedLifeline> usedLifelines;

    public void activate() {
        if (Boolean.TRUE.equals(active)) {
            throw new IllegalStateException("Active game cannot be activated");
        }
        active = true;
    }

    public void deactivate() {
        if (Boolean.FALSE.equals(active)) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return Objects.equals(id, game.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
