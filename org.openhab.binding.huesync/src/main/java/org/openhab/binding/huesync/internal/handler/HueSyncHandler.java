/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
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
package org.openhab.binding.huesync.internal.handler;

import static org.openhab.binding.huesync.internal.HueSyncBindingConstants.*;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.client.HttpClient;
import org.openhab.binding.huesync.internal.HueSyncState;
import org.openhab.binding.huesync.internal.HueSyncStateChangedListener;
import org.openhab.binding.huesync.internal.UnsupportedCommandTypeException;
import org.openhab.binding.huesync.internal.config.HueSyncConfiguration;
import org.openhab.binding.huesync.internal.connector.HueSyncApiConnector;
import org.openhab.core.config.core.Configuration;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.openhab.core.types.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link HueSyncHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Marco Kawon - Initial contribution
 */
public class HueSyncHandler extends BaseThingHandler implements HueSyncStateChangedListener {

    private static final int RECONNECT_TIME_SECONDS = 30;

    private HttpClient httpClient;
    private HueSyncApiConnector connector;
    private HueSyncConfiguration config;
    private HueSyncState hueSyncState;
    private ScheduledFuture<?> retryJob;
    private final Logger logger = LoggerFactory.getLogger(HueSyncHandler.class);

    public HueSyncHandler(Thing thing, HttpClient httpClient) {
        super(thing);
        this.httpClient = httpClient;
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (connector == null) {
            return;
        }

        if (command instanceof RefreshType) {
            // Refreshing individual channels isn't supported by the Http connector.
            // The connector refreshes all channels together at the configured polling interval.
            return;
        }

        try {
            switch (channelUID.getId()) {
                case CHANNEL_POWER:
                    connector.sendExecCommand("{\"hdmiActive\":" + Boolean.toString(command == OnOffType.ON) + "}");
                    break;
                case CHANNEL_MODE:
                    connector.sendExecCommand("{\"mode\":\"" + command + "\"}");
                    break;
                case CHANNEL_BRIGHTNESS:
                    if (command instanceof OnOffType) {
                        connector.sendExecCommand("{\"syncActive\":" + Boolean.toString(command == OnOffType.ON) + "}");
                    } else {
                        if (hueSyncState.getStateForChannelID(CHANNEL_SYNCSTATUS) == OnOffType.OFF) {
                            connector.sendExecCommand("{\"syncActive\":true}");
                        }
                        int brightness = (int) Double.parseDouble(command.toString()) * 2;
                        connector.sendExecCommand("{\"brightness\":" + brightness + "}");
                    }
                    break;
                case CHANNEL_INTENSITY:
                    connector.sendExecCommand("{\"intensity\":\"" + command + "\"}");
                    break;
                case CHANNEL_INPUT:
                    connector.sendExecCommand("{\"hdmiSource\":\"" + command + "\"}");
                    break;
                case CHANNEL_SYNCSTATUS:
                    connector.sendExecCommand("{\"syncActive\":" + Boolean.toString(command == OnOffType.ON) + "}");
                    break;
                default:
                    throw new UnsupportedCommandTypeException();
            }
        } catch (UnsupportedCommandTypeException e) {
            logger.warn("Unsupported command {} for channel {}", command, channelUID.getId());
        }
    }

    public boolean checkConfiguration() {
        // prevent too low values for polling interval
        if (config.httpPollingInterval < 5) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                    "The polling interval should be at least 5 seconds!");
            return false;
        }
        return true;
    }

    @Override
    public void initialize() {
        logger.debug("Initializing HueSync-Box Thing handler - Thing Type: {}; Thing ID: {}.", THING_TYPE_SYNCBOX,
                this.getThing().getUID());

        cancelRetryJob();
        config = getConfigAs(HueSyncConfiguration.class);

        if (!checkConfiguration()) {
            return;
        }

        hueSyncState = new HueSyncState(this);
        updateStatus(ThingStatus.UNKNOWN);

        if (config.getApiAccessToken().isBlank()) {
            String warnMessage = "Cannot connect to huesync-box. API Access Token is not available in configuration.";
            logger.warn(warnMessage);
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, warnMessage);
        }

        createConnection();
    }

    private void createConnection() {
        logger.trace("createConnection()");
        if (connector != null) {
            connector.dispose();
        }

        connector = new HueSyncApiConnector(config, hueSyncState, scheduler, httpClient);
        connector.createConnection();
    }

    private void cancelRetryJob() {
        if (retryJob != null && !retryJob.isDone()) {
            retryJob.cancel(false);
        }
    }

    @Override
    public void dispose() {
        if (connector != null) {
            connector.dispose();
            connector = null;
        }
        cancelRetryJob();
        super.dispose();
    }

    @Override
    public void channelLinked(ChannelUID channelUID) {
        super.channelLinked(channelUID);
        String channelID = channelUID.getId();
        if (isLinked(channelID)) {
            State state = hueSyncState.getStateForChannelID(channelID);
            if (state != null) {
                updateState(channelID, state);
            }
        }
    }

    @Override
    public void stateChanged(String channelID, State state) {
        logger.debug("Received state {} for channelID {}", state, channelID);

        // Don't flood the log with thing 'updated: ONLINE' each time a single channel changed
        if (this.getThing().getStatus() != ThingStatus.ONLINE) {
            updateStatus(ThingStatus.ONLINE);
        }

        updateState(channelID, state);

        // Update Thing Configuration
        if (!config.getApiAccessToken().isBlank()) {
            if (this.getThing().getConfiguration().get(PARAMETER_API_ACCESS_TOKEN) != config.getApiAccessToken()) {
                logger.debug("API Token changed! Updating Thing Configuration ...");
                Configuration editConfig = editConfiguration();
                editConfig.put(PARAMETER_API_ACCESS_TOKEN, config.getApiAccessToken());
                updateConfiguration(editConfig);
            }
        }
    }

    @Override
    public void connectionError(String errorMessage) {
        logger.debug("huesync-box connection error: {}", errorMessage);

        if (this.getThing().getStatus() != ThingStatus.OFFLINE) {
            // Don't flood the log with thing 'updated: OFFLINE' when already offline
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, errorMessage);
        }
        connector.dispose();

        logger.debug("Trying again in {}s", RECONNECT_TIME_SECONDS);
        retryJob = scheduler.schedule(this::createConnection, RECONNECT_TIME_SECONDS, TimeUnit.SECONDS);
    }
}
