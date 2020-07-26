package pl.pilionerzy.init;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.io.Files;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.pilionerzy.repository.QuestionRepository;
import pl.pilionerzy.dto.NewQuestionDto;
import pl.pilionerzy.mapping.DtoMapper;

import java.io.IOException;
import java.util.List;

/**
 * Runs at application startup and loads initial questions into the database.
 * It looks for a file named <code>questions.yaml</code> in the current directory
 * and after successful import renames it to <code>questions_loaded.yaml</code>.
 */
@Component
@Transactional
@Slf4j
@SuppressWarnings("UnstableApiUsage")
@RequiredArgsConstructor
class InitialQuestionsLoader implements CommandLineRunner {

    private final DtoMapper dtoMapper;
    private final QuestionRepository questionRepository;
    private final ResourceLoader resourceLoader;

    private Environment environment;

    @Override
    public void run(String... args) throws IOException {
        boolean testProfile = environment.acceptsProfiles(Profiles.of("test"));
        String filename = testProfile ? "classpath:questions-test.yaml" : "file:questions.yaml";
        Resource questionsYaml = resourceLoader.getResource(filename);
        if (!questionsYaml.exists()) {
            logger.info("File {} not found. No initial questions will be loaded", questionsYaml.getFilename());
        } else {
            logger.debug("File {} found", questionsYaml.getFilename());
            loadInitialQuestions(questionsYaml);
        }
    }

    private void loadInitialQuestions(Resource questionsYaml) throws IOException {
        String changedFilename = getChangedFilename(questionsYaml.getFilename());
        Resource questionsLoadedYaml = resourceLoader.getResource("file:" + changedFilename);
        if (questionsLoadedYaml.exists()) {
            logger.warn("File {} found. Initial questions will not be added", changedFilename);
        } else {
            logger.info("Initial questions from file {} will be loaded", questionsYaml.getFilename());
            List<NewQuestionDto> initialQuestions = readQuestions(questionsYaml);
            initialQuestions.stream()
                    .map(dtoMapper::mapToModel)
                    .forEach(question -> {
                        question.activate();
                        questionRepository.save(question);
                    });
            logger.info("{} initial questions saved", initialQuestions.size());
            rename(questionsYaml, questionsLoadedYaml);
        }
    }

    String getChangedFilename(String filename) {
        String nameWithoutExtension = Files.getNameWithoutExtension(filename);
        String fileExtension = Files.getFileExtension(filename);
        return nameWithoutExtension + "_loaded." + fileExtension;
    }

    private List<NewQuestionDto> readQuestions(Resource questionsYaml) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        return objectMapper.readValue(questionsYaml.getFile(), new QuestionList());
    }

    private void rename(Resource questionsYaml, Resource questionsLoadedYaml) throws IOException {
        if (environment.acceptsProfiles(Profiles.of("dev", "test"))) {
            logger.debug("File {} will not be renamed, because application is running in dev or test mode",
                    questionsYaml.getFilename());
            return;
        }
        Files.move(questionsYaml.getFile(), questionsLoadedYaml.getFile());
        logger.info("File {} renamed to {}", questionsYaml.getFilename(), questionsLoadedYaml.getFilename());
    }

    @Autowired
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    private static class QuestionList extends TypeReference<List<NewQuestionDto>> {
    }
}
