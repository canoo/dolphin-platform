package com.canoo.platform.remoting.server.event;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.dp.impl.server.event.ClientSessionEventFilter;
import com.canoo.dp.impl.server.event.HttpSessionEventFilter;
import com.canoo.platform.server.client.ClientSession;
import org.apiguardian.api.API;

import javax.servlet.http.HttpSession;
import java.io.Serializable;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * A factory that provides some common implementations for the {@link EventFilter}.
 *
 * @author Hendrik Ebbers
 */
@API(since = "1.0.0.RC1", status = EXPERIMENTAL)
public class EventFilterFactory {

    public static EventFilter allowSessions(final ClientSession clientSession) {
        Assert.requireNonNull(clientSession, "clientSession");
        return allowClientSessions(clientSession.getId());
    }

    public static EventFilter allowHttpSessions(final ClientSession clientSession) {
        Assert.requireNonNull(clientSession, "clientSession");
        Assert.requireNonNull(clientSession.getHttpSession(), "httpSession");
        return allowHttpSessions(clientSession.getHttpSession().getId());
    }

    public static EventFilter allowSessions(final HttpSession httpSession) {
        Assert.requireNonNull(httpSession, "httpSession");
        return allowHttpSessions(httpSession.getId());
    }

    /**
     * Creates a {@link EventFilter} that allows subscriptions for all client sessions (see {@link com.canoo.platform.server.client.ClientSession}) of the given session ids
     * @param clientSessionIds the ids for all allowed client sessions
     * @return the filter
     */
    public static EventFilter allowClientSessions(final String... clientSessionIds) {
        return new ClientSessionEventFilter(clientSessionIds);
    }

    public static EventFilter allowHttpSessions(final String... httpSessionIds) {
        return new HttpSessionEventFilter(httpSessionIds);
    }

    /**
     * Creates a {@link EventFilter} that blocks subscriptions for all client sessions (see {@link com.canoo.platform.server.client.ClientSession}) of the given session ids
     * @param clientSessionIds the ids for all blocked client sessions
     * @return the filter
     */
    public static EventFilter excludeClientSessions(final String... clientSessionIds) {
        return not(allowClientSessions(clientSessionIds));
    }

    public static EventFilter excludeHttpSessions(final String... httpSessionIds) {
        return not(allowHttpSessions(httpSessionIds));
    }

    /**
     * Returns a filter that negates the result of the given filter
     * @param filter the given filter
     * @return the filter
     */
    public static <T extends Serializable> EventFilter<T> not(final EventFilter<T> filter) {
        Assert.requireNonNull(filter, "filter");
        return new EventFilter<T>() {

            @Override
            public boolean test(final MessageEventContext<T> context) {
                return !filter.test(context);
            }
        };
    }

    /**
     * Returns a filter that only allow a client session if it is allowed by all given filters.
     * @param filters given filters
     * @return the filter
     */
    public static <T extends Serializable> EventFilter<T> and(final EventFilter<T>... filters) {
        return new EventFilter<T>() {
            @Override
            public boolean test(final MessageEventContext<T> context) {
                for(EventFilter filter : filters) {
                    if(!filter.test(context)) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    /**
     * Returns a filter that only allow a client session if it is allowed by at least one of the given filters.
     * @param filters given filters
     * @return the filter
     */
    public static <T extends Serializable> EventFilter<T> or(final EventFilter<T>... filters) {
        return new EventFilter<T>() {
            @Override
            public boolean test(final MessageEventContext<T> context) {
                for(EventFilter filter : filters) {
                    if(filter.test(context)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

}
