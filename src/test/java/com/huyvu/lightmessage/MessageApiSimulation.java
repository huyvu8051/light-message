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

    // Define Create Conversation Scenario
    ScenarioBuilder createConversationScenario = CoreDsl.scenario("Create Conversation and Send Message")
            .exec(
                    // Step 1: Create a conversation
                    HttpDsl.http("Create Conversation")
                            .post("/conversations")
                            .header("Authorization", session -> generateAuthToken()) // Add Authorization header
                            .body(CoreDsl.StringBody(session -> {
                                // Generate random participants for the conversation
                                long user1 = ThreadLocalRandom.current().nextLong(1, 100);
                                long user2 = ThreadLocalRandom.current().nextLong(1, 100);
                                return """
                                {
                                  "conversationName": "Test Chat",
                                  "participants": [%d, %d]
                                }
                                """.formatted(user1, user2);
                            }))
                            .check(
                                    HttpDsl.status().is(201), // Verify HTTP 201 Created
                                    CoreDsl.jsonPath("$.id").saveAs("conversationId") // Save conversation ID for next step
                            )
            )
            .pause(1) // Pause for 1 second before the next request
            .exec(
                    // Step 2: Send a message
                    HttpDsl.http("Send Message")
                            .post("/messages")
                            .header("Authorization", session -> generateAuthToken()) // Add Authorization header
                            .body(CoreDsl.StringBody(session -> """
                            {
                              "convId": %s,
                              "content": "Hello, World!"
                            }
                            """.formatted(session.getString("conversationId")))) // Use conversation ID from previous step
                            .check(HttpDsl.status().is(201)) // Verify HTTP 201 Created
            );

    // Generate Authorization Token (Mock)
    private String generateAuthToken() {
        // Generate a mock Authorization token
        long userId = ThreadLocalRandom.current().nextLong(1, 100);
        return String.valueOf(userId); // Using userId as the token for simplicity
    }

    {
        // Load Simulation: Define the number of users and ramp-up time
        setUp(
                createConversationScenario.injectOpen(
                        CoreDsl.rampUsers(10).during(10) // Ramp up to 10 users over 10 seconds
                )
        ).protocols(httpProtocol);
    }
}
