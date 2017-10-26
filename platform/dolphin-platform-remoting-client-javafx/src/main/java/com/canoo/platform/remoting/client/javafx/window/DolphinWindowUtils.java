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
package com.canoo.platform.remoting.client.javafx.window;

import com.canoo.platform.remoting.client.javafx.view.AbstractViewController;
import com.canoo.platform.core.functional.Subscription;
import com.canoo.dp.impl.platform.core.Assert;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

/**
 * Class that provides some helper methods.
 */
public class DolphinWindowUtils {
	
	/**
	 * A JavaFX {@link Stage} that contains the view of a {@link AbstractViewController} and will automatically call
	 * {@link AbstractViewController#destroy()} when the stage becomes hidden.
	 * @param <M> type of the model
	 */
	private static class DolphinStage<M> extends Stage {

	    /**
	     * Constructor
	     * @param viewBinder the viewBinder
	     */
	    public DolphinStage(final AbstractViewController<M> viewBinder) {
	        Assert.requireNonNull(viewBinder, "viewBinder");
	        DolphinWindowUtils.destroyOnClose(this, viewBinder);
	        setScene(new Scene(viewBinder.getParent()));
	    }
	}
	
	/**
	 * A JavaFX {@link Window} that contains the view of a {@link AbstractViewController} and will automatically call
	 * {@link AbstractViewController#destroy()} when the stage becomes hidden.
	 *
	 * @param <M> type of the model
	 */
	 private static class DolphinWindow<M> extends Window {

	    /**
	     * Constructor
	     * @param viewBinder the viewBinder
	     */
	    public DolphinWindow(final AbstractViewController<M> viewBinder) {
	        Assert.requireNonNull(viewBinder, "viewBinder");
	        DolphinWindowUtils.destroyOnClose(this, viewBinder);
	        setScene(new Scene(viewBinder.getParent()));
	    }
	    
	   
	}

    /**
     * The method will register an event handler to the window that will automatically call the {@link AbstractViewController#destroy()}
     * method when the windows becomes hidden.
     * @param window the window
     * @param viewBinder the view binder
     * @param <M> the model type
     * @return a subscription to unsubsribe / deregister the handler.
     */
    public static <M> Subscription destroyOnClose(final Window window, final AbstractViewController<M> viewBinder) {
        Assert.requireNonNull(window, "window");
        Assert.requireNonNull(viewBinder, "viewBinder");
        final EventHandler<WindowEvent> handler = e -> viewBinder.destroy();
        window.addEventFilter(WindowEvent.WINDOW_HIDDEN, handler);
        return () -> window.removeEventFilter(WindowEvent.WINDOW_HIDDEN, handler);
    }

	/**
	 * The method will create a JavaFX {@link Window} that contains the view of a {@link AbstractViewController} and will automatically call
	 * {@link AbstractViewController#destroy()} when the stage becomes hidden.
	 *
	 * @param <M> type of the model
	 */
    public static final <M> Window createWindow(AbstractViewController<M> viewBinder) {
       Assert.requireNonNull(viewBinder, "viewBinder");
	   return new DolphinWindow<M>(viewBinder);
    }
    
	/**
	 * The method will create a JavaFX {@link Stage} that contains the view of a {@link AbstractViewController} and will automatically
	 * call {@link AbstractViewController#destroy()} when the stage becomes hidden.
	 * @param <M> type of the model
	 */
    public static final <M> Stage createStage(final AbstractViewController<M> viewBinder) {
    	Assert.requireNonNull(viewBinder, "viewBinder");
    	return new DolphinStage<M>(viewBinder);
     }

}
