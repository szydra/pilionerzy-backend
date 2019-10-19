package pl.pilionerzy.service;

import org.springframework.stereotype.Component;

import static java.util.UUID.randomUUID;

@Component
class GameIdGenerator {

    String generate() {
        return randomUUID().toString().replace("-", "");
    }
}
