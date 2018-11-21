package pl.pilionerzy.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import pl.pilionerzy.model.Prefix;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
public class NewQuestionDto {

    @NotBlank
    private String content;

    @JsonManagedReference
    @Size(min = 4, max = 4)
    @Valid
    private List<NewAnswerDto> answers;

    @NotNull
    private Prefix correctAnswer;

    @Override
    public String toString() {
        return content;
    }

}
