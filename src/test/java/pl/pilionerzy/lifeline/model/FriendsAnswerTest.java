package pl.pilionerzy.lifeline.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import pl.pilionerzy.model.Prefix;

import static org.assertj.core.api.Assertions.assertThat;

class FriendsAnswerTest {

    @Test
    void shouldWriteToJsonProperly() throws JsonProcessingException {
        FriendsAnswer friendsAnswer = new FriendsAnswer(Prefix.A, 60);

        String friendsAnswerInJson = new ObjectMapper().writeValueAsString(friendsAnswer);

        assertThat(friendsAnswerInJson).isEqualTo("{\"prefix\":\"A\",\"wisdom\":\"60%\"}");
    }
}
