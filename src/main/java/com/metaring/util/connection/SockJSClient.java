/**
 *    Copyright 2019 MetaRing s.r.l.
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.metaring.util.connection;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.metaring.framework.Tools;
import com.metaring.framework.type.DataRepresentation;

public final class SockJSClient {

    private Consumer<SockJSClient> onOpen;
    private Runnable onOpenSingle;

    private Consumer<SockJSClient> onHeartbeat;
    private Runnable onHeartbeatSingle;

    private BiConsumer<SockJSClient, String> onMessage;
    private Consumer<String> onMessageSingle;

    private BiConsumer<SockJSClient, Throwable> onError;
    private Consumer<Throwable> onErrorSingle;

    private Consumer<SockJSCloseData> onClose;

    private SockJS sockJS;

    public final SockJSClient onOpen(Consumer<SockJSClient> onOpen) {
        this.onOpen = onOpen;
        this.onOpenSingle = null;
        return this;
    }

    public final SockJSClient onOpen(Runnable onOpenSingle) {
        this.onOpenSingle = onOpenSingle;
        this.onOpen = null;
        return this;
    }

    public final SockJSClient onHeartbeat(Consumer<SockJSClient> onHeartbeat) {
        this.onHeartbeat = onHeartbeat;
        this.onHeartbeatSingle = null;
        return this;
    }

    public final SockJSClient onHeartbeat(Runnable onHeartbeatSingle) {
        this.onHeartbeatSingle = onHeartbeatSingle;
        this.onHeartbeat = null;
        return this;
    }

    public final SockJSClient onMessage(BiConsumer<SockJSClient, String> onMessage) {
        this.onMessage = onMessage;
        this.onMessageSingle = null;
        return this;
    }

    public final SockJSClient onMessage(Consumer<String> onMessageSingle) {
        this.onMessageSingle = onMessageSingle;
        this.onMessage = null;
        return this;
    }

    public final SockJSClient onError(BiConsumer<SockJSClient, Throwable> onError) {
        this.onError = onError;
        this.onErrorSingle = null;
        return this;
    }

    public final SockJSClient onError(Consumer<Throwable> onErrorSingle) {
        this.onErrorSingle = onErrorSingle;
        this.onError = null;
        return this;
    }

    public final SockJSClient onClose(Consumer<SockJSCloseData> onClose) {
        this.onClose = onClose;
        return this;
    }

    public final SockJSClient connect() {
        this.sockJS.connect();
        return this;
    }

    public final SockJSClient connect(Runnable onOpenSingle) {
        this.onOpen(() -> {
            this.onOpen = null;
            this.onOpenSingle = null;
            onOpenSingle.run();
        });
        this.sockJS.connect();
        return this;
    }

    public final SockJSClient connect(Consumer<SockJSClient> onOpen) {
        this.onOpen(client -> {
            this.onOpen = null;
            this.onOpenSingle = null;
            onOpen.accept(client);
        });
        this.sockJS.connect();
        return this;
    }

    public final SockJSClient send(String text) {
        this.sockJS.send(text);
        return this;
    }

    public final SockJSClient disconnect() {
        try {
            this.sockJS.closeBlocking();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    private SockJSClient(String connection) {
        connection = connection.trim();
        if (!connection.endsWith("/")) {
            connection += "/";
        }
        connection += "info?=" + System.currentTimeMillis();
        String wsConnection = "ws";
        try {
            URL url = new URL(connection);
            if (url.getProtocol().toLowerCase().equals("https")) {
                wsConnection += "s";
            }
            wsConnection += "://";
            wsConnection += url.getHost();
            if (url.getPort() > 0) {
                wsConnection += (":" + url.getPort());
            }
            wsConnection += url.getPath().replace("/info", "");
            if (!wsConnection.endsWith("/")) {
                wsConnection += "/";
            }
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();
            try (InputStream inputStream = urlConnection.getInputStream()) {
                try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
                    try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                        String entropy = Tools.FACTORY_DATA_REPRESENTATION.fromJson(bufferedReader.readLine()).getDigit("entropy").toString();
                        int halfLength = entropy.length() / 2;
                        wsConnection += entropy.substring(0, halfLength);
                        wsConnection += "/";
                        wsConnection += entropy.substring(halfLength);
                        wsConnection += "/websocket";
                    }
                }
            }
            this.sockJS = new SockJS(wsConnection);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static final SockJSClient create(String connection) {
        return new SockJSClient(connection);
    }

    public final class SockJSCloseData {

        public final SockJSClient source;
        public final int code;
        public final String reason;
        public final boolean remote;

        public SockJSCloseData(SockJSClient source, int code, String reason, boolean remote) {
            super();
            this.source = source;
            this.code = code;
            this.reason = reason;
            this.remote = remote;
        }
    }

    final class SockJS extends WebSocketClient {

        private static final String SOCKJS_O = "o";
        private static final String SOCKJS_H = "h";
        private static final String SOCKJS_A = "a";

        private SockJS(String connection) throws URISyntaxException {
            super(new URI(connection));
        }

        @Override
        public final void onOpen(ServerHandshake srverHandshake) {
            if (onOpen != null) {
                onOpen.accept(SockJSClient.this);
                return;
            }
            if (onOpenSingle != null) {
                onOpenSingle.run();
                return;
            }
        }

        @Override
        public final void onMessage(String message) {
            if (message.equals(SOCKJS_O)) {
                return;
            }
            if (message.equals(SOCKJS_H)) {
                if (onHeartbeat != null) {
                    onHeartbeat.accept(SockJSClient.this);
                }
                if (onHeartbeatSingle != null) {
                    onHeartbeatSingle.run();
                }
                return;
            }
            if (onMessage != null || onMessageSingle != null) {
                String response = "";
                if (message.startsWith(SOCKJS_A)) {
                    String m = message.substring(1);
                    if (!m.trim().isEmpty() && !m.trim().replace(" ", "").equals("[]")) {
                        DataRepresentation rep = Tools.FACTORY_DATA_REPRESENTATION.fromJson(m);
                        if (!rep.isSimple() && !rep.isEmpty()) {
                            rep = rep.first();
                        }
                        response = rep.asText();
                    }
                }
                if (onMessage != null) {
                    onMessage.accept(SockJSClient.this, response);
                }
                if (onMessageSingle != null) {
                    onMessageSingle.accept(response);
                }
                return;
            }
        }

        @Override
        public final void onClose(int status, String reason, boolean remote) {
            if (onClose != null) {
                onClose.accept(new SockJSCloseData(SockJSClient.this, status, reason, remote));
                return;
            }
        }

        @Override
        public final void onError(Exception t) {
            t.printStackTrace();
            if (onError != null) {
                onError.accept(SockJSClient.this, t);
                return;
            }
            if (onErrorSingle != null) {
                onErrorSingle.accept(t);
                return;
            }
            throw new RuntimeException("WebSocket: onError", t);
        }

        @Override
        public final void send(String text) {
            try {
                super.send(Tools.FACTORY_DATA_REPRESENTATION.create().add(text).toJson());
            }
            catch (Exception e) {
                onError(e);
            }
        }
    }
}