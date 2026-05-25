package ua.uni.logging;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public final class SwingLogAppender extends AppenderBase<ILoggingEvent> {
    private static final List<Consumer<String>> LISTENERS = new CopyOnWriteArrayList<>();

    private PatternLayoutEncoder encoder;

    public static void addListener(Consumer<String> listener) {
        if (listener != null) {
            LISTENERS.add(listener);
        }
    }

    public static void removeListener(Consumer<String> listener) {
        LISTENERS.remove(listener);
    }

    public void setEncoder(PatternLayoutEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        if (encoder == null) {
            return;
        }
        String formatted = new String(encoder.encode(eventObject));
        for (Consumer<String> listener : LISTENERS) {
            listener.accept(formatted);
        }
    }
}
