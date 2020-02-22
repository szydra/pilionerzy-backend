package pl.pilionerzy.mapping;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import pl.pilionerzy.dto.GameDto;
import pl.pilionerzy.model.Answer;
import pl.pilionerzy.model.Game;
import pl.pilionerzy.model.Prefix;
import pl.pilionerzy.model.Question;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = GameMapperImpl.class)
public class GameMapperTest {

    @Autowired
    private GameMapper gameMapper;

    @Test
    public void shouldMapCorrectAnswer() {
        Question question = new Question();
        Answer answer = new Answer();
        answer.setPrefix(Prefix.C);
        answer.setCorrect(true);
        question.setAnswers(List.of(answer));
        Game game = new Game();
        game.setLastAskedQuestion(question);

        GameDto gameDto = gameMapper.modelToDto(game);

        assertThat(gameDto.getCorrectAnswer()).isSameAs(Prefix.C);
    }

    @Test
    public void shouldNotThrowNullPointerException() {
        Game game = new Game();

        GameDto gameDto = gameMapper.modelToDto(game);

        assertThat(gameDto.getCorrectAnswer()).isNull();
    }
}
