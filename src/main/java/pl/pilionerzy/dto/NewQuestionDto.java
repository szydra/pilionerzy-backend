package pl.pilionerzy.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import pl.pilionerzy.model.Prefix;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
public class NewQuestionDto {

    private String content;

    @JsonManagedReference
    private List<AnswerDto> answers;

    private Prefix correctAnswer;

    @Override
    public String toString() {
        return content;
    }

}
