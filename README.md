# Pilionerzy

This project provides a REST API for a counterpart of the famous game show
*Who Wants to Be a Millionaire?* It is a Spring Boot 2 based back-end application.

There is MySQL database used as the data storage.

## How to run

The application may be compiled and packaged via `mvn package` and then run as a single jar file:

```
java -jar target/pilionerzy-1.0.jar
```

A docker image can be build as well; just run, e.g.,

```
docker build -t pilionerzy-backend .
```

to create an [alpine](https://hub.docker.com/_/openjdk) based image. If you want to use MySQL on docker as well, you can
create an instance with

```
docker run -d -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=pilionerzy
  -e MYSQL_USER=pilioner -e MYSQL_PASSWORD=pilioner -p 3306:3306 mysql:8
  --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
```

## How to use

### Adding new questions

In order to add a new question one should use `/questions` endpoint with POST method. A sample request body looks like
the following:

```json
{
  "content": "Simple question",
  "correctAnswer": "A",
  "answers": [
    {
      "prefix": "A",
      "content": "Answer A"
    },
    {
      "prefix": "B",
      "content": "Answer B"
    },
    {
      "prefix": "C",
      "content": "Answer C"
    },
    {
      "prefix": "D",
      "content": "Answer D"
    }
  ]
}
```

By default a newly added question remains inactive and has to be activated manually.

### How to play

A new game is returned by the endpoint `/games/start-new` with GET method. Response body might look like:

```json
{
  "id": 1,
  "startTime": "2020-02-01T18:37:50.856",
  "active": true,
  "level": 0
}
```

Consecutive questions can be get from
`
/questions?game-id={game-id}
`
where `{game-id}` is replaced with the started game identifier. Answers are posted to the
endpoint `/games/{game-id}/answers` with the request body

```json
{
  "selected": "{selected-prefix}"
}
```

where `{selected-prefix}` can be `A`, `B`, `C` or `D`. In response one will get

```json
{
  "prefix": "{correct-prefix}"
}
```

The game is stopped either by reaching the highest level, by posting a wrong answer or, manually, by
hitting `/games/{game-id}/stop` using POST method with an empty body.

### Using lifelines

During an active game three lifelines may be used: *Ask the Audience*, *50:50* and *Phone a Friend*.
*Ask the Audience* is returned by `/games/{game-id}/ask-the-audience`; sample response can look like the following:

```json
{
  "A": "10%",
  "B": "20%",
  "C": "30%",
  "D": "40%"
}
```

Similarly, *50:50* result is returned by `/games/{game-id}/fifty-fifty`, e.g.,

```json
{
  "incorrectPrefixes": [
    "C",
    "D"
  ]
}
```

Finally, *Phone a Friend* result is returned by `/games/{game-id}/phone-a-friend`, e.g.,

```json
{
  "prefix": "A",
  "wisdom": "80%"
}
```

where `wisdom` cannot exceed 100% and should be interpreted in the following way:
*I am 80% sure that the correct answer is A*.

## Testing

During a standard build process all integration tests are skipped. They are run in `integration-test` maven profile, so
in order to execute them one has to run

```
mvn test -Pintegration-test
```
