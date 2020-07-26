package pl.pilionerzy.init;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import pl.pilionerzy.repository.QuestionRepository;

import static pl.pilionerzy.assertion.Assertions.assertThat;


@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class InitialQuestionsLoaderIntegrationTest {

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void shouldLoadInitialQuestionsAndActivateThem() {
        assertThat(questionRepository.findAll())
                .hasSize(5)
                .allSatisfy(question ->
                        assertThat(question).isActive()
                );
    }
}
