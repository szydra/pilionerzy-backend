package pl.pilionerzy.init;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import pl.pilionerzy.mapping.DtoMapper;
import pl.pilionerzy.repository.QuestionRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.AdditionalMatchers.find;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InitialQuestionsLoaderTest {

    @Mock
    private Environment environment;

    @Mock
    private DtoMapper dtoMapper;

    @Mock
    private Resource loadedResource;

    @Mock
    private Resource resourceToLoad;

    @Mock
    private ResourceLoader resourceLoader;

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private InitialQuestionsLoader questionsLoader;

    @BeforeEach
    void init() {
        questionsLoader.setEnvironment(environment);
    }

    @Test
    void shouldRunWithoutExceptionWhenFileWithQuestionsDoesNotExist() {
        doReturn(false).when(resourceToLoad).exists();
        doReturn(resourceToLoad).when(resourceLoader).getResource(not(find("loaded")));

        assertThatCode(() -> questionsLoader.run()).doesNotThrowAnyException();

        verify(resourceLoader).getResource(isA(String.class));
        verifyNoInteractions(dtoMapper, questionRepository);
    }

    @Test
    void shouldNotLoadQuestionsWhenFileWithLoadedQuestionsExists() {
        doReturn(true).when(resourceToLoad).exists();
        doReturn(true).when(loadedResource).exists();
        doReturn(resourceToLoad).when(resourceLoader).getResource(not(find("loaded")));
        doReturn(loadedResource).when(resourceLoader).getResource(find("loaded"));
        doReturn("questions.yaml").when(resourceToLoad).getFilename();

        assertThatCode(() -> questionsLoader.run()).doesNotThrowAnyException();

        verify(resourceLoader, times(2)).getResource(isA(String.class));
        verifyNoInteractions(dtoMapper, questionRepository);
    }

    @Test
    void shouldPrepareChangedFilename() {
        assertThat(questionsLoader.getChangedFilename("questions.yaml"))
                .isEqualTo("questions_loaded.yaml");
    }
}
