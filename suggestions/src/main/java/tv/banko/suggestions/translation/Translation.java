package tv.banko.suggestions.translation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;

public class Translation {

    private final String resourceBundlePrefix;
    private final ClassLoader loader;

    public Translation(String resourceBundlePrefix) {
        this.resourceBundlePrefix = resourceBundlePrefix;
        this.loader = getClass().getClassLoader();
    }

    public Translation(String resourceBundlePrefix, ClassLoader loader) {
        this.resourceBundlePrefix = resourceBundlePrefix;
        this.loader = loader;
    }

    @NotNull
    public String get(String key, Object... format) {
        return Objects.requireNonNullElse(get(key, Locale.ENGLISH, format), key);
    }

    @NotNull
    public String get(String key, Locale locale, Object... format) {
        return Objects.requireNonNullElse(getNull(key, locale, format), key);
    }

    @Nullable
    public String getNull(String key, Object... format) {
        return getNull(key, Locale.ENGLISH, format);
    }

    @Nullable
    public String getNull(String key, Locale locale, Object... format) {
        try {
            ResourceBundle bundle = getResourceBundle(locale);

            if (!bundle.containsKey(key)) {
                bundle = getDefaultResourceBundle();

                if (!bundle.containsKey(key)) {
                    return null;
                }
            }

            if (format.length == 0) {
                return bundle.getString(key);
            }

            return MessageFormat.format(bundle.getString(key), format);
        } catch (MissingResourceException e) {
            return null;
        }
    }

    @NotNull
    private ResourceBundle getResourceBundle(Locale locale) {
        try {
            return ResourceBundle.getBundle(resourceBundlePrefix, locale, loader);
        } catch (MissingResourceException e) {
            return getDefaultResourceBundle();
        }
    }

    private ResourceBundle getDefaultResourceBundle() {
        return ResourceBundle.getBundle(resourceBundlePrefix, Locale.GERMAN, loader);
    }
}
