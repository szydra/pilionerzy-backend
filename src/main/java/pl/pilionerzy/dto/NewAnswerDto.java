package pl.pilionerzy.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import pl.pilionerzy.model.Prefix;

import java.util.Objects;

@Getter
@Setter
public class NewAnswerDto {

    @JsonBackReference
    private NewQuestionDto question;

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
        NewAnswerDto that = (NewAnswerDto) o;
        return prefix == that.prefix &&
                Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prefix, content);
    }

}
