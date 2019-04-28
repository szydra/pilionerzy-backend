package pl.pilionerzy.init;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import pl.pilionerzy.dao.QuestionDao;
import pl.pilionerzy.dto.NewQuestionDto;
import pl.pilionerzy.mapping.DtoMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * This class runs at application startup and loads initial questions into the database.
 * It looks for a file named <code>questions.json</code> in the current directory and after
 * successful import renames it to <code>questions_loaded.json</code>.
 */
@Component
@Transactional
@Slf4j
class InitialQuestionsLoader implements CommandLineRunner {

    private static final String QUESTIONS_JSON = "questions.json";
    private static final String QUESTIONS_JSON_LOADED = "questions_loaded.json";

    private DtoMapper dtoMapper;
    private Environment environment;
    private QuestionDao questionDao;

    private ResourceLoader resourceLoader = new FileSystemResourceLoader();

    @Autowired
    public InitialQuestionsLoader(DtoMapper dtoMapper, QuestionDao questionDao) {
        this.dtoMapper = dtoMapper;
        this.questionDao = questionDao;
    }

    @Override
    public void run(String... args) throws IOException {
        Resource questionsJson = resourceLoader.getResource(QUESTIONS_JSON);
        if (!questionsJson.exists()) {
            logger.info("File {} not found", QUESTIONS_JSON);
        } else {
            logger.info("File {} found", QUESTIONS_JSON);
            loadInitialQuestions(questionsJson);
        }
    }

    private void loadInitialQuestions(Resource questionsJson) throws IOException {
        Resource questionsLoadedJson = resourceLoader.getResource(QUESTIONS_JSON_LOADED);
        if (questionsLoadedJson.exists()) {
            logger.warn("File {} found. Initial questions will not be added", QUESTIONS_JSON_LOADED);
        } else {
            List<NewQuestionDto> initialQuestions = readQuestions(questionsJson);
            initialQuestions.stream()
                    .map(dtoMapper::mapToModel)
                    .forEach(question -> {
                        question.setActive(true);
                        questionDao.save(question);
                    });
            logger.info("{} initial questions added", initialQuestions.size());
            rename(questionsJson, questionsLoadedJson);
        }
    }

    private List<NewQuestionDto> readQuestions(Resource questionsJson) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(questionsJson.getFile(),
                new TypeReference<List<NewQuestionDto>>() {
                });
    }

    private void rename(Resource questionsJson, Resource questionsLoadedJson) throws IOException {
        if (environment.acceptsProfiles("dev", "test")) {
            return;
        }
        Files.move(questionsJson.getFile().toPath(), questionsLoadedJson.getFile().toPath());
        logger.info("File {} renamed", QUESTIONS_JSON);
    }

    @Autowired
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

}
