package pl.pilionerzy.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class AnswerDto {

    @JsonBackReference
    private NewQuestionDto question;

    private Character prefix;

    private String content;

    @Override
    public String toString() {
        return content;
    }

}
