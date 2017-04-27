package org.opendolphin;

public interface RemotingConstants {

    String DOLPHIN_PLATFORM_PREFIX = "dolphin_platform_intern_";

    String CLIENT_ORIGIN = "C";

    String SERVER_ORIGIN = "S";

    String CLIENT_PM_AUTO_ID_SUFFIX = "-AUTO-CLT";

    String SERVER_PM_AUTO_ID_SUFFIX = "-AUTO-SRV";

    String SOURCE_SYSTEM = "@@@ SOURCE_SYSTEM @@@";

    String SOURCE_SYSTEM_CLIENT = "client";

    String SOURCE_SYSTEM_SERVER = "server";

   String START_LONG_POLL_COMMAND_NAME = DOLPHIN_PLATFORM_PREFIX + "longPoll";

    String INTERRUPT_LONG_POLL_COMMAND_NAME = DOLPHIN_PLATFORM_PREFIX + "release";

String ID_NAME = "id";

    String QUALIFIER_NAME = "qualifier";

    String VALUE_NAME = "value";

    String PROPERTY_NAME = "propertyName";
}
