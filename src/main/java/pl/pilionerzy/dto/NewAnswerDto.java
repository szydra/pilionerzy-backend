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
    @NotNull
    private NewQuestionDto question;

    @NotNull
    private Prefix prefix;

    @NotNull
    @Size(min = 1, max = 1023)
    private String content;

    @Override
    public String toString() {
        return content;
    }
}
