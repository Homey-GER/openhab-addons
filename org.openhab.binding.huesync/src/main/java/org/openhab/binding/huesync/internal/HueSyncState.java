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

import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.types.State;

/**
 * Represents the state of the handled HueSync Box
 *
 * @author Marco Kawon - Initial contribution
 *
 */
public class HueSyncState {

    private State power;
    private State mode;
    private State intensity;
    private State brightness;
    private State input;
    private State syncStatus;

    private State input1Name;
    private State input1Type;
    private State input1Status;
    private State input1LastMode;

    private State input2Name;
    private State input2Type;
    private State input2Status;
    private State input2LastMode;

    private State input3Name;
    private State input3Type;
    private State input3Status;
    private State input3LastMode;

    private State input4Name;
    private State input4Type;
    private State input4Status;
    private State input4LastMode;

    private State outputName;
    private State outputType;
    private State outputStatus;
    private State outputLastMode;

    private HueSyncStateChangedListener handler;

    public HueSyncState(HueSyncStateChangedListener handler) {
        this.handler = handler;
    }

    public void connectionError(String errorMessage) {
        handler.connectionError(errorMessage);
    }

    public State getStateForChannelID(String channelID) {
        switch (channelID) {
            case HueSyncBindingConstants.CHANNEL_POWER:
                return power;
            case HueSyncBindingConstants.CHANNEL_MODE:
                return mode;
            case HueSyncBindingConstants.CHANNEL_INTENSITY:
                return intensity;
            case HueSyncBindingConstants.CHANNEL_BRIGHTNESS:
                return brightness;
            case HueSyncBindingConstants.CHANNEL_INPUT:
                return input;
            case HueSyncBindingConstants.CHANNEL_SYNCSTATUS:
                return syncStatus;

            case HueSyncBindingConstants.CHANNEL_INPUT1_NAME:
                return input1Name;
            case HueSyncBindingConstants.CHANNEL_INPUT1_TYPE:
                return input1Type;
            case HueSyncBindingConstants.CHANNEL_INPUT1_STATUS:
                return input1Status;
            case HueSyncBindingConstants.CHANNEL_INPUT1_LASTMODE:
                return input1LastMode;

            case HueSyncBindingConstants.CHANNEL_INPUT2_NAME:
                return input2Name;
            case HueSyncBindingConstants.CHANNEL_INPUT2_TYPE:
                return input2Type;
            case HueSyncBindingConstants.CHANNEL_INPUT2_STATUS:
                return input2Status;
            case HueSyncBindingConstants.CHANNEL_INPUT2_LASTMODE:
                return input2LastMode;

            case HueSyncBindingConstants.CHANNEL_INPUT3_NAME:
                return input3Name;
            case HueSyncBindingConstants.CHANNEL_INPUT3_TYPE:
                return input3Type;
            case HueSyncBindingConstants.CHANNEL_INPUT3_STATUS:
                return input3Status;
            case HueSyncBindingConstants.CHANNEL_INPUT3_LASTMODE:
                return input3LastMode;

            case HueSyncBindingConstants.CHANNEL_INPUT4_NAME:
                return input4Name;
            case HueSyncBindingConstants.CHANNEL_INPUT4_TYPE:
                return input4Type;
            case HueSyncBindingConstants.CHANNEL_INPUT4_STATUS:
                return input4Status;
            case HueSyncBindingConstants.CHANNEL_INPUT4_LASTMODE:
                return input4LastMode;

            case HueSyncBindingConstants.CHANNEL_OUTPUT_NAME:
                return outputName;
            case HueSyncBindingConstants.CHANNEL_OUTPUT_TYPE:
                return outputType;
            case HueSyncBindingConstants.CHANNEL_OUTPUT_STATUS:
                return outputStatus;
            case HueSyncBindingConstants.CHANNEL_OUTPUT_LASTMODE:
                return outputLastMode;
            default:
                return null;
        }
    }

    public void setPower(boolean power) {
        OnOffType newVal = power ? OnOffType.ON : OnOffType.OFF;
        if (newVal != this.power) {
            this.power = newVal;
            handler.stateChanged(HueSyncBindingConstants.CHANNEL_POWER, this.power);
        }
    }

    public void setMode(String mode) {
        StringType newVal = StringType.valueOf(mode);
        if (!newVal.equals(this.mode)) {
            this.mode = newVal;
            handler.stateChanged(HueSyncBindingConstants.CHANNEL_MODE, this.mode);
        }
    }

    public void setIntensity(String intensity) {
        StringType newVal = StringType.valueOf(intensity);
        if (!newVal.equals(this.intensity)) {
            this.intensity = newVal;
            handler.stateChanged(HueSyncBindingConstants.CHANNEL_INTENSITY, this.intensity);
        }
    }

    public void setBrightness(int brightness) {
        PercentType newVal = new PercentType(brightness);
        if (!newVal.equals(this.brightness)) {
            this.brightness = newVal;
            handler.stateChanged(HueSyncBindingConstants.CHANNEL_BRIGHTNESS, this.brightness);
        }
    }

    public void setInput(String input) {
        StringType newVal = StringType.valueOf(input);
        if (!newVal.equals(this.input)) {
            this.input = newVal;
            handler.stateChanged(HueSyncBindingConstants.CHANNEL_INPUT, this.input);
        }
    }

    public void setSyncStatus(boolean active) {
        OnOffType newVal = active ? OnOffType.ON : OnOffType.OFF;
        if (newVal != this.syncStatus) {
            this.syncStatus = newVal;
            handler.stateChanged(HueSyncBindingConstants.CHANNEL_SYNCSTATUS, this.syncStatus);
        }
    }

    public void setInput1Name(String name) {
        StringType newVal = StringType.valueOf(name);
        if (!newVal.equals(this.input1Name)) {
            this.input1Name = newVal;
            handler.stateChanged(HueSyncBindingConstants.CHANNEL_INPUT1_NAME, this.input1Name);
        }
    }

    public void setInput1Type(String type) {
        StringType newVal = StringType.valueOf(type);
        if (!newVal.equals(this.input1Type)) {
            this.input1Type = newVal;
            handler.stateChanged(HueSyncBindingConstants.CHANNEL_INPUT1_TYPE, this.input1Type);
        }
    }

    public void setInput1Status(String status) {
        StringType newVal = StringType.valueOf(status);
        if (!newVal.equals(this.input1Status)) {
            this.input1Status = newVal;
            handler.stateChanged(HueSyncBindingConstants.CHANNEL_INPUT1_STATUS, this.input1Status);
        }
    }

    public void setInput1LastMode(String mode) {
        StringType newVal = StringType.valueOf(mode);
        if (!newVal.equals(this.input1LastMode)) {
            this.input1LastMode = newVal;
            handler.stateChanged(HueSyncBindingConstants.CHANNEL_INPUT1_LASTMODE, this.input1LastMode);
        }
    }

    public void setInput2Name(String name) {
        StringType newVal = StringType.valueOf(name);
        if (!newVal.equals(this.input2Name)) {
            this.input2Name = newVal;
            handler.stateChanged(HueSyncBindingConstants.CHANNEL_INPUT2_NAME, this.input2Name);
        }
    }

    public void setInput2Type(String type) {
        StringType newVal = StringType.valueOf(type);
        if (!newVal.equals(this.input2Type)) {
            this.input2Type = newVal;
            handler.stateChanged(HueSyncBindingConstants.CHANNEL_INPUT2_TYPE, this.input2Type);
        }
    }

    public void setInput2Status(String status) {
        StringType newVal = StringType.valueOf(status);
        if (!newVal.equals(this.input2Status)) {
            this.input2Status = newVal;
            handler.stateChanged(HueSyncBindingConstants.CHANNEL_INPUT2_STATUS, this.input2Status);
        }
    }

    public void setInput2LastMode(String mode) {
        StringType newVal = StringType.valueOf(mode);
        if (!newVal.equals(this.input2LastMode)) {
            this.input2LastMode = newVal;
            handler.stateChanged(HueSyncBindingConstants.CHANNEL_INPUT2_LASTMODE, this.input2LastMode);
        }
    }

    public void setInput3Name(String name) {
        StringType newVal = StringType.valueOf(name);
        if (!newVal.equals(this.input3Name)) {
            this.input3Name = newVal;
            handler.stateChanged(HueSyncBindingConstants.CHANNEL_INPUT3_NAME, this.input3Name);
        }
    }

    public void setInput3Type(String type) {
        StringType newVal = StringType.valueOf(type);
        if (!newVal.equals(this.input3Type)) {
            this.input3Type = newVal;
            handler.stateChanged(HueSyncBindingConstants.CHANNEL_INPUT3_TYPE, this.input3Type);
        }
    }

    public void setInput3Status(String status) {
        StringType newVal = StringType.valueOf(status);
        if (!newVal.equals(this.input3Status)) {
            this.input3Status = newVal;
            handler.stateChanged(HueSyncBindingConstants.CHANNEL_INPUT3_STATUS, this.input3Status);
        }
    }

    public void setInput3LastMode(String mode) {
        StringType newVal = StringType.valueOf(mode);
        if (!newVal.equals(this.input3LastMode)) {
            this.input3LastMode = newVal;
            handler.stateChanged(HueSyncBindingConstants.CHANNEL_INPUT3_LASTMODE, this.input3LastMode);
        }
    }

    public void setInput4Name(String name) {
        StringType newVal = StringType.valueOf(name);
        if (!newVal.equals(this.input4Name)) {
            this.input4Name = newVal;
            handler.stateChanged(HueSyncBindingConstants.CHANNEL_INPUT4_NAME, this.input4Name);
        }
    }

    public void setInput4Type(String type) {
        StringType newVal = StringType.valueOf(type);
        if (!newVal.equals(this.input4Type)) {
            this.input4Type = newVal;
            handler.stateChanged(HueSyncBindingConstants.CHANNEL_INPUT4_TYPE, this.input4Type);
        }
    }

    public void setInput4Status(String status) {
        StringType newVal = StringType.valueOf(status);
        if (!newVal.equals(this.input4Status)) {
            this.input4Status = newVal;
            handler.stateChanged(HueSyncBindingConstants.CHANNEL_INPUT4_STATUS, this.input4Status);
        }
    }

    public void setInput4LastMode(String mode) {
        StringType newVal = StringType.valueOf(mode);
        if (!newVal.equals(this.input4LastMode)) {
            this.input4LastMode = newVal;
            handler.stateChanged(HueSyncBindingConstants.CHANNEL_INPUT4_LASTMODE, this.input4LastMode);
        }
    }

    public void setOutputName(String name) {
        StringType newVal = StringType.valueOf(name);
        if (!newVal.equals(this.outputName)) {
            this.outputName = newVal;
            handler.stateChanged(HueSyncBindingConstants.CHANNEL_OUTPUT_NAME, this.outputName);
        }
    }

    public void setOutputType(String type) {
        StringType newVal = StringType.valueOf(type);
        if (!newVal.equals(this.outputType)) {
            this.outputType = newVal;
            handler.stateChanged(HueSyncBindingConstants.CHANNEL_OUTPUT_TYPE, this.outputType);
        }
    }

    public void setOutputStatus(String status) {
        StringType newVal = StringType.valueOf(status);
        if (!newVal.equals(this.outputStatus)) {
            this.outputStatus = newVal;
            handler.stateChanged(HueSyncBindingConstants.CHANNEL_OUTPUT_STATUS, this.outputStatus);
        }
    }

    public void setOutputLastMode(String mode) {
        StringType newVal = StringType.valueOf(mode);
        if (!newVal.equals(this.outputLastMode)) {
            this.outputLastMode = newVal;
            handler.stateChanged(HueSyncBindingConstants.CHANNEL_OUTPUT_LASTMODE, this.outputLastMode);
        }
    }
}
