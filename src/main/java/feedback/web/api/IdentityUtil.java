package feedback.web.api;

import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author msmyers
 * @since 10/17/16
 */
public class IdentityUtil {

    private static final Pattern REPLACE_CAPITALS = Pattern.compile("([A-Z])");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("(?:\\w)+(?:\\w|-|\\.|\\+)*@(?:\\w)+(?:\\w|\\.|-)*\\.(?:\\w|\\.|-)+$");
    private static final Pattern EMAIL_NAME_VALUE_PATTERN = Pattern.compile("(.*)<(.*)>");

    public static Identity email(@Nonnull final String email) {
        return new Identity("email", MorePreconditions.checkArgument(email, IdentityUtil.isValidEmail(email), "not valid email: " + email));
    }

    public static boolean isNotBlank(@Nullable final Identity identity) {
        return !isBlank(identity);
    }

    public static boolean isBlank(@Nullable final Identity identity) {
        if (null == identity) {
            return true;
        }

        return StringUtils.isBlank(identity.getProviderId()) || StringUtils.isBlank(identity.getProviderUserId());
    }

    public static boolean isValidEmail(@Nullable final String string) {
        if (StringUtils.isBlank(string)) {
            return false;
        }

        if (string.contains(";")) {
            Iterable<String> emails = Splitter.on(";").trimResults().split(string);

            for (String email : emails) {
                if (!isValidEmail(email)) {
                    return false;
                }
            }

            return true;
        }

        if (1 != StringUtils.countMatches(string, "@")) {
            // TODO: add more validation
            return false;
        }

        if (1 == org.apache.commons.lang3.StringUtils.countMatches(string, "<")
                && 1 == org.apache.commons.lang3.StringUtils.countMatches(string, ">")
                && matches(EMAIL_NAME_VALUE_PATTERN, string)) {

            // Split this.
            MatchResult result = toMatchResult(EMAIL_NAME_VALUE_PATTERN, string);

            if (null == result) {
                return false;
            }

            String name = result.group(1);
            String email = result.group(2);

            return isValidEmail(email);
        }

        return matches(EMAIL_PATTERN, string);
    }

    @Nullable
    public static MatchResult toMatchResult(@Nonnull final Pattern pattern, @Nullable final String string) {
        if (null == string) {
            return null;
        }

        Matcher matcher = pattern.matcher(string);

        if (matcher.find()) {
            if (!matcher.matches()) {
                return null;
            }

            return matcher.toMatchResult();
        }

        return null;
    }

    public static boolean matches(@Nullable final Pattern pattern, @Nullable final CharSequence string) {
        if (null == pattern && null == string) {
            return true;
        } else if (null == pattern || null == string) {
            return false;
        }

        Matcher matcher = pattern.matcher(string);

        return matcher.matches();
    }
}
