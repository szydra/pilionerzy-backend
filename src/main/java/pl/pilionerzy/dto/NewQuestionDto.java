package pl.pilionerzy.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
public class NewQuestionDto {

    private String content;

    @JsonManagedReference
    private List<AnswerDto> answers;

    private Character correctAnswer;

    @Override
    public String toString() {
        return content;
    }

}
