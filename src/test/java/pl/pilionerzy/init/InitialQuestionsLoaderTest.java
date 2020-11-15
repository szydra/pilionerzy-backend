package pl.pilionerzy.init;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
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

@RunWith(MockitoJUnitRunner.class)
public class InitialQuestionsLoaderTest {

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

    @Before
    public void init() {
        questionsLoader.setEnvironment(environment);
        doReturn(resourceToLoad).when(resourceLoader).getResource(not(find("loaded")));
        doReturn(loadedResource).when(resourceLoader).getResource(find("loaded"));
        doReturn("questions.yaml").when(resourceToLoad).getFilename();
    }

    @Test
    public void shouldRunWithoutExceptionWhenFileWithQuestionsDoesNotExist() {
        doReturn(false).when(resourceToLoad).exists();

        assertThatCode(() -> questionsLoader.run()).doesNotThrowAnyException();

        verify(resourceLoader).getResource(isA(String.class));
        verifyNoInteractions(dtoMapper, questionRepository);
    }

    @Test
    public void shouldNotLoadQuestionsWhenFileWithLoadedQuestionsExists() {
        doReturn(true).when(resourceToLoad).exists();
        doReturn(true).when(loadedResource).exists();

        assertThatCode(() -> questionsLoader.run()).doesNotThrowAnyException();

        verify(resourceLoader, times(2)).getResource(isA(String.class));
        verifyNoInteractions(dtoMapper, questionRepository);
    }

    @Test
    public void shouldPrepareChangedFilename() {
        assertThat(questionsLoader.getChangedFilename("questions.yaml"))
                .isEqualTo("questions_loaded.yaml");
    }
}
