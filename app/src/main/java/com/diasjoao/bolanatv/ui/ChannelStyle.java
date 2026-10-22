package com.diasjoao.bolanatv.ui;

import android.content.Context;
import android.content.res.Configuration;

import java.text.Normalizer;
import java.util.Locale;

public final class ChannelStyle {

    public final int background;
    public final int text;

    private static final int WHITE = 0xFFFFFFFF;
    private static final int INK = 0xFF111111;

    private static final class Brand {
        final String[] keys;   // matched with startsWith on the normalized channel name
        final int bgLight, bgDark, textLight, textDark;

        Brand(String[] keys, int bgLight, int bgDark, int textLight, int textDark) {
            this.keys = keys;
            this.bgLight = bgLight;
            this.bgDark = bgDark;
            this.textLight = textLight;
            this.textDark = textDark;
        }
    }

    // Edit colors here. Format: keys, bg (light theme), bg (dark theme), text (light), text (dark).
    private static final Brand[] BRANDS = {
            new Brand(new String[]{"sporttv"},          0xFFFFD500, 0xFFFFD500, INK,   INK),
            new Brand(new String[]{"dazn"},             INK,        0xFFE6E6E6, WHITE, INK),
            new Brand(new String[]{"canal11"},          0xFF00FF78, 0xFF00D468, INK, INK),
            new Brand(new String[]{"sportingtv"},       0xFF00783A, 0xFF1E7B4A, WHITE, WHITE),
            new Brand(new String[]{"btv", "benficatv"}, 0xFFD0121B, 0xFFBF2B31, WHITE, WHITE),
            new Brand(new String[]{"abolatv", "abola"}, 0xFFA61B22, 0xFF9C2A30, WHITE, WHITE),
            new Brand(new String[]{"portocanal"},       0xFF0B3F8F, 0xFF2F5FA8, WHITE, WHITE),
            new Brand(new String[]{"rtp"},              0xFF1F3F8C, 0xFF3B5BA8, WHITE, WHITE), // placeholder
            new Brand(new String[]{"v+"},               0xFF6B3FA0, 0xFF7B4FA0, WHITE, WHITE), // placeholder
    };

    // Stable colors for channels not in the table above.
    private static final int[] FALLBACK = {
            0xFF7B4FA0, 0xFF23766F, 0xFF9A5A22, 0xFF5F7A2E, 0xFFA0457A, 0xFF3A67B5
    };

    private ChannelStyle(int background, int text) {
        this.background = background;
        this.text = text;
    }

    public static ChannelStyle of(Context c, String channel) {
        String key = normalize(channel);
        boolean night = (c.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;

        for (Brand b : BRANDS) {
            for (String k : b.keys) {
                if (key.startsWith(k)) {
                    return new ChannelStyle(night ? b.bgDark : b.bgLight,
                            night ? b.textDark : b.textLight);
                }
            }
        }
        int index = (key.hashCode() & 0x7fffffff) % FALLBACK.length;
        return new ChannelStyle(FALLBACK[index], WHITE);
    }

    // "SportTV 2" -> "sporttv2", "Sporting TV" -> "sportingtv", "A Bola TV" -> "abolatv"
    private static String normalize(String s) {
        if (s == null) return "";
        String n = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return n.toLowerCase(Locale.ROOT).replaceAll("[\\s.\\-]", "");
    }
}