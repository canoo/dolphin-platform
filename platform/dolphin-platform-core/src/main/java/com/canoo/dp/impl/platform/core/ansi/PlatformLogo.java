/*
 * Copyright 2015-2018 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.canoo.dp.impl.platform.core.ansi;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.dp.impl.platform.core.PlatformConstants;
import com.canoo.dp.impl.platform.core.PlatformVersion;
import com.canoo.platform.core.PlatformConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static com.canoo.dp.impl.platform.core.ansi.AnsiOut.ANSI_BLUE;
import static com.canoo.dp.impl.platform.core.ansi.AnsiOut.ANSI_BOLD;
import static com.canoo.dp.impl.platform.core.ansi.AnsiOut.ANSI_GREEN;
import static com.canoo.dp.impl.platform.core.ansi.AnsiOut.ANSI_RED;
import static com.canoo.dp.impl.platform.core.ansi.AnsiOut.ANSI_RESET;
import static com.canoo.dp.impl.platform.core.ansi.AnsiOut.print;

/**
 * Created by hendrikebbers on 23.01.18.
 */
public class PlatformLogo {

    private final static Logger LOG = LoggerFactory.getLogger(PlatformLogo.class);

    public static void printLogo(final PlatformConfiguration configuration) {
        Assert.requireNonNull(configuration, "configuration");
        try {
            final boolean ansi = configuration.getBooleanProperty(PlatformConstants.ANSI_PROPERTY, PlatformConstants.ANSI_DEFAULT_VALUE);
            final String version = PlatformVersion.getVersion();
            final String versionString = Optional.of(version).map(v -> "Version " + version + " | ").orElse("");
            final String versionEndSuffix = "                                      ".substring(versionString.length());

            final String strokeColor = ANSI_BLUE;
            final String textColor = ANSI_RED;
            final String borderColor = ANSI_GREEN;


            final String borderStart = ANSI_BOLD + borderColor;
            final String borderEnd = ANSI_RESET;
            final String borderPipe = borderStart + "|" + borderEnd;
            final String logoStart = ANSI_BOLD + strokeColor;
            final String textStart = textColor;
            final String textEnd = ANSI_RESET;
            final String boldTextStart = ANSI_BOLD + textColor;
            final String boldTextEnd = ANSI_RESET;

            print("", ansi);
            print("  " + borderStart + "_____________________________________________________________________________________" + borderEnd, ansi);
            print("  " + borderPipe + logoStart + "   _____        _       _     _       _____  _       _    __                       " + borderPipe, ansi);
            print("  " + borderPipe + logoStart + "  |  __ \\      | |     | |   (_)     |  __ \\| |     | |  / _|                      " + borderPipe, ansi);
            print("  " + borderPipe + logoStart + "  | |  | | ___ | |_ __ | |__  _ _ __ | |__) | | __ _| |_| |_ ___  _ __ _ __ ___    " + borderPipe, ansi);
            print("  " + borderPipe + logoStart + "  | |  | |/ _ \\| | '_ \\| '_ \\| | '_ \\|  ___/| |/ _` | __|  _/ _ \\| '__| '_ ` _ \\   " + borderPipe, ansi);
            print("  " + borderPipe + logoStart + "  | |  | | (_) | | |_) | | | | | | | | |    | | (_| | |_| || (_) | |  | | | | | |  " + borderPipe, ansi);
            print("  " + borderPipe + logoStart + "  | |__| |\\___/|_| .__/|_| |_|_|_| |_|_|    |_|\\__,_|\\__|_| \\___/|_|  |_| |_| | |  " + borderPipe, ansi);
            print("  " + borderPipe + logoStart + "  |_____/        | |                                                          |_|  " + borderPipe, ansi);
            print("  " + borderPipe + logoStart + "                 |_|   " + textStart + versionString + "supported by " + textEnd + boldTextStart + "canoo.com" + boldTextEnd + versionEndSuffix + borderPipe, ansi);
            print("  " + borderPipe + borderStart + "___________________________________________________________________________________" + borderEnd + borderPipe, ansi);
            print("", ansi);
            print("", ansi);
        } catch (Exception e) {
            LOG.warn("Can not show logo", e);
        }
    }
}
