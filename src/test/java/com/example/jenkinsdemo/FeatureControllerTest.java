package com.example.jenkinsdemo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;

class FeatureControllerTest {

    @Test
    void greetingShouldContainProvidedName() {

        FeatureController controller = new FeatureController();

        Map<String, String> response =
                controller.greeting("Nematullah");

        assertEquals(
                "Hello Nematullah - this feature came from GitHub and Jenkins!",
                response.get("message")
        );
    }

    @Test
    void applicationStatusShouldBeUp() {

        FeatureController controller = new FeatureController();

        Map<String, String> response =
                controller.status();

        assertEquals("UP", response.get("status"));
        assertEquals("2.5", response.get("version"));
    }
}
