package pl.pilionerzy.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import pl.pilionerzy.model.Prefix;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

@Getter
@Setter
@EqualsAndHashCode
public class NewQuestionDto {

    @JsonProperty(access = READ_ONLY)
    private Long id;

    @NotNull(message = "question must have content")
    @Size(min = 4, max = 1023, message = "question content length must be between 4 and 1023")
    private String content;

    @JsonManagedReference
    @NotNull(message = "question must have answers")
    @Size(min = 4, max = 4, message = "question must have exactly 4 answers")
    @Valid
    private List<NewAnswerDto> answers;

    @NotNull(message = "question must have correct answer")
    private Prefix correctAnswer;

    @Override
    public String toString() {
        return content;
    }
}
