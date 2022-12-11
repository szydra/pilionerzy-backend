package pl.pilionerzy.mapping;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.pilionerzy.model.Answer;
import pl.pilionerzy.model.Game;
import pl.pilionerzy.model.Prefix;
import pl.pilionerzy.model.Question;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = GameMapperImpl.class)
class GameMapperTest {

    @Autowired
    private GameMapper gameMapper;

    @Test
    void shouldMapCorrectAnswer() {
        var question = new Question();
        var answer = new Answer();
        answer.setPrefix(Prefix.C);
        answer.setCorrect(true);
        question.setAnswers(List.of(answer));
        var game = new Game();
        game.setLastAskedQuestion(question);

        var gameDto = gameMapper.modelToDto(game);

        assertThat(gameDto.getCorrectAnswer()).isSameAs(Prefix.C);
    }

    @Test
    void shouldNotThrowNullPointerException() {
        var game = new Game();

        var gameDto = gameMapper.modelToDto(game);

        assertThat(gameDto.getCorrectAnswer()).isNull();
    }
}
