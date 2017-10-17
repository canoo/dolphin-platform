/**
 * The Dolphin Platform supports different implementations for the event bus (see {@link com.canoo.platform.remoting.server.event.DolphinEventBus}). This is for example needed if the event bus should be used in a clustered or distributed environment. This intrerface provide a SPI that is loaded by the default Java SPI (see {@link java.util.ServiceLoader}) at runtime to provide a event bus implementation.
 */
package com.canoo.platform.remoting.server.event.spi;
