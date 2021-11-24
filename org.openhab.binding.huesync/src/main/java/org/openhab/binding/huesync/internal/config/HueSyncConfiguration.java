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
package org.openhab.binding.huesync.internal.config;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Configuration class for the HueSync binding.
 *
 * @author Marco Kawon - Initial contribution
 *
 */
@NonNullByDefault
public class HueSyncConfiguration {

    /**
     * The hostname (or IP Address) of the HueSync Box
     */
    public String host = "";

    /**
     * The interval to poll the box over HTTP for state updates
     */
    public Integer httpPollingInterval = 60;

    /**
     * The api bearer token for API endpoints that require registration
     */
    public String apiAccessToken = "";

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getApiAccessToken() {
        return apiAccessToken;
    }

    public void setApiAccessToken(String apiAccessToken) {
        this.apiAccessToken = apiAccessToken;
    }
}
