package pl.pilionerzy.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
public class QuestionDto {

    private String content;

    @JsonManagedReference
    private List<AnswerDto> answers;

    @Override
    public String toString() {
        return content;
    }
}
