package pl.pilionerzy.init;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import pl.pilionerzy.dao.QuestionDao;
import pl.pilionerzy.model.Question;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * This class runs at application startup and loads initial questions into the database.
 * It looks for a file named <code>questions.json</code> in the current directory and after
 * successful import renames it to <code>questions_loaded.json</code>.
 */
@Component
class InitialQuestionsLoader implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitialQuestionsLoader.class);

    private static final String QUESTIONS_JSON = "questions.json";

    private static final String QUESTIONS_JSON_LOADED = "questions_loaded.json";

    private QuestionDao questionDao;

    private ResourceLoader resourceLoader = new FileSystemResourceLoader();

    @Autowired
    public InitialQuestionsLoader(QuestionDao questionDao) {
        this.questionDao = questionDao;
    }

    @Override
    public void run(String... args) throws IOException {
        Resource questionsJson = resourceLoader.getResource(QUESTIONS_JSON);
        if (!questionsJson.exists()) {
            LOGGER.info("File {} not found", QUESTIONS_JSON);
        } else {
            LOGGER.info("File {} found", QUESTIONS_JSON);
            loadInitialQuestions(questionsJson);
        }
    }

    private void loadInitialQuestions(Resource questionsJson) throws IOException {
        Resource questionsLoadedJson = resourceLoader.getResource(QUESTIONS_JSON_LOADED);
        if (questionsLoadedJson.exists()) {
            LOGGER.warn("File {} found. Initial questions will not be added", QUESTIONS_JSON_LOADED);
        } else {
            List<Question> initialQuestions = readQuestions(questionsJson);
            questionDao.saveAll(initialQuestions);
            LOGGER.info("{} initial questions added", initialQuestions.size());
            Files.move(questionsJson.getFile().toPath(), questionsLoadedJson.getFile().toPath());
            LOGGER.info("File {} renamed successfully", QUESTIONS_JSON);
        }
    }

    private List<Question> readQuestions(Resource questionsJson) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(questionsJson.getFile(),
                new TypeReference<List<Question>>() {
                });
    }

}
