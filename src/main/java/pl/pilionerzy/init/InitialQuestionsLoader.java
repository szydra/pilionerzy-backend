package pl.pilionerzy.init;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.pilionerzy.dao.QuestionDao;
import pl.pilionerzy.dto.NewQuestionDto;
import pl.pilionerzy.mapping.DtoMapper;

import java.io.IOException;
import java.util.List;

/**
 * Runs at application startup and loads initial questions into the database.
 * It looks for a file named <code>questions.json</code> in the current directory
 * and after successful import renames it to <code>questions_loaded.json</code>.
 */
@Component
@Transactional
@Slf4j
@SuppressWarnings("UnstableApiUsage")
class InitialQuestionsLoader implements CommandLineRunner {

    private DtoMapper dtoMapper;
    private Environment environment;
    private QuestionDao questionDao;
    private ResourceLoader resourceLoader;

    public InitialQuestionsLoader(DtoMapper dtoMapper, QuestionDao questionDao, ResourceLoader resourceLoader) {
        this.dtoMapper = dtoMapper;
        this.questionDao = questionDao;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void run(String... args) throws IOException {
        String filename = environment.acceptsProfiles("test") ? "classpath:questions-test.json" : "file:questions.json";
        Resource questionsJson = resourceLoader.getResource(filename);
        if (!questionsJson.exists()) {
            logger.info("File {} not found. No initial questions will be loaded", questionsJson.getFilename());
        } else {
            logger.debug("File {} found", questionsJson.getFilename());
            loadInitialQuestions(questionsJson);
        }
    }

    private void loadInitialQuestions(Resource questionsJson) throws IOException {
        String changedFilename = getChangedFilename(questionsJson.getFilename());
        Resource questionsLoadedJson = resourceLoader.getResource("file:" + changedFilename);
        if (questionsLoadedJson.exists()) {
            logger.warn("File {} found. Initial questions will not be added", changedFilename);
        } else {
            logger.info("Initial questions from file {} will be loaded", questionsJson.getFilename());
            List<NewQuestionDto> initialQuestions = readQuestions(questionsJson);
            initialQuestions.stream()
                    .map(dtoMapper::mapToModel)
                    .forEach(question -> {
                        question.activate();
                        questionDao.save(question);
                    });
            logger.info("{} initial questions saved", initialQuestions.size());
            rename(questionsJson, questionsLoadedJson);
        }
    }

    String getChangedFilename(String filename) {
        String nameWithoutExtension = Files.getNameWithoutExtension(filename);
        String fileExtension = Files.getFileExtension(filename);
        return nameWithoutExtension + "_loaded." + fileExtension;
    }

    private List<NewQuestionDto> readQuestions(Resource questionsJson) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(questionsJson.getFile(), new QuestionList());
    }

    private void rename(Resource questionsJson, Resource questionsLoadedJson) throws IOException {
        if (environment.acceptsProfiles("dev", "test")) {
            logger.debug("File {} will not be renamed, because application is running in dev or test mode",
                    questionsJson.getFilename());
            return;
        }
        Files.move(questionsJson.getFile(), questionsLoadedJson.getFile());
        logger.info("File {} renamed to {}", questionsJson.getFilename(), questionsLoadedJson.getFilename());
    }

    @Autowired
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    private static class QuestionList extends TypeReference<List<NewQuestionDto>> {
    }
}
