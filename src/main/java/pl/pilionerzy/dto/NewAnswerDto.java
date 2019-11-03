package pl.pilionerzy.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import pl.pilionerzy.model.Prefix;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@EqualsAndHashCode(of = {"prefix", "content"})
public class NewAnswerDto {

    @JsonBackReference
    @NotNull(message = "answer must be linked with a question")
    private NewQuestionDto question;

    @NotNull(message = "answer must have prefix")
    private Prefix prefix;

    @NotNull(message = "answer must have content")
    @Size(min = 1, max = 1023, message = "answer content length must be between 1 and 1023")
    private String content;

    @Override
    public String toString() {
        return content;
    }
}
