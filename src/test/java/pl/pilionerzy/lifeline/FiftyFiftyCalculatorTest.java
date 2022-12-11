package pl.pilionerzy.lifeline;

import org.junit.jupiter.api.Test;
import pl.pilionerzy.lifeline.model.FiftyFiftyResult;
import pl.pilionerzy.model.*;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static pl.pilionerzy.assertion.Assertions.assertThat;
import static pl.pilionerzy.model.Lifeline.FIFTY_FIFTY;

class FiftyFiftyCalculatorTest {

    private final LifelineProcessor<FiftyFiftyResult> calculator = new FiftyFiftyCalculator();

    @Test
    void shouldDiscardExactlyTwoIncorrectAnswers() {
        // given
        var game = createGame();

        // when
        var fiftyFiftyResult = calculator.process(game);

        // then
        assertThat(fiftyFiftyResult.getIncorrectPrefixes())
                .hasSize(2)
                .doesNotContain(Prefix.A);
    }

    @Test
    void shouldUpdateUsedLifelines() {
        // given
        var game = createGame();

        // when
        calculator.process(game);

        // then
        assertThat(game).hasUsedLifeline(FIFTY_FIFTY);
    }

    @Test
    void shouldSaveRejectedAnswers() {
        // given
        var game = createGame();

        // then
        var fiftyFiftyResult = calculator.process(game);

        // then
        assertThat(game.getUsedLifelines())
                .filteredOn(usedLifeline -> usedLifeline.getType() == FIFTY_FIFTY)
                .flatExtracting(UsedLifeline::getRejectedAnswers)
                .hasSameElementsAs(fiftyFiftyResult.getIncorrectPrefixes());
    }

    private Game createGame() {
        var game = new Game();
        game.setUsedLifelines(newArrayList());
        game.setLastAskedQuestion(createQuestion());
        return game;
    }

    private Question createQuestion() {
        var question = new Question();
        var answer = new Answer();
        answer.setPrefix(Prefix.A);
        answer.setCorrect(true);
        question.setAnswers(List.of(answer));
        return question;
    }
}
