package pl.pilionerzy.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import pl.pilionerzy.model.Prefix;

@Getter
@Setter
@EqualsAndHashCode(of = {"prefix", "content"})
public class AnswerDto {

    @JsonBackReference
    private QuestionDto question;

    private Prefix prefix;

    private String content;

    @Override
    public String toString() {
        return content;
    }
}
