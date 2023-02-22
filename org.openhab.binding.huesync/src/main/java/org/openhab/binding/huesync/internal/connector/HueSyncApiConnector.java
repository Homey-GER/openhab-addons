/**
 * Copyright (c) 2010-2022 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.huesync.internal.connector;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.HttpHeaders;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.openhab.binding.huesync.internal.HueSyncState;
import org.openhab.binding.huesync.internal.config.HueSyncConfiguration;
import org.openhab.core.id.InstanceUUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * The {@link HueSyncApiConnector} class makes the api-connection to the huesync-box.
 * It is also responsible for sending commands to the huesync-box.
 * *
 *
 * @author Marco Kawon - Initial Contribution
 */
public class HueSyncApiConnector {

    private final String apiUrl;
    private final HttpClient httpClient;
    private final JsonParser jsonParser = new JsonParser();

    private static final String URL_DEVICE_INFO = "/device";
    private static final String URL_EXEC_COMMAND = "/execution";
    private static final String URL_REGISTRATION = "/registrations";
    private static final int REQUEST_TIMEOUT_MS = 5000; // 5 seconds

    private ScheduledFuture<?> pollingJob;
    private Logger logger = LoggerFactory.getLogger(HueSyncApiConnector.class);
    private ScheduledExecutorService scheduler;
    private HueSyncState state;
    private HueSyncConfiguration config;

    public HueSyncApiConnector(HueSyncConfiguration config, HueSyncState state, ScheduledExecutorService scheduler,
            HttpClient httpClient) {
        this.config = config;
        this.scheduler = scheduler;
        this.state = state;
        this.apiUrl = String.format("https://%s/api/v1", config.getHost());
        this.httpClient = httpClient;
    }

    public HueSyncState getState() {
        return state;
    }

    /**
     * Set up the connection to the huesync box by starting to poll the HTTP API.
     */
    public void createConnection() {
        if (setConfigProperties()) {
            if (config.getApiAccessToken().isBlank()) {
                startPairingProcess();
            } else {
                startStatusPolling();
            }
        }
    }

    private void startStatusPolling() {
        stopPolling();

        pollingJob = scheduler.scheduleWithFixedDelay(() -> {
            updateBoxState();
        }, 0, config.httpPollingInterval, TimeUnit.SECONDS);

        logger.debug("HTTP polling started.");
    }

    private boolean isPolling() {
        return pollingJob != null && !pollingJob.isCancelled();
    }

    private void stopPolling() {
        if (isPolling()) {
            pollingJob.cancel(true);
            logger.debug("HTTP polling stopped.");
        }
    }

    /**
     * Shutdown the http client
     */
    public void dispose() {
        logger.trace("disposing connector");
        stopPolling();
    }

    protected void startPairingProcess() {
        logger.info("Creating new user on huesync-box");

        stopPolling();

        String url = apiUrl + URL_REGISTRATION;
        String command = "{\"appName\":\"openhab\",\"instanceName\":\"huesync-" + InstanceUUID.get() + "\"}";

        pollingJob = scheduler.scheduleWithFixedDelay(() -> {
            try {
                logger.debug("Sending registration command '{}' to '{}'", command, url);
                String jsonString = httpClient.newRequest(url).method(HttpMethod.POST)
                        .header(HttpHeader.CONTENT_TYPE, "application/json").timeout(2, TimeUnit.SECONDS)
                        .content(new StringContentProvider(command)).send().getContentAsString();
                JsonObject jsonObject = jsonParser.parse(jsonString).getAsJsonObject();
                logger.trace("Received response: {}", jsonObject);

                if (jsonObject.get("accessToken") != null) {
                    String accessToken = jsonObject.get("accessToken").getAsString();
                    logger.info("Pairing with huesync-box successfully finished");
                    config.setApiAccessToken(accessToken);
                    startStatusPolling();
                    return;
                } else if (jsonObject.get("code") != null) {
                    int statusCode = jsonObject.get("code").getAsInt();
                    if (statusCode == 16) {
                        logger.info(
                                "Please press the pairing button on your HueSync Box for 3 seconds until the LED blinks green!");
                    } else {
                        logger.debug("Unhandled statusCode: {}", statusCode);
                    }
                } else {
                    logger.debug("Unhandled response type: {}", jsonObject);
                }
            } catch (Exception e) {
                logger.error("Error sending registration command: {}", e.getMessage());
            }
        }, 0, 3, TimeUnit.SECONDS);
    }

    public void sendExecCommand(String command) {
        if (command == null || command.isBlank()) {
            logger.warn("Can not send empty command to huesync-box!");
            return;
        }

        String url = apiUrl + URL_EXEC_COMMAND;
        logger.debug("Sending command '{}' to '{}'", command, URL_EXEC_COMMAND);
        httpClient.newRequest(url).method(HttpMethod.PUT).header(HttpHeader.CONTENT_TYPE, "application/json")
                .timeout(REQUEST_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + config.getApiAccessToken())
                .content(new StringContentProvider(command)).send(new Response.CompleteListener() {
                    @Override
                    public void onComplete(Result result) {
                        logger.debug("Status code {}", result.getResponse().getStatus());
                        if (result.getResponse().getStatus() != 200) {
                            logger.warn("Error {} while sending command", result.getResponse().getReason());
                        }
                    }
                });

        // Update Box state immediately after sending exec-command
        updateBoxState();
    }

    private boolean setConfigProperties() {
        logger.trace("setConfigProperties(): Not implemented yet ...");

        String url = apiUrl + URL_DEVICE_INFO;
        JsonObject deviceData = getJsonResponse(url, false);

        return (deviceData != null);
    }

    private void updateBoxState() {
        logger.debug("Updating state of huesync-box ...");

        String url = apiUrl;
        JsonObject deviceData = getJsonResponse(url, true);
        logger.trace("Received deviceData response: {}", deviceData);

        if (deviceData != null) {
            try {
                // Update Box Mode
                String mode = deviceData.getAsJsonObject("execution").get("mode").getAsString();
                state.setMode(mode);
                if (deviceData.getAsJsonObject("execution").get(mode) != null) {
                    state.setIntensity(deviceData.getAsJsonObject("execution").getAsJsonObject(mode).get("intensity")
                            .getAsString());
                }

                // Update Box State
                state.setPower(deviceData.getAsJsonObject("execution").get("hdmiActive").getAsBoolean());
                state.setInput(deviceData.getAsJsonObject("execution").get("hdmiSource").getAsString());
                state.setSyncStatus(deviceData.getAsJsonObject("execution").get("syncActive").getAsBoolean());
                state.setBrightness(deviceData.getAsJsonObject("execution").get("brightness").getAsInt() / 2);

                JsonObject input1 = deviceData.getAsJsonObject("hdmi").getAsJsonObject("input1");
                state.setInput1Name(input1.get("name").getAsString());
                state.setInput1Type(input1.get("type").getAsString());
                state.setInput1Status(input1.get("status").getAsString());
                state.setInput1LastMode(input1.get("lastSyncMode").getAsString());

                JsonObject input2 = deviceData.getAsJsonObject("hdmi").getAsJsonObject("input2");
                state.setInput2Name(input2.get("name").getAsString());
                state.setInput2Type(input2.get("type").getAsString());
                state.setInput2Status(input2.get("status").getAsString());
                state.setInput2LastMode(input2.get("lastSyncMode").getAsString());

                JsonObject input3 = deviceData.getAsJsonObject("hdmi").getAsJsonObject("input3");
                state.setInput3Name(input3.get("name").getAsString());
                state.setInput3Type(input3.get("type").getAsString());
                state.setInput3Status(input3.get("status").getAsString());
                state.setInput3LastMode(input3.get("lastSyncMode").getAsString());

                JsonObject input4 = deviceData.getAsJsonObject("hdmi").getAsJsonObject("input4");
                state.setInput4Name(input4.get("name").getAsString());
                state.setInput4Type(input4.get("type").getAsString());
                state.setInput4Status(input4.get("status").getAsString());
                state.setInput4LastMode(input4.get("lastSyncMode").getAsString());

                JsonObject output = deviceData.getAsJsonObject("hdmi").getAsJsonObject("output");
                state.setOutputName(output.get("name").getAsString());
                state.setOutputType(output.get("type").getAsString());
                state.setOutputStatus(output.get("status").getAsString());
                state.setOutputLastMode(output.get("lastSyncMode").getAsString());
            } catch (Exception e) {
                stopPolling();
                state.connectionError("Internal Error while updating box state: " + e.getMessage());
            }
        }
    }

    @Nullable
    private JsonObject getJsonResponse(String uri, Boolean auth) {
        logger.trace("getJsonResponse() from '{}' with auth '{}'", uri, auth);
        try {
            Request request = httpClient.newRequest(uri);
            request.timeout(REQUEST_TIMEOUT_MS, TimeUnit.MILLISECONDS);

            if (auth) {
                request.header(HttpHeaders.AUTHORIZATION, "Bearer " + config.getApiAccessToken());
            }

            String jsonString = request.send().getContentAsString();
            JsonObject jsonObject = jsonParser.parse(jsonString).getAsJsonObject();

            logger.trace("jsonString: {}", jsonString);

            return jsonObject;
        } catch (Exception e) {
            stopPolling();
            state.connectionError("API Error: " + e.getMessage());
        }

        return null;
    }
}
