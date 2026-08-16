package com.example.jenkinsdemo;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FeatureController {

    @GetMapping("/api/greeting")
    public Map<String, String> greeting(
            @RequestParam(name = "name", defaultValue = "Visitor") String name) {

        return Map.of(
                "message",
                "Hello " + name + " - this feature came from GitHub and Jenkins!"
        );
    }

    @GetMapping("/api/status")
    public Map<String, String> status() {

        Map<String, String> status = new LinkedHashMap<>();

        status.put("application", "springboot-jenkins-demo");
        status.put("status", "UP");
        status.put("version", "2.4");
        status.put("deployment", "GitHub -> Jenkins -> systemd");
        status.put("serverTime", OffsetDateTime.now().toString());

        return status;
    }

@GetMapping("/api/cicd")
public Map<String, String> cicdInfo() {

    Map<String, String> info = new LinkedHashMap<>();

    info.put("source", "GitLab");
    info.put("trigger", "Jenkins Poll SCM");
    info.put("build", "Maven");
    info.put("deployment", "Docker");
    info.put("status", "Automatic CI/CD Enabled");

    return info;
}


}
