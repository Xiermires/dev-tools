package org.dev.exception;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class LocalizationManager {

    private static final Logger log = LoggerFactory.getLogger(LocalizableException.class);

    private static final String DEFAULT_BUNDLE = "bundle_en.properties";
    private static final Charset charset = Charset.forName(System.getProperty("localization.default.charset", "UTF-8"));

    private static final AtomicBoolean initialized = new AtomicBoolean(false);
    private static final BiMap<String, String> id2Format = HashBiMap.create();
    static {
	try {
	    final String bundleName = System.getProperty("localization.default.bundlename", DEFAULT_BUNDLE);
	    final ResourceBundle bundle = getResourceBundle(bundleName);
	    final Enumeration<String> keys = bundle.getKeys();
	    while (keys.hasMoreElements()) {
		final String key = keys.nextElement();
		id2Format.put(key, bundle.getString(key));
	    }
	    initialized.set(true);
	} catch (Exception e) {
	    log.debug("Cannot initialize.", e);
	}
    }

    private static ResourceBundle getResourceBundle(final String bundleName) throws IOException {
	final InputStream is = LocalizableException.class.getClassLoader().getResourceAsStream(bundleName);
	final ResourceBundle bundle = new PropertyResourceBundle(new InputStreamReader(is, charset));
	return bundle;
    }

    public static String localize(String format, Object... args) {
	return localize(Locale.getDefault(), format, args);
    }

    public static String localize(Locale locale, String format, Object... args) {
	if (initialized.get()) {
	    try {
		String bundleName = "bundle_" + locale.getLanguage() + ".properties";
		final ResourceBundle bundle = getResourceBundle(bundleName);
		final String key = id2Format.inverse().get(format);
		if (key != null) {
		    final String lformat = bundle.getString(key);
		    if (lformat != null) {
			return String.format(lformat, args);
		    } else {
			log.debug("Cannot localize (no localized format for key '%').", key);
		    }
		} else {
		    log.debug("Cannot localize (no key for format '%').", format);
		}
	    } catch (Exception e) {
		log.debug("Cannot localize.", e);
	    }
	}
	log.debug("Cannot localize (no default bundle).");
	return String.format(format, args);
    }
}
