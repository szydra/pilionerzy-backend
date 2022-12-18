package pl.pilionerzy.init;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.pilionerzy.repository.QuestionRepository;

import static pl.pilionerzy.assertion.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class InitialQuestionsLoaderIntegrationTest {

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    void shouldLoadInitialQuestionsAndActivateThem() {
        assertThat(questionRepository.findAll())
                .hasSize(5)
                .allSatisfy(question ->
                        assertThat(question).isActive()
                );
    }
}
