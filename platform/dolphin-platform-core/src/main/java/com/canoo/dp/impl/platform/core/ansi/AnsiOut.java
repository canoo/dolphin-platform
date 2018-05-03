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
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public interface AnsiOut {

    String ANSI_RESET = "\u001B[0m";
    String ANSI_BOLD = "\u001B[1m";
    String ANSI_BLACK = "\u001B[30m";
    String ANSI_RED = "\u001B[31m";
    String ANSI_GREEN = "\u001B[32m";
    String ANSI_YELLOW = "\u001B[33m";
    String ANSI_BLUE = "\u001B[34m";
    String ANSI_PURPLE = "\u001B[35m";
    String ANSI_CYAN = "\u001B[36m";
    String ANSI_WHITE = "\u001B[37m";

    String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    String ANSI_RED_BACKGROUND = "\u001B[41m";
    String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    String ANSI_CYAN_BACKGROUND = "\u001B[46m";
    String ANSI_WHITE_BACKGROUND = "\u001B[47m";

    static void print(String message, boolean ansi) {
        new AnsiString(message).print(ansi);
    }

    class AnsiString {

        private final String content;

        public AnsiString(final String content) {
            this.content = Assert.requireNonNull(content, "content");
        }

        public void print(final boolean ansi) {
            if (ansi) {
                System.out.println(content);
            } else {
                final AnsiString ansiString = replaceAll(ANSI_BOLD, "").
                        replaceAll(ANSI_BLACK, "")
                        .replaceAll(ANSI_RED, "")
                        .replaceAll(ANSI_GREEN, "")
                        .replaceAll(ANSI_YELLOW, "")
                        .replaceAll(ANSI_BLUE, "")
                        .replaceAll(ANSI_PURPLE, "")
                        .replaceAll(ANSI_CYAN, "")
                        .replaceAll(ANSI_WHITE, "")
                        .replaceAll(ANSI_BLACK_BACKGROUND, "")
                        .replaceAll(ANSI_RED_BACKGROUND, "")
                        .replaceAll(ANSI_GREEN_BACKGROUND, "")
                        .replaceAll(ANSI_YELLOW_BACKGROUND, "")
                        .replaceAll(ANSI_BLUE_BACKGROUND, "")
                        .replaceAll(ANSI_PURPLE_BACKGROUND, "")
                        .replaceAll(ANSI_CYAN_BACKGROUND, "")
                        .replaceAll(ANSI_WHITE_BACKGROUND, "")
                        .replaceAll(ANSI_RESET, "");
                System.out.println(ansiString.getContent());
            }
        }

        public AnsiString replaceAll(final CharSequence target, final CharSequence replacement) {
            if (content.contains(target)) {
                final String m = content.replace(target, replacement);
                return new AnsiString(m);
            } else {
                return this;
            }
        }

        public String getContent() {
            return content;
        }
    }
}
