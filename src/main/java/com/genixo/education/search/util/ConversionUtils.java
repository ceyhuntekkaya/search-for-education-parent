package com.genixo.education.search.util;

import com.genixo.education.search.enumaration.Currency;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Component
public class ConversionUtils {

    private static final Locale TR_LOCALE = new Locale("tr", "TR");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    // Turkish number format: 1.500,50
    private static final DecimalFormat MONEY_FORMAT;
    private static final DecimalFormat NUMBER_FORMAT;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(TR_LOCALE);
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');

        MONEY_FORMAT = new DecimalFormat("#,##0.00", symbols);
        NUMBER_FORMAT = new DecimalFormat("#,##0", symbols);
    }

    // ================== DATE & TIME FORMATTING ==================

    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : null;
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_TIME_FORMATTER) : null;
    }

    public static String formatTime(LocalTime time) {
        return time != null ? time.format(TIME_FORMATTER) : null;
    }

    public static String formatTimeRange(LocalTime startTime, LocalTime endTime) {
        if (startTime == null || endTime == null) {
            return null;
        }
        return formatTime(startTime) + " - " + formatTime(endTime);
    }

    // ================== MONEY & CURRENCY FORMATTING ==================

    public static String formatPrice(BigDecimal amount, Currency currency) {
        if (amount == null) {
            return null;
        }

        String formattedAmount = MONEY_FORMAT.format(amount);
        String currencySymbol = currency != null ? currency.getSymbol() : "₺";

        return formattedAmount + " " + currencySymbol;
    }

    public static String formatPrice(Double amount, Currency currency) {
        if (amount == null) {
            return null;
        }
        return formatPrice(BigDecimal.valueOf(amount), currency);
    }

    public static String formatPrice(BigDecimal amount) {
        return formatPrice(amount, Currency.TRY);
    }

    public static String formatPrice(Double amount) {
        return formatPrice(amount, Currency.TRY);
    }

    public static String formatPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Currency currency) {
        if (minPrice == null && maxPrice == null) {
            return null;
        }

        if (minPrice != null && maxPrice != null) {
            if (minPrice.compareTo(maxPrice) == 0) {
                return formatPrice(minPrice, currency);
            }
            return formatPrice(minPrice, currency) + " - " + formatPrice(maxPrice, currency);
        }

        if (minPrice != null) {
            return formatPrice(minPrice, currency) + "+";
        }

        return "0 - " + formatPrice(maxPrice, currency);
    }

    // ================== NUMBER FORMATTING ==================

    public static String formatNumber(Long number) {
        return number != null ? NUMBER_FORMAT.format(number) : null;
    }

    public static String formatNumber(Integer number) {
        return number != null ? NUMBER_FORMAT.format(number) : null;
    }

    public static String formatPercentage(Double percentage) {
        if (percentage == null) {
            return null;
        }

        DecimalFormat percentFormat = new DecimalFormat("#,##0.0",
                new DecimalFormatSymbols(TR_LOCALE));
        return percentFormat.format(percentage) + "%";
    }

    // ================== AGE & RANGE FORMATTING ==================

    public static String formatAgeRange(Integer minAge, Integer maxAge) {
        if (minAge == null && maxAge == null) {
            return null;
        }

        if (minAge != null && maxAge != null) {
            if (minAge.equals(maxAge)) {
                return minAge + " yaş";
            }
            return minAge + "-" + maxAge + " yaş";
        }

        if (minAge != null) {
            return minAge + "+ yaş";
        }

        return "0-" + maxAge + " yaş";
    }

    public static String formatCapacity(Integer current, Integer total) {
        if (current == null && total == null) {
            return null;
        }

        String currentStr = current != null ? formatNumber(current) : "0";
        String totalStr = total != null ? formatNumber(total) : "∞";

        return currentStr + "/" + totalStr;
    }

    // ================== DURATION FORMATTING ==================

    public static String formatDuration(Integer seconds) {
        if (seconds == null || seconds <= 0) {
            return null;
        }

        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, secs);
        } else {
            return String.format("%d:%02d", minutes, secs);
        }
    }

    public static String formatDurationInWords(Integer seconds) {
        if (seconds == null || seconds <= 0) {
            return null;
        }

        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;

        if (hours > 0 && minutes > 0) {
            return hours + " saat " + minutes + " dakika";
        } else if (hours > 0) {
            return hours + " saat";
        } else if (minutes > 0) {
            return minutes + " dakika";
        } else {
            return "1 dakikadan az";
        }
    }

    // ================== DISTANCE FORMATTING ==================

    public static String formatDistance(Double distanceKm) {
        if (distanceKm == null) {
            return null;
        }

        if (distanceKm < 1.0) {
            int meters = (int) (distanceKm * 1000);
            return formatNumber(meters) + " m";
        } else {
            DecimalFormat kmFormat = new DecimalFormat("#,##0.0",
                    new DecimalFormatSymbols(TR_LOCALE));
            return kmFormat.format(distanceKm) + " km";
        }
    }

    // ================== SLUG GENERATION ==================

    public static String generateSlug(String text) {
        if (!StringUtils.hasText(text)) {
            return null;
        }

        // Turkish character replacements
        String slug = text.toLowerCase(TR_LOCALE)
                .replace("ğ", "g")
                .replace("ü", "u")
                .replace("ş", "s")
                .replace("ı", "i")
                .replace("ö", "o")
                .replace("ç", "c");

        // Normalize to remove accents
        slug = Normalizer.normalize(slug, Normalizer.Form.NFD);

        // Remove non-alphanumeric characters except hyphens
        slug = slug.replaceAll("[^a-z0-9\\s-]", "");

        // Replace spaces with hyphens
        slug = slug.replaceAll("\\s+", "-");

        // Remove multiple consecutive hyphens
        slug = slug.replaceAll("-+", "-");

        // Remove leading/trailing hyphens
        slug = slug.replaceAll("^-|-$", "");

        return slug.isEmpty() ? null : slug;
    }

    // ================== TEXT FORMATTING ==================

    public static String formatPhoneNumber(String phone) {
        if (!StringUtils.hasText(phone)) {
            return null;
        }

        // Remove all non-digits
        String digits = phone.replaceAll("\\D", "");

        // Turkish phone format: +90 (5xx) xxx xx xx
        if (digits.startsWith("90") && digits.length() == 12) {
            return String.format("+90 (%s) %s %s %s",
                    digits.substring(2, 5),
                    digits.substring(5, 8),
                    digits.substring(8, 10),
                    digits.substring(10, 12));
        }

        // Domestic format: (5xx) xxx xx xx
        if (digits.startsWith("5") && digits.length() == 10) {
            return String.format("(%s) %s %s %s",
                    digits.substring(0, 3),
                    digits.substring(3, 6),
                    digits.substring(6, 8),
                    digits.substring(8, 10));
        }

        return phone; // Return original if format not recognized
    }

    public static String truncateText(String text, int maxLength) {
        if (!StringUtils.hasText(text) || text.length() <= maxLength) {
            return text;
        }

        return text.substring(0, maxLength - 3) + "...";
    }

    public static String capitalizeWords(String text) {
        if (!StringUtils.hasText(text)) {
            return text;
        }

        String[] words = text.split("\\s+");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (word.length() > 0) {
                result.append(word.substring(0, 1).toUpperCase(TR_LOCALE))
                        .append(word.substring(1).toLowerCase(TR_LOCALE))
                        .append(" ");
            }
        }

        return result.toString().trim();
    }

    // ================== URL & VALIDATION ==================

    public static boolean isValidUrl(String url) {
        if (!StringUtils.hasText(url)) {
            return false;
        }

        try {
            Pattern urlPattern = Pattern.compile(
                    "^(https?://)?" +                     // Protocol
                            "([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}" +  // Domain
                            "(:[0-9]+)?" +                        // Port
                            "(/.*)?$"                             // Path
            );
            return urlPattern.matcher(url).matches();
        } catch (Exception e) {
            return false;
        }
    }

    public static String ensureHttpProtocol(String url) {
        if (!StringUtils.hasText(url)) {
            return url;
        }

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return "https://" + url;
        }

        return url;
    }

    // ================== STATUS & ENUM DISPLAY ==================

    public static String getDisplayName(Enum<?> enumValue) {
        if (enumValue == null) {
            return null;
        }

        // Convert enum name to readable format
        // ENUM_VALUE -> Enum Value
        String name = enumValue.name().toLowerCase().replace("_", " ");
        return capitalizeWords(name);
    }

    // ================== CALCULATION UTILITIES ==================

    public static Double calculatePercentage(Long part, Long total) {
        if (part == null || total == null || total == 0) {
            return 0.0;
        }

        return (part.doubleValue() / total.doubleValue()) * 100.0;
    }

    public static Double calculateGrowthRate(BigDecimal current, BigDecimal previous) {
        if (current == null || previous == null || previous.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }

        return current.subtract(previous)
                .divide(previous, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    public static Long daysBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return null;
        }

        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    public static String formatFileSize(Long bytes) {
        if (bytes == null || bytes <= 0) {
            return "0 B";
        }

        String[] units = {"B", "KB", "MB", "GB", "TB"};
        double size = bytes.doubleValue();
        int unitIndex = 0;

        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }

        DecimalFormat sizeFormat = new DecimalFormat("#,##0.#",
                new DecimalFormatSymbols(TR_LOCALE));
        return sizeFormat.format(size) + " " + units[unitIndex];
    }

    // ================== COLLECTION UTILITIES ==================

    public static boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    public static int safeSize(List<?> list) {
        return list != null ? list.size() : 0;
    }

    // ================== NULL SAFETY ==================

    public static <T> T defaultIfNull(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    public static String defaultIfEmpty(String value, String defaultValue) {
        return StringUtils.hasText(value) ? value : defaultValue;
    }
}