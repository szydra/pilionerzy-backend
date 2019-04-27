package pl.pilionerzy;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class PilionerzyApplicationTest {

    private static final Logger logger = LoggerFactory.getLogger("TestLogger");

    @Test
    public void contextLoads() {
        logger.info("Context loads!");
    }

}
