package feedback.web.api;

import com.google.common.base.Preconditions;
import com.zipwhip.concurrent.*;
import com.zipwhip.concurrent.ExecutorFactory;
import com.zipwhip.executors.ExecutorAdapterBase;
import com.zipwhip.executors.NamedThreadFactory;
import com.zipwhip.executors.SimpleExecutor;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author msmyers
 * @since 8/5/16
 */
@SuppressWarnings("NullableProblems")
public class ObservableExecutorAdapter extends ExecutorAdapterBase {

    public ObservableExecutorAdapter(@Nonnull final Executor executor) {
        super(executor);
    }

    @Nonnull
    @Override
    public ObservableFuture<Void> submit(@Nullable final Runnable runnable) {
        if (null == runnable) {
            return new FakeObservableFuture<>(this, null);
        }

        return submit(() -> {
            runnable.run();

            return null;
        });
    }

    @Nonnull
    @Override
    public <T> ObservableFuture<T> submit(@Nullable Callable<T> callable) {
        if (null == callable) {
            return new FakeObservableFuture<T>(this, null);
        }

        final MutableObservableFuture<T> future = new DefaultObservableFuture<>(this);

        getExecutor().execute(() -> {
            if (future.isCancelled()) {
                return;
            }

            try {
                future.setSuccess(callable.call());
            } catch (Exception e) {
                if (e instanceof RuntimeException && (null != e.getCause())) {
                    future.setFailure(e.getCause());
                } else {
                    future.setFailure(e);
                }
            }
        });

        return future;
    }

    @Nonnull
    public static ObservableExecutorAdapter createSingleThreaded() {
        return new ObservableExecutorAdapter(ExecutorFactory.newInstance("ObservableExecutorAdapter-"));
    }

    @Nonnull
    public static ObservableExecutorAdapter createSimpleExecutor() {
        return new ObservableExecutorAdapter(SimpleExecutor.getInstance());
    }

    @Nonnull
    public static ObservableExecutorAdapter createFixedThreadPool() {
        return createFixedThreadPool(null);
    }

    @Nonnull
    public static ObservableExecutorAdapter createFixedThreadPool(@Nullable final String name) {
        return createFixedThreadPool(name, 10);
    }

    @Nonnull
    public static ObservableExecutorAdapter createFixedThreadPool(@Nullable final String name, int size) {
        Preconditions.checkState(0 < size, "Must be larger than 0");

        return new ObservableExecutorAdapter(Executors.newFixedThreadPool(size, new NamedThreadFactory(StringUtils.defaultIfBlank(name, "ObservableExecutorAdapter-"))));
    }
}
