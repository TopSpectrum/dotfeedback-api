package feedback.web.api.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.zipwhip.concurrent.*;
import com.zipwhip.events.Observer;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * {discussion here}
 *
 * @author msmyers
 * @version 1.0.0
 * @since 1/3/16
 */
public final class FutureUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(FutureUtils.class);

    public static final Observer NULL_OBSERVER = (Observer<ObservableFuture>) (sender, future) -> {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("NULL_OBSERVER (sender:{}) (future:{})", sender, future);
        }
    };

    private FutureUtils() {
    }

    /**
     * @param source
     * @param destination
     * @return TRUE if failure
     */
    public static boolean checkFailure(ObservableFuture<?> source, MutableObservableFuture<?> destination) {
        if (source.isCancelled()) {
            destination.cancel();

            return true;
        } else if (source.isFailed()) {
            destination.setFailure(source.getCause());

            return true;
        } else {
            return false;
        }
    }

    /**
     * @param source
     * @param destination
     * @param adapter
     * @param <TSource>
     * @param <TDestination>
     * @return TRUE if success, FALSE if failure
     */
    public static <TSource, TDestination> void propagate(@Nonnull final ObservableFuture<TSource> source, @Nonnull final MutableObservableFuture<TDestination> destination, @Nonnull final Function<TSource, TDestination> adapter) {
        if (!checkFailure(source, destination)) {
            destination.setSuccess(adapter.apply(source.getResult()));
        }
    }

    @Nullable
    public static <V> List<V> opt(@Nullable final List<ObservableFuture<V>> futures) {
        if (CollectionUtils.isEmpty(futures)) {
            return null;
        }

        return futures
                .stream()
                .map(FutureUtils::opt)
                .collect(Collectors.toList());
    }

    @Nullable
    public static <V> V opt(@Nonnull final ObservableFuture<V> future) {
        return opt(future, 30, TimeUnit.SECONDS);
    }

    @Nullable
    public static <V> V opt(@Nonnull final ObservableFuture<V> future, long time, @Nonnull final TimeUnit units) {
        return opt(future, time, units, null);
    }

    @Nullable
    public static <V> V opt(@Nonnull final ObservableFuture<V> future, long time, @Nonnull final TimeUnit units, @Nullable final V defaultValueIfException) {
        Preconditions.checkNotNull(future);

        try {
            return future.get(time, units);
        } catch (Throwable e) {
            return defaultValueIfException;
        }
    }

    @Nullable
    public static <V> V getUnchecked(@Nonnull final ObservableFuture<V> future) {
        return getUnchecked(future, 30, TimeUnit.SECONDS);
    }

    @Nullable
    public static <V> V getUnchecked(@Nonnull final ObservableFuture<V> future, long time, TimeUnit units) {
        Preconditions.checkNotNull(future);

        try {
            return future.get(time, units);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static <K, V> Observer<ObservableFuture<K>> propagateStateObserver(@Nonnull final MutableObservableFuture<V> result, @Nonnull final Function<K, V> converter) {
        return (source, future) -> propagate(future, result, converter);
    }

//    public static <K, V> void propagateState(@Nonnull final ObservableFuture<K> source, @Nonnull final MutableObservableFuture<V> result, @Nonnull final Function<K, V> converter) {
//        if (source.isFailed()) {
//            result.setFailure(source.getCause());
//        } else if (source.isCancelled()) {
//            result.cancel();
//        } else {
//            result.setSuccess(converter.apply(source.getResult()));
//        }
//    }

    @Nonnull
    public static <K, V> ObservableFuture<V> unwrap(@Nonnull final ObservableFuture<K> future, @Nonnull final Function<K, V> fn) {
        MutableObservableFuture<V> result = new DefaultObservableFuture<V>(null);

        future.addObserver(new Observer<ObservableFuture<K>>() {
            @Override
            public void notify(Object o, ObservableFuture<K> future) {
                if (future.isFailed() || future.isCancelled()) {
                    NestedObservableFuture.syncFailure(future, result);
                } else {
                    try {
                        result.setSuccess(fn.apply(future.getResult()));
                    } catch (Exception e) {
                        result.setFailure(e);
                    }
                }
            }
        });

        return result;
    }

    @Nonnull
    public static <T> ObservableFuture<T> wrapFailureObserver(@Nonnull final ObservableFuture<T> future) {
        return wrapFailureObserver(future, null);
    }

    @Nonnull
    public static <T> ObservableFuture<T> wrapFailureObserver(@Nonnull final ObservableFuture<T> future, @Nullable final Logger logger) {
        future.addObserver(failureObserver(logger));

        return future;
    }

    @Nonnull
    public static <T> Observer<ObservableFuture<T>> failureObserver() {
        return failureObserver(null);
    }

    @Nonnull
    public static <T> Observer<ObservableFuture<T>> loggingObserver(@Nullable final Logger logger) {
        return (o, future) -> logFutureStatus(logger, future);
    }

    public static void logFutureStatus(@Nullable final Logger logger, @Nonnull ObservableFuture<?> future) {
        Logger chosen = Preconditions.checkNotNull(ConversionUtils.defaultObject(logger, LOGGER), "never happens");

        if (future.isSuccess()) {
            chosen.debug("Future: {}", future);
        } else if (future.isCancelled()) {
            chosen.info("Future: {}", future);
        } else if (future.isFailed()) {
            chosen.error(String.format("Future: %s", future), future.getCause());
        }
    }

    @Nonnull
    public static <T> Observer<ObservableFuture<T>> failureObserver(@Nullable final Logger logger) {
        @Nonnull
        final Logger finalLogger = Preconditions.checkNotNull(ConversionUtils.defaultObject(logger, LOGGER));

        return new Observer<ObservableFuture<T>>() {
            @Override
            public void notify(Object o, ObservableFuture<T> future) {
                if (future.isFailed()) {
                    finalLogger.error("This future has failed.", future.getCause());
                }
            }
        };
//        return new Observer<ObservableFuture<T>>(o, future1) {
//            @Override
//            public void notify(Object o, T t) {
//                if (future1.isFailed()) {
//                    finalLogger.error("This future has failed.", future1.getCause());
//                }
//            }
//        };
    }

    @Nullable
    public static <K, V> V unwrapPair(ImmutablePair<K, ObservableFuture<V>> pair) {
        if (null == pair) {
            return null;
        }

        return getResult(pair.getValue());
    }

    @Nullable
    public static <V> V getResult(ObservableFuture<V> future) {
        Preconditions.checkNotNull(future);
        Preconditions.checkState(future.isDone(), "not done");
        Preconditions.checkState(future.isSuccess(), "not success");

        return future.getResult();
    }

    public static boolean success(ObservableFuture<?> future) {
        if (null == future) {
            return false;
        }

        return future.isSuccess();
    }

    public static <K, V> ObservableFuture<ImmutablePair<K, ObservableFuture<V>>> firstSuccess(Collection<K> set, Function<K, ObservableFuture<V>> fn) {
        return any(set, fn, FutureUtils::success);
    }

    public static <K, V> ObservableFuture<ImmutablePair<K, ObservableFuture<V>>> any(Collection<K> set, Function<K, ObservableFuture<V>> fn, Predicate<ObservableFuture<V>> predicate) {
        int size = set.size();
        MutableObservableFuture<ImmutablePair<K, ObservableFuture<V>>> result = new DefaultObservableFuture<>("FutureUtils");

        final AtomicInteger i = new AtomicInteger(0);

        for (K key : set) {
            fn.apply(key)
                    .addObserver(new Observer<ObservableFuture<V>>() {
                        @Override
                        public void notify(Object o, ObservableFuture<V> future) {
                            if (predicate.test(future)) {
                                result.setSuccess(new ImmutablePair<>(key, future));
                            }

                            if (i.incrementAndGet() == size && !result.isDone()) {
                                result.setFailure(new Exception("None of the elements matched."));
                            }
                        }
                    });
        }

        return result;
    }

    @Nullable
    public static <T extends Collection<ObservableFuture<V>>, V> T waitFor(@Nullable final T list) {
        return waitFor(list, 30, TimeUnit.SECONDS);
    }

    @Nullable
    public static <T extends Collection<ObservableFuture<V>>, V> T waitFor(@Nullable final T list, long time, @Nonnull final TimeUnit units) {
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }

        CountDownLatch latch = new CountDownLatch(list.size());

        for (ObservableFuture<V> future : list) {
            future.addObserver((o, f) -> latch.countDown());
        }

        try {
            if (!latch.await(time, units)) {
                throw new IllegalStateException("Did not return true: " + latch);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public static <K> ObservableFuture<List<ObservableFuture<K>>> each(@Nonnull final List<ObservableFuture<K>> collection, @Nullable final Observer<ObservableFuture<K>> observer) {
        MutableObservableFuture<List<ObservableFuture<K>>> result = new DefaultObservableFuture<>("FutureUtils");
        CountDownLatch latch = new CountDownLatch(collection.size());
        List<ObservableFuture<K>> list = ConversionUtils.immutableList(collection);

        for (ObservableFuture future : collection) {
            future.addObserver((sender, f) -> {
                if (null != observer) {
                    observer.notify(sender, (ObservableFuture<K>) f);
                }

                latch.countDown();

                if (0 == latch.getCount()) {
                    result.setSuccess(list);
                }
            });
        }

        return result;
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public static ObservableFuture<List<ObservableFuture>> eachUnchecked(@Nonnull final List<ObservableFuture> collection) {
        return eachUnchecked(collection, null);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public static ObservableFuture<List<ObservableFuture>> eachUnchecked(@Nonnull final List<ObservableFuture> collection, @Nullable final Observer<ObservableFuture> observer) {
        MutableObservableFuture<List<ObservableFuture>> result = new DefaultObservableFuture<>("FutureUtils");
        CountDownLatch latch = new CountDownLatch(collection.size());
        List<ObservableFuture> list = ConversionUtils.immutableList(collection);

        for (ObservableFuture future : collection) {
            future.addObserver((sender, f) -> {
                if (null != observer) {
                    observer.notify(sender, (ObservableFuture) f);
                }

                latch.countDown();

                if (0 == latch.getCount()) {
                    result.setSuccess(list);
                }
            });
        }

        return result;
    }

    @Nonnull
    public static <K> ObservableFuture<List<ObservableFuture<K>>> all(@Nonnull final List<ObservableFuture<K>> collection) {
        return each(collection, null);
    }

    @Nonnull
    public static <K> ObservableFuture<List<ObservableFuture<K>>> all(@Nonnull final ObservableFuture<K>... collection) {
        return each(Arrays.asList(collection), null);
    }

    @Nonnull
    public static <K, V> ObservableFuture<Map<K, ObservableFuture<V>>> all(Set<K> set, Function<K, ObservableFuture<V>> fn) {
        int size = set.size();
        MutableObservableFuture<Map<K, ObservableFuture<V>>> result = new DefaultObservableFuture<>("FutureUtils");
        Map<K, ObservableFuture<V>> map = Collections.synchronizedMap(Maps.newHashMapWithExpectedSize(size));

        for (K key : set) {
            fn.apply(key).addObserver((o, future) -> {
                synchronized (map) {
                    // Put the future into the map regardless of status.
                    map.put(key, future);

                    if (map.size() == size) {
                        result.setSuccess(map);
                    }
                }
            });
        }

        return result;
    }

    public static void assertSuccess(@Nonnull final Iterable<ObservableFuture> future) {
        ConversionUtils.toStream(future).forEach(FutureUtils::assertSuccess);
    }

    public static <T> void assertSuccess(@Nonnull final ObservableFuture<T> future) {
        Preconditions.checkNotNull(future);

        if (!future.isFailed()) {
            throw new IllegalStateException("Failed: " + future, future.getCause());
        } else if (future.isCancelled()) {
            throw new IllegalStateException("Cancelled: " + future);
        }
    }

    public static <T> ObservableFuture<T> withObserver(@Nonnull final ObservableFuture<T> future, @Nullable final Observer<ObservableFuture<T>> observer) {
        if (null != observer) {
            future.addObserver(observer);
        }

        return future;
    }

    @SuppressWarnings("unchecked")
    public static <T> Observer<ObservableFuture<T>> defaultObserver() {
        return (Observer<ObservableFuture<T>>) NULL_OBSERVER;
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public static <T> Observer<ObservableFuture<T>> defaultObserver(@Nullable final Observer<ObservableFuture<T>> observer) {
        return Preconditions.checkNotNull(ConversionUtils.defaultObject(observer, defaultObserver()));
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public static ObservableFuture<Void> flat(@Nullable final ObservableFuture<?>... futures) {
        if (null == futures) {
            return new FakeObservableFuture<>(FutureUtils.class, null);
        }

        return unwrap(eachUnchecked(Arrays.asList(futures)), new Function<List<ObservableFuture>, Void>() {
            @Override
            public Void apply(List<ObservableFuture> observableFutures) {
                assertSuccess(observableFutures);

                return null;
            }
        });
    }

    public static void await(CountDownLatch latch, long time, TimeUnit timeUnit) {
        try {
            if (!latch.await(time, timeUnit)) {
                throw new RuntimeException("Did not finish: " + latch);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T await(ObservableFuture<T> future, long time, TimeUnit timeUnit) {
        try {
            if (!future.await(time, timeUnit)) {
                throw new RuntimeException("Did not finish: " + future);
            }

            return future.getResult();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public static <T> T optResult(@Nullable final ObservableFuture<T> future) {
        if (null == future) {
            return null;
        }

        if (future.isCancelled() || future.isFailed()) {
            return null;
        }

        return future.getResult();
    }
}
