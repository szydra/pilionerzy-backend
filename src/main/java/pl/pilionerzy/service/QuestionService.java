package pl.pilionerzy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pilionerzy.dto.NewQuestionDto;
import pl.pilionerzy.dto.QuestionDto;
import pl.pilionerzy.exception.GameException;
import pl.pilionerzy.exception.NoSuchGameException;
import pl.pilionerzy.exception.NotEnoughDataException;
import pl.pilionerzy.mapping.DtoMapper;
import pl.pilionerzy.model.Game;
import pl.pilionerzy.model.Question;
import pl.pilionerzy.repository.QuestionRepository;
import pl.pilionerzy.util.GameUtils;
import pl.pilionerzy.util.RequestType;

import java.util.Random;

/**
 * Service that is responsible for operations on questions such as saving or drawing.
 */
@Service
@RequiredArgsConstructor
public class QuestionService {

    /**
     * The number of attempts to get another question from the database.
     */
    static final int LIMIT = 12;

    private final Random random = new Random();
    private final GameService gameService;
    private final DtoMapper mapper;
    private final QuestionRepository questionRepository;

    public NewQuestionDto saveNew(NewQuestionDto newQuestion) {
        var question = mapper.mapToModel(newQuestion);
        return mapper.mapToNewDto(questionRepository.save(question));
    }

    /**
     * Draws another question to be asked for an active game.
     *
     * @param gameId game id
     * @return next question
     * @throws NoSuchGameException    if no game with the passed id can be found
     * @throws GameException          if it is not allow to fetch another question
     * @throws NotEnoughDataException if fetching another question failed
     */
    @Transactional
    public QuestionDto getNextQuestionByGameId(Long gameId) {
        Game game = gameService.findByIdWithAskedQuestions(gameId);
        GameUtils.validate(game, RequestType.QUESTION);
        return mapper.mapToDto(getAnotherQuestion(game));
    }

    private Question getAnotherQuestion(Game game) {
        var askedQuestions = game.getAskedQuestions();
        Question question;
        int attempts = 0;
        do {
            // Prevent an infinite loop
            if (attempts++ >= LIMIT) {
                throw new NotEnoughDataException("Cannot get another question");
            }
            question = getRandomQuestion();
        } while (askedQuestions.contains(question));
        gameService.updateLastQuestion(game, question);
        return question;
    }

    private Question getRandomQuestion() {
        int numberOfActiveQuestions = questionRepository.countByActive(true);
        if (numberOfActiveQuestions == 0) {
            throw new NotEnoughDataException("No active questions available");
        }
        int page = random.nextInt(numberOfActiveQuestions);
        var questionPage = questionRepository.findByActive(true, PageRequest.of(page, 1));
        if (questionPage.hasContent()) {
            return questionPage.getContent().get(0);
        } else {
            throw new NotEnoughDataException("Cannot get another question");
        }
    }
}
