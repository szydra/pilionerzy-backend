package pl.pilionerzy.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Level {

    @Id
    private Integer id;

    @NotNull(message = "level must have an award")
    @Size(min = 1, max = 15, message = "level award length must be between 1 and 15")
    private String award;

    @NotNull(message = "level must be guaranteed or not")
    private Boolean guaranteed;

    @Override
    public String toString() {
        return String.format("Level %s; award %s", id, award);
    }
}
