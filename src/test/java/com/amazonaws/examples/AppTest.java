package com.amazonaws.examples;

import com.amazonaws.examples.utils.ResourceUtils;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AppTest {

    @Test
    public void handleRequest_shouldCreateChannelFromJsonInput() {
        App function = new App();
        String input = ResourceUtils.getInstance().loadResource("/CreateEmlRtmpToEmpChannelSettings.json");
        Map<String, String> response = function.handleRequest(input, null);
        assertNotNull(response, "Response object should be created.");
        assertNotNull(response.get("id"), "Channel id should be available.");
        assertNotNull(response.get("arn"), "Channel arn should be available.");
        assertEquals("MyEML_Channel_1", response.get("name"), "Channel name must match input.");
        assertNotNull(response.get("state"), "Channel state should be available.");
    }
}
