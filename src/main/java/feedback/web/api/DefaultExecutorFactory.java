package feedback.web.api;

import com.google.common.base.Preconditions;
import com.zipwhip.executors.NamedThreadFactory;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author msmyers
 * @since 8/11/16
 */
public class DefaultExecutorFactory implements ExecutorFactory {

    private static final DefaultExecutorFactory INSTANCE = new DefaultExecutorFactory();

    private final int threadsPerWorker;
    private final int threadsPerEvents;

    public DefaultExecutorFactory() {
        this(null, null);
    }

    public DefaultExecutorFactory(@Nullable final Integer threadsPerWorker, @Nullable final Integer threadsPerEvents) {
        if (threadsPerEvents != null) {
            this.threadsPerEvents = threadsPerEvents;
        } else {
            this.threadsPerEvents = 1;
        }

        if (null != threadsPerWorker) {
            this.threadsPerWorker = threadsPerWorker;
        } else {
            this.threadsPerWorker = 10;
        }
    }

    @Nonnull
    @Override
    public ExecutorService create(@Nonnull ExecutorType type, @Nullable String prefix) {
        Preconditions.checkNotNull(type);

        prefix = ensureSuffix(StringUtils.defaultIfBlank(prefix, type.toString()), "-");

        final ThreadFactory namedThreadFactory = new NamedThreadFactory(prefix);
        final Executor executor;

        switch (type) {
            case Boss:
                executor = Executors.newSingleThreadExecutor(namedThreadFactory);
                break;
            case Worker:
                executor = Executors.newFixedThreadPool(threadsPerWorker, namedThreadFactory);
                break;
            case Events:
                executor = Executors.newFixedThreadPool(threadsPerEvents, namedThreadFactory);
                break;
            default:
                throw new IllegalStateException("not possible");
        }

        return new ObservableExecutorAdapter(executor);
    }

    @Nonnull
    @Override
    public ExecutorService create(@Nonnull ExecutorType type) {
        return create(type, "ObservableExecutor-" + type.toString() + "-");
    }

    @Nonnull
    public static ExecutorFactory getInstance() {
        return INSTANCE;
    }

    @Nonnull
    public static ExecutorFactory getOrCreate(@Nullable final ExecutorFactory factory) {
        if (null == factory) {
            return getInstance();
        }

        return factory;
    }

    @Nonnull
    public static String ensureSuffix(@Nullable final String string, @Nonnull final String suffix) {
        if (StringUtils.endsWith(string, suffix) && null != string) {
            return string;
        }

        return StringUtils.defaultString(string) + suffix;
    }
}
