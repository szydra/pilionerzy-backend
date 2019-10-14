package pl.pilionerzy.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import pl.pilionerzy.model.Prefix;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameDto {

    private Long id;

    private LocalDateTime startTime;

    private Boolean active;

    private Integer level;

    private Prefix correctAnswer;

    @Override
    public String toString() {
        return "Game with id " + id;
    }
}
