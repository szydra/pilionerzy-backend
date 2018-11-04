package pl.pilionerzy.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import pl.pilionerzy.model.Prefix;

import java.util.Objects;

@Getter
@Setter
public class AnswerDto {

    @JsonBackReference
    private QuestionDto question;

    private Prefix prefix;

    private String content;

    @Override
    public String toString() {
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnswerDto answerDto = (AnswerDto) o;
        return prefix == answerDto.prefix &&
                Objects.equals(content, answerDto.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prefix, content);
    }

}
