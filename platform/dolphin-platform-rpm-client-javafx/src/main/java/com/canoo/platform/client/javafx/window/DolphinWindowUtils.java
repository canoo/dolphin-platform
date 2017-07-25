/*
 * Copyright 2015-2017 Canoo Engineering AG.
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
package com.canoo.platform.client.javafx.window;

import com.canoo.platform.client.javafx.view.AbstractViewBinder;
import com.canoo.platform.core.functional.Subscription;
import com.canoo.dp.impl.platform.core.Assert;
import javafx.event.EventHandler;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

/**
 * Class that provides some helper methods.
 */
public class DolphinWindowUtils {

    /**
     * The method will register an event handler to the window that will automatically call the {@link AbstractViewBinder#destroy()}
     * method when the windows becomes hidden.
     * @param window the window
     * @param viewBinder the view binder
     * @param <M> the model type
     * @return a subscription to unsubsribe / deregister the handler.
     */
    public static <M> Subscription destroyOnClose(final Window window, final AbstractViewBinder<M> viewBinder) {
        Assert.requireNonNull(window, "window");
        Assert.requireNonNull(viewBinder, "viewBinder");
        final EventHandler<WindowEvent> handler = e -> viewBinder.destroy();
        window.addEventFilter(WindowEvent.WINDOW_HIDDEN, handler);
        return () -> window.removeEventFilter(WindowEvent.WINDOW_HIDDEN, handler);
    }

}
