package pl.pilionerzy.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
public class GameDto {

    private Long id;

    private LocalDateTime startTime;

    private Boolean active;

    private Integer level;

    @Override
    public String toString() {
        return "Game with id " + id;
    }

}
