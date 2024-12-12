package com.huyvu.lightmessage;

import io.gatling.javaapi.core.CoreDsl;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpDsl;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.util.concurrent.ThreadLocalRandom;

public class MessageApiSimulation extends Simulation {

    // Base URL for the API
    private static final String BASE_URL = "http://localhost:8080/api/v1";

    // HTTP Protocol Configuration
    HttpProtocolBuilder httpProtocol = HttpDsl.http
            .baseUrl(BASE_URL) // Base URL
            .acceptHeader("application/json") // Common headers
            .contentTypeHeader("application/json");

    // Generate Authorization Token (Mock)
    private String generateAuthToken() {
        // Generate a mock Authorization token
        long userId = ThreadLocalRandom.current().nextLong(1, 100);
        return String.valueOf(userId); // Using userId as the token for simplicity
    }

    // Define Create Conversation Scenario
    ScenarioBuilder createConversationScenario = CoreDsl.scenario("Create 100 Conversations")
            .repeat(100).on(
                    CoreDsl.exec(
                            HttpDsl.http("Create Conversation")
                                    .post("/conversations")
                                    .header("Authorization", session -> generateAuthToken())
                                    .body(CoreDsl.StringBody(session -> {
                                        // Generate random participants for the conversation
                                        long user1 = ThreadLocalRandom.current().nextLong(1, 100);
                                        long user2 = ThreadLocalRandom.current().nextLong(1, 100);
                                        return """
                                        {
                                          \"conversationName\": \"Test Chat\",
                                          \"participants\": [%d, %d]
                                        }
                                        """.formatted(user1, user2);
                                    }))
                                    .check(
                                            HttpDsl.status().is(201),
                                            CoreDsl.jsonPath("$.id").saveAs("conversationId")
                                    )
                    )
            );

    // Define Send Messages Scenario
    ScenarioBuilder sendMessagesScenario = CoreDsl.scenario("Send 1000 Messages per Conversation")
            .repeat(100).on(
                    CoreDsl.exec(
                            HttpDsl.http("Fetch Conversation ID")
                                    .get("/conversations") // Assuming endpoint to fetch conversation IDs
                                    .header("Authorization", session -> generateAuthToken())
                                    .check(CoreDsl.jsonPath("$[*].id").findAll().saveAs("conversationIds"))
                    )
            )
            .exec(session -> {
                String[] conversationIds = session.getString("conversationIds").split(",");
                System.out.println("sick my duck " + conversationIds.length);
                for (String conversationId : conversationIds) {
                    session.set("conversationId", conversationId);
                    CoreDsl.repeat(1000).on(
                            CoreDsl.exec(
                                    HttpDsl.http("Send Message")
                                            .post("/messages")
                                            .header("Authorization", s -> generateAuthToken())
                                            .body(CoreDsl.StringBody(s -> """
                                            {
                                              \"convId\": \"%s\",
                                              \"content\": \"Hello, World!\"
                                            }
                                            """.formatted(s.getString("conversationId"))))
                                            .check(HttpDsl.status().is(201))
                            )
                    );
                }
                return session;
            });

    {
        // Load Simulation: Define the number of users and ramp-up time
        setUp(
                createConversationScenario.injectOpen(
                        CoreDsl.atOnceUsers(1) // Create 100 conversations with 1 user
                ),
                sendMessagesScenario.injectOpen(
                        CoreDsl.rampUsers(10).during(10) // Simulate 10 users sending messages gradually
                )
        ).protocols(httpProtocol);
    }
}