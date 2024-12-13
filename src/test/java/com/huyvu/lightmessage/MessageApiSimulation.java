package com.huyvu.lightmessage;

import com.github.javafaker.Faker;
import io.gatling.javaapi.core.CoreDsl;
import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpDsl;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MessageApiSimulation extends Simulation {

    // Base URL for the API
    private static final String BASE_URL = "http://localhost:8080/api/v1";
    static Faker faker = Faker.instance(new Locale("vi")); // Faker instance with Vietnamese locale

    // HTTP Protocol Configuration
    HttpProtocolBuilder httpProtocol = HttpDsl.http
            .baseUrl(BASE_URL)
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    // Generate Authorization Token (Mock)
    private String generateAuthToken() {
        long userId = ThreadLocalRandom.current().nextLong(1, 100);
        return String.valueOf(userId); // Using userId as the token for simplicity
    }

    // Feeder with mock data

    FeederBuilder<Object> feed = getFeederData();

    @NotNull
    private static FeederBuilder<Object> getFeederData() {
        List<Map<String, Object>> list = IntStream.range(0, 1_000).parallel().mapToObj(operand -> {
            Map<String, Object> conversation = Map.of("content", faker.lorem().paragraph(), "convId", faker.number().numberBetween(2_000, 2_020));
            return conversation;
        }).collect(Collectors.toList());

        return CoreDsl.listFeeder(list).random();
    }

    // Define Send Messages Scenario
    ScenarioBuilder sendMessagesScenario = CoreDsl.scenario("Send 1000 Messages per Conversation")
            .feed(feed) // Feed data from the feeder
            .exec(
                    HttpDsl.http("Send Message")
                            .post("/messages")
                            .header("Authorization", session -> generateAuthToken())
                            .body(CoreDsl.StringBody(session -> """
                                    {
                                      "convId": "%d",
                                      "content": "%s"
                                    }
                                    """.formatted(
                                    session.getInt("convId"),
                                    session.getString("content")
                            )))
                            .check(HttpDsl.status().is(201))
            );

    {
        // Load Simulation: Define the number of users and ramp-up time
        setUp(
                sendMessagesScenario.injectOpen(
                        CoreDsl.rampUsersPerSec(10).to(400).during(5),
                        CoreDsl.constantUsersPerSec(400).during(30)
                )
        ).protocols(httpProtocol);
    }
}
