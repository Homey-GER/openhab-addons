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
package org.openhab.binding.huesync.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.thing.ThingTypeUID;

/**
 * The {@link HueSyncBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Marco Kawon - Initial contribution
 */
@NonNullByDefault
public class HueSyncBindingConstants {

    public static final String BINDING_ID = "huesync";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_SYNCBOX = new ThingTypeUID(BINDING_ID, "box");

    // List of thing parameters
    public static final String PARAMETER_HOST = "host";
    public static final String PARAMETER_POLLING_INTERVAL = "httpPollingInterval";
    public static final String PARAMETER_API_ACCESS_TOKEN = "apiAccessToken";

    // List of all basic channels
    public static final String CHANNEL_POWER = "power";
    public static final String CHANNEL_MODE = "mode";
    public static final String CHANNEL_INTENSITY = "intensity";
    public static final String CHANNEL_BRIGHTNESS = "brightness";
    public static final String CHANNEL_INPUT = "input";
    public static final String CHANNEL_SYNCSTATUS = "syncStatus";

    // List of all advanced i/o channels
    public static final String CHANNEL_OUTPUT_NAME = "outputName";
    public static final String CHANNEL_OUTPUT_TYPE = "outputType";
    public static final String CHANNEL_OUTPUT_STATUS = "outputStatus";
    public static final String CHANNEL_OUTPUT_LASTMODE = "outputLastMode";

    public static final String CHANNEL_INPUT1_NAME = "input1Name";
    public static final String CHANNEL_INPUT1_TYPE = "input1Type";
    public static final String CHANNEL_INPUT1_STATUS = "input1tatus";
    public static final String CHANNEL_INPUT1_LASTMODE = "input1LastMode";

    public static final String CHANNEL_INPUT2_NAME = "input2Name";
    public static final String CHANNEL_INPUT2_TYPE = "input2Type";
    public static final String CHANNEL_INPUT2_STATUS = "input2Status";
    public static final String CHANNEL_INPUT2_LASTMODE = "input2LastMode";

    public static final String CHANNEL_INPUT3_NAME = "input3Name";
    public static final String CHANNEL_INPUT3_TYPE = "input3Type";
    public static final String CHANNEL_INPUT3_STATUS = "input3Status";
    public static final String CHANNEL_INPUT3_LASTMODE = "input3LastMode";

    public static final String CHANNEL_INPUT4_NAME = "input4Name";
    public static final String CHANNEL_INPUT4_TYPE = "input4Type";
    public static final String CHANNEL_INPUT4_STATUS = "input4Status";
    public static final String CHANNEL_INPUT4_LASTMODE = "input4LastMode";
}
