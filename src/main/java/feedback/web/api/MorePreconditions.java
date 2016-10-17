package feedback.web.api;

import com.google.common.base.Preconditions;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.NotFoundException;
import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

/**
 * {discussion here}
 *
 * @author msmyers
 * @version 1.0.0
 * @since 2/25/15
 */
public final class MorePreconditions {

    @Nullable
    public static <T> T checkSerializableOrNull(@Nullable final T object) {
        if (null == object) {
            return null;
        }

        checkSerializable(object);

        return object;
    }

    @Nonnull
    public static <T> T checkSerializable(@Nonnull T object) {
        Preconditions.checkState(object instanceof Serializable);

        return object;
    }

//    @Nonnull
//    public static <T extends Collection> T checkNotEmpty(@Nullable final T collection) {
//        if (CollectionUtils.isEmpty(collection)) {
//            throw new IllegalStateException("The collection was empty and not allowed to be.");
//        }
//
//        return collection;
//    }

    @Nonnull
    public static <K, V> Map<K, V> checkNotEmpty(@Nullable final Map<K, V> map) {
        if (MapUtils.isEmpty(map)) {
            throw new IllegalStateException("The map was empty and not allowed to be.");
        }

        return map;
    }

    @Nonnull
    public static <V, K extends Collection<V>> K checkNotEmpty(@Nullable final K collection) {
        return checkNotEmpty(collection, null);
    }

    @Nonnull
    public static <V, K extends Collection<V>> K checkNotEmpty(@Nullable final K collection, @Nullable final String message) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new IllegalStateException(StringUtils.defaultIfBlank(message, "The collection was not allowed to be empty."));
        }

        return collection;
    }

    @Nonnull
    public static <K, V> Map<K, V> checkNotEmpty(@Nullable final Map<K, V> map, @Nullable final String message) {
        if (MapUtils.isEmpty(map)) {
            throw new IllegalStateException(StringUtils.defaultIfBlank(message, "The map was empty and not allowed to be."));
        }

        return map;
    }

    public static void checkPermission(boolean expression) throws SecurityException {
        if (!expression) {
            throw new SecurityException();
        }
    }

    public static void checkPermission(boolean expression, @Nullable final String errorMessage) throws SecurityException {
        if (!expression) {
            if (StringUtils.isBlank(errorMessage)) {
                throw new SecurityException();
            } else {
                throw new SecurityException(String.valueOf(errorMessage));
            }
        }
    }

    public static void checkPermission(boolean expression, @Nullable final String errorMessage, @Nullable final Object... arguments) throws SecurityException {
        if (!expression) {
            if (StringUtils.isBlank(errorMessage)) {
                throw new SecurityException();
            } else {
                throw new SecurityException(String.format(errorMessage, arguments));
            }
        }
    }

    /**
     * <p>Checks if a CharSequence is not empty (""), not null and not whitespace only.</p>
     * <p>
     * <pre>
     * StringUtils.isNotBlank(null)      = false
     * StringUtils.isNotBlank("")        = false
     * StringUtils.isNotBlank(" ")       = false
     * StringUtils.isNotBlank("bob")     = true
     * StringUtils.isNotBlank("  bob  ") = true
     * </pre>
     *
     * @param <T>   CharSequence
     * @param value The value to pass in. If null or empty, will crash
     * @return the original value
     * @throws IllegalArgumentException if the value is null
     */
    public static <T> T checkNotBlank(@Nullable final T value) throws IllegalArgumentException {
        return checkNotBlank(value, null);
    }

    public static <T> T checkNotBlank(@Nullable final T value, @Nullable final String message) throws IllegalArgumentException {
        boolean expression = expressionForNullOrEmpty(value);

        Preconditions.checkArgument(expression, message);

        return value;
    }

    @Nonnull
    public static <T> T checkNotNull(@Nullable final T object) {
        return Preconditions.checkNotNull(object);
    }

    @Nonnull
    public static <T> T checkNotNull(@Nullable final T object, @Nullable final String message) {
        return Preconditions.checkNotNull(object, message);
    }

    @Nonnull
    public static <T> T checkNotNull(@Nullable final T object, @Nullable final String messageTemplate, @Nullable final Object... parameters) {
        return Preconditions.checkNotNull(object, messageTemplate, parameters);
    }

    public static <T> T checkArgument(T parameters, boolean test) {
        Preconditions.checkArgument(test);

        return parameters;
    }

    @Nonnull
    public static <T> T checkArgument(@Nullable final T parameters) {
        return checkArgument(parameters, null);
    }

    @Nonnull
    public static <T> T checkArgument(@Nullable final T parameters, @Nullable final String message) {
        boolean expression = expressionForNullOrEmpty(parameters);

        if (StringUtils.isBlank(message)) {
            Preconditions.checkArgument(expression);
        } else {
            Preconditions.checkArgument(expression, message);
        }

        return parameters;
    }

    public static <T> T checkArgument(T parameters, boolean test, String errorMessage) {
        Preconditions.checkArgument(test, errorMessage);

        return parameters;
    }

    public static String checkMatches(String string, String search, int count, String message) {
        int matchCount = StringUtils.countMatches(string, search);

        return checkArgument(string, matchCount == count, message);
    }

    public static String checkMatches(String string, String search, int count) {
        return checkMatches(string, search, count, string);
    }

    @Nonnull
    public static <T> T checkState(@Nullable T object) {
        Preconditions.checkState(expressionForNullOrEmpty(object));

        return object;
    }

    @Nonnull
    public static <T> T checkState(@Nullable T object, @Nullable final String message, @Nullable final Object... errorMessageArgs) {
        Preconditions.checkState(expressionForNullOrEmpty(object), message, errorMessageArgs);

        return object;
    }

    /**
     * True for good.
     * False for bad.
     *
     * @param parameters
     * @param <T>
     * @return
     */
    private static <T> boolean expressionForNullOrEmpty(@Nullable T parameters) {
        if (null == parameters) {
            return false;
        }

        if (parameters instanceof Collection) {
            return !CollectionUtils.isEmpty((Collection) parameters);
        } else if (parameters instanceof String) {
            return StringUtils.isNotBlank((String) parameters);
        } else if (parameters instanceof URI) {
            return UrlUtils.isNotBlank((URI) parameters);
        } else if (parameters instanceof URL) {
            return UrlUtils.isNotBlank((URL) parameters);
        } else if (parameters instanceof Identity) {
            return IdentityUtil.isNotBlank((Identity) parameters);
        } else if (parameters instanceof Map) {
            return !MapUtils.isEmpty((Map) parameters);
        } else if (parameters instanceof Number) {
            return ((Number) parameters).intValue() <= 0;
        } else if (parameters instanceof Boolean) {
            return (Boolean) parameters;
        } else if (parameters instanceof Website) {
            Website p = ((Website) parameters);

            return !(StringUtils.isBlank(p.getCustomerDomainName()) || StringUtils.isBlank(p.getTopLevelDomainName()));
        } else {
            return true;
        }
    }

    @Nonnull
    public static <T> T checkNotFound(@Nullable final T value) throws NotFoundException {
        return checkNotFound(value, null);
    }

    @Nonnull
    public static <T> T checkNotFound(@Nullable final T value, @Nullable final String message) throws NotFoundException {
        return checkNotFound(value, message, (Object) null);
    }

    @Nonnull
    public static <T> T checkNotFound(@Nullable final T value, @Nullable final String message, @Nullable final Object... arguments) throws NotFoundException {
        if (null == value) {
            if (null == message) {
                throw new NotFoundException("Object not found");
            } else {
                throw new NotFoundException(StringUtils.defaultIfBlank(String.format(message, arguments), "Object not found"));
            }
        }

        return value;
    }

    @Nonnull
    public static <T> T notFound() {
        return notFound("Not found");
    }

    @Nonnull
    public static <T> T notFound(@Nullable final String message) {
        return notFound(message, (Object[]) null);
    }

    @Nonnull
    public static <T> T notFound(@Nullable final String message, @Nullable final Object... arguments) {
        @Nonnull
        final String _message;

        if (null == arguments || StringUtils.isBlank(message)) {
            _message = StringUtils.defaultIfBlank(message, "Not found");
        } else {
            _message = StringUtils.defaultIfBlank(String.format(message, arguments), "Not found");
        }

        throw new NotFoundException(_message);
    }

    public static <T> boolean isGenericallyBlank(@Nullable final T object) {
        return !expressionForNullOrEmpty(object);
    }

//    @Nullable
//    @SuppressWarnings("unchecked")
//    public static <T> T checkCastable(@Nullable final Object context, @Nonnull Class<T> clazz) {
//        if (null != context) {
//            Preconditions.checkState(ConversionUtils.isCastableObject(context, clazz), "Incorrect type. Expected {}, Found {}", clazz, context.getClass());
//        }
//
//        return (T) context;
//    }
//
//
//    @Nonnull
//    public static String checkIsValidEmail(@Nullable final String email) {
//        Preconditions.checkState(com.topspectrum.util.StringUtils.isValidEmail(email), "Must be valid email: " + email);
//
//        return Preconditions.checkNotNull(email);
//    }
//
//    @Nonnull
//    public static String checkValidFullDomainName(@Nullable final String fullDomainName) {
//        Preconditions.checkState(DomainNameUtils.isValidFullDomainName(fullDomainName), "Not valid fullDomainName: " + fullDomainName);
//
//        return Preconditions.checkNotNull(fullDomainName);
//    }
//
//    @Nonnull
//    public static <T extends FlexEntity> T checkNotNew(@Nullable final T entity) {
//        checkState(!DataUtil.isNew(entity));
//
//        return Preconditions.checkNotNull(entity);
//    }
//
//    @Nonnull
//    public static DateTimeBounds checkNotBlank(@Nonnull final DateTimeBounds bounds) {
//        return checkNotBlank(bounds, null);
//    }
//
//    @Nonnull
//    public static DateTimeBounds checkNotBlank(@Nonnull final DateTimeBounds bounds, @Nullable final String message) {
//        Preconditions.checkNotNull(bounds, StringUtils.defaultIfBlank(message, "bounds"));
//        Preconditions.checkNotNull(DateUtils.isNotBlank(bounds.optStartDate()), StringUtils.defaultIfBlank(message, "startDate"));
//        Preconditions.checkState(DateUtils.isNotBlank(bounds.optEndDate()), StringUtils.defaultIfBlank(message, "endDate"));
//
//        // for completion. just in case the definition of 'is blank' changes in the future.
//        Preconditions.checkState(DateUtils.isNotBlank(bounds), StringUtils.defaultIfBlank(message, "bounds"));
//
//        return bounds;
//    }
//
//    public static <T> T checkType(@Nonnull final T instance, @Nonnull final Class clazz) {
//        Preconditions.checkNotNull(instance, "checkType(instance, clazz) - instance was null");
//        Preconditions.checkNotNull(clazz, "checkType(instance, clazz) - clazz was null");
//
//        Preconditions.checkArgument(ConversionUtils.isCastableClass(ConversionUtils.optClass(instance), clazz), "was wrong type: " + ConversionUtils.optClassName(instance));
//
//        return instance;
//    }
//
//    @Nonnull
//    public static <T> T failWithWrongType(@Nullable Object instance, Class clazz) {
//        Preconditions.checkArgument(false, "was wrong type: " + ConversionUtils.optClassName(instance) + " (needed: " + ConversionUtils.optClassName(clazz) + ")");
//
//        return null;
//    }
//
//    @Nonnull
//    public static <T> T failWithUnsupportedType(@Nullable Object instance) {
//        Preconditions.checkArgument(false, "was unsupported type: " + ConversionUtils.optClassName(instance));
//
//        //noinspection ConstantConditions
//        return null;
//    }
}