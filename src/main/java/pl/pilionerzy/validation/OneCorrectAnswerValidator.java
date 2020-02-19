package pl.pilionerzy.validation;

import pl.pilionerzy.model.Question;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static java.lang.Boolean.TRUE;

public class OneCorrectAnswerValidator implements ConstraintValidator<OneCorrectAnswer, Question> {

    @Override
    public boolean isValid(Question question, ConstraintValidatorContext context) {
        try {
            return question.getAnswers().stream()
                    .filter(answer -> TRUE.equals(answer.getCorrect()))
                    .count() == 1;
        } catch (Exception e) {
            return false;
        }
    }
}
