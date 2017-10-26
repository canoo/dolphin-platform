package com.canoo.dp.impl.platform.client.http;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.platform.client.ClientConfiguration;
import com.canoo.platform.core.http.ByteArrayProvider;
import com.canoo.platform.core.http.HttpExecutor;
import com.canoo.platform.core.http.HttpResponse;
import com.canoo.platform.core.http.HttpURLConnectionHandler;
import com.canoo.platform.core.DolphinRuntimeException;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.canoo.dp.impl.platform.core.PlatformConstants.ACCEPT_CHARSET_HEADER;
import static com.canoo.dp.impl.platform.core.PlatformConstants.ACCEPT_HEADER;
import static com.canoo.dp.impl.platform.core.PlatformConstants.CHARSET;
import static com.canoo.dp.impl.platform.core.PlatformConstants.JSON_MIME_TYPE;

public class HttpResponseImpl implements HttpResponse {

    private final HttpURLConnection connection;

    private final Gson gson;

    private final ByteArrayProvider dataProvider;

    private AtomicBoolean handled = new AtomicBoolean(false);

    private final List<HttpURLConnectionHandler> responseHandlers;

    private final ClientConfiguration configuration;

    public HttpResponseImpl(final HttpURLConnection connection, final Gson gson, final ByteArrayProvider dataProvider, final List<HttpURLConnectionHandler> responseHandlers, ClientConfiguration configuration) {
        this.connection = Assert.requireNonNull(connection, "connection");
        this.gson = Assert.requireNonNull(gson, "gson");
        this.dataProvider = Assert.requireNonNull(dataProvider, "dataProvider");
        this.configuration = configuration;

        Assert.requireNonNull(responseHandlers, "responseHandlers");
        this.responseHandlers = Collections.unmodifiableList(responseHandlers);
    }

    @Override
    public HttpExecutor<ByteArrayProvider> readBytes() {
        return new HttpExecutorImpl<>(configuration, new HttpProvider<ByteArrayProvider>() {
            @Override
            public ByteArrayProvider get() throws IOException{
                final byte[] bytes = readBytesImpl();
                return new SimpleByteArrayProvider(bytes);
            }
        });
    }

    @Override
    public HttpExecutor<ByteArrayProvider> readBytes(final String contentType) {
        connection.setRequestProperty(ACCEPT_HEADER, contentType);
        return readBytes();
    }

    @Override
    public HttpExecutor<String> readString() {
        connection.setRequestProperty(ACCEPT_CHARSET_HEADER, CHARSET);
        return new HttpExecutorImpl<>(configuration, new HttpProvider<String>() {
            @Override
            public String get() throws IOException{
                return new String(readBytesImpl(), CHARSET);
            }
        });
    }

    @Override
    public HttpExecutor<String> readString(final String contentType) {
        connection.setRequestProperty(ACCEPT_HEADER, contentType);
        return readString();
    }

    @Override
    public <R> HttpExecutor<R> readObject(final Class<R> responseType) {
        connection.setRequestProperty(ACCEPT_CHARSET_HEADER, CHARSET);
        connection.setRequestProperty(ACCEPT_HEADER, JSON_MIME_TYPE);

        return new HttpExecutorImpl<>(configuration, new HttpProvider<R>() {
            @Override
            public R get() throws IOException{
                return gson.fromJson(new String(readBytesImpl(), CHARSET), responseType);
            }
        });
    }

    @Override
    public HttpExecutor<Void> withoutResult() {
        return new HttpExecutorImpl<>(configuration, new HttpProvider<Void>() {
            @Override
            public Void get() throws IOException{
                withoutResultImpl();
                return null;
            }
        });
    }

    private byte[] readBytesImpl() throws IOException {
        connection.setDoInput(true);
        withoutResultImpl();
        final InputStream is = connection.getInputStream();
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int read = is.read();
        while (read != -1) {
            byteArrayOutputStream.write(read);
            read = is.read();
        }
        return byteArrayOutputStream.toByteArray();
    }

    private void withoutResultImpl() throws IOException {
        if(handled.get()) {
            throw new DolphinRuntimeException("Http call already handled");
        }
        handled.set(true);
        final byte[] data = dataProvider.get();
        if (data != null && data.length > 0) {
            connection.setDoOutput(true);
            final OutputStream w = connection.getOutputStream();
            w.write(data);
            w.close();
        }

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpStatus.HTTP_OK) {
           // throw new DolphinRuntimeException("Bad response: " + responseCode);
        }

        for(HttpURLConnectionHandler handler : responseHandlers) {
            handler.handle(connection);
        }

    }

}
