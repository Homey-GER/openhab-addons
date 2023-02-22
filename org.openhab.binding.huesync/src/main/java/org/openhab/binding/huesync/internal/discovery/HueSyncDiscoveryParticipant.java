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
package org.openhab.binding.huesync.internal.discovery;

import static org.openhab.binding.huesync.internal.HueSyncBindingConstants.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jmdns.ServiceInfo;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.config.discovery.DiscoveryResult;
import org.openhab.core.config.discovery.DiscoveryResultBuilder;
import org.openhab.core.config.discovery.mdns.MDNSDiscoveryParticipant;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.ThingUID;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link HueSyncDiscoveryParticipant} is responsible for discovering
 * the remote huesync.boxes using mDNS discovery service.
 *
 * @author Marco Kawon - Initial contribution
 *
 */
@NonNullByDefault
@Component(service = MDNSDiscoveryParticipant.class, configurationPid = "mdnsdiscovery.huesync")
public class HueSyncDiscoveryParticipant implements MDNSDiscoveryParticipant {

    private static final String SERVICE_TYPE = "_huesync._tcp.local.";

    private Logger logger = LoggerFactory.getLogger(HueSyncDiscoveryParticipant.class);

    /**
     * Match the hostname + identifier of the discovered huesync-box.
     * Input is like "HueSyncBox-C4299605AAB2._huesync._tcp.local."
     */
    private static final Pattern PHILIPS_SYNCBOX_PATTERN = Pattern.compile("^(.*)-(.*)\\._huesync\\._tcp\\.local\\.$");

    @Override
    public Set<ThingTypeUID> getSupportedThingTypeUIDs() {
        return Collections.singleton(THING_TYPE_SYNCBOX);
    }

    @Override
    public String getServiceType() {
        return SERVICE_TYPE;
    }

    @Override
    public @Nullable DiscoveryResult createResult(ServiceInfo serviceInfo) {
        String qualifiedName = serviceInfo.getQualifiedName();
        logger.debug("HueSync Device found: {}", qualifiedName);

        ThingUID thingUID = getThingUID(serviceInfo);
        if (thingUID != null) {
            Matcher matcher = PHILIPS_SYNCBOX_PATTERN.matcher(qualifiedName);
            matcher.matches(); // we already know it matches, it was matched in getThingUID

            String vendor = "Philips";
            String model = "Hue Play HDMI Sync Box";
            String friendlyName = "Philips Hue HDMI Sync Box";
            String hostname = (matcher.group(1) + "-" + matcher.group(2)).toLowerCase();
            String serial = matcher.group(2);

            Map<String, Object> properties = new HashMap<>(2);
            properties.put(PARAMETER_HOST, hostname);
            properties.put(Thing.PROPERTY_SERIAL_NUMBER, serial);
            properties.put(Thing.PROPERTY_VENDOR, vendor);
            properties.put(Thing.PROPERTY_MODEL_ID, model);

            logger.debug("thing properties: {}", properties);

            return DiscoveryResultBuilder.create(thingUID).withProperties(properties)
                    .withRepresentationProperty(Thing.PROPERTY_SERIAL_NUMBER).withLabel(friendlyName).build();
        } else {
            logger.debug("This discovered device is not supported by the HueSync binding, ignoring...");
        }

        return null;
    }

    @Override
    public @Nullable ThingUID getThingUID(ServiceInfo service) {
        Matcher matcher = PHILIPS_SYNCBOX_PATTERN.matcher(service.getQualifiedName());
        if (matcher.matches()) {
            String serial = matcher.group(2).toLowerCase();
            return new ThingUID(THING_TYPE_SYNCBOX, serial);
        }
        return null;
    }
}
