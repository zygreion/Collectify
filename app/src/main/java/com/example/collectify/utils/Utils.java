package com.example.collectify.utils;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Utils {
    public static String formatDateTime(OffsetDateTime timestamp) {
        // Tentukan Locale untuk nama hari dan bulan dalam Bahasa Indonesia
        Locale indonesiaLocale = new Locale("id", "ID");

        // 3. Buat DateTimeFormatter kustom
        // "EEEE dd MMMM yyyy 'GMT'Z" -> Kamis, 26 Juni 2023
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", indonesiaLocale);

        // 4. Format OffsetDateTime menjadi String
        return timestamp.format(formatter);
    }
}
