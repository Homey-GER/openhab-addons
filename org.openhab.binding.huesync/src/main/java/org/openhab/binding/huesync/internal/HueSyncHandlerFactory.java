/**
 * Copyright (c) 2010-2023 Contributors to the openHAB project
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

import java.util.Collections;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.openhab.binding.huesync.internal.handler.HueSyncHandler;
import org.openhab.core.io.net.http.HttpClientFactory;
import org.openhab.core.io.net.http.HttpClientInitializationException;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.binding.BaseThingHandlerFactory;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * The {@link HueSyncHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Marco Kawon - Initial contribution
 */
@NonNullByDefault
@Component(configurationPid = "binding.huesync", service = ThingHandlerFactory.class)
public class HueSyncHandlerFactory extends BaseThingHandlerFactory {

    private static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections
            .singleton(HueSyncBindingConstants.THING_TYPE_SYNCBOX);

    private final HttpClient httpClient;

    @Activate
    public HueSyncHandlerFactory(@Reference final HttpClientFactory httpClientFactory) {
        // [wip] mgb: disabled due to missing common name attributes with certs
        // this.httpClient = httpClientFactory.getCommonHttpClient();
        httpClient = new HttpClient(new SslContextFactory.Client(true));
        try {
            httpClient.start();
        } catch (Exception e) {
            throw new HttpClientInitializationException("Could not start HttpClient", e);
        }
    }

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (thingTypeUID.equals(HueSyncBindingConstants.THING_TYPE_SYNCBOX)) {
            return new HueSyncHandler(thing, httpClient);
        }

        return null;
    }
}
