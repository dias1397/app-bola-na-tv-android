package com.diasjoao.bolanatv.ui;

import android.content.Context;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;


import com.diasjoao.bolanatv.R;

import java.util.Locale;

public final class ChannelStyle {

    public final int background;
    public final int text;

    private static final @ColorRes int[] FALLBACK = {
            R.color.bp_fb_1, R.color.bp_fb_2, R.color.bp_fb_3,
            R.color.bp_fb_4, R.color.bp_fb_5, R.color.bp_fb_6
    };

    private ChannelStyle(int background, int text) {
        this.background = background;
        this.text = text;
    }

    public static ChannelStyle of(Context c, String channel) {
        String key = channel.toLowerCase(Locale.ROOT).replace(" ", "");

        if (key.startsWith("sporttv")) {
            return make(c, R.color.bp_sporttv, R.color.bp_on_sportv);
        }
        if (key.startsWith("dazn")) {
            return make(c, R.color.bp_dazn, R.color.bp_on_dazn);
        }
        if (key.equals("canal11")) {
            return make(c, R.color.bp_canal11, R.color.bp_on_brand);
        }
        // Unknown channel: a stable color derived from the name
        int index = (key.hashCode() & 0x7fffffff) % FALLBACK.length;
        return make(c, FALLBACK[index], R.color.bp_on_brand);
    }

    private static ChannelStyle make(Context c, @ColorRes int bg, @ColorRes int text) {
        return new ChannelStyle(ContextCompat.getColor(c, bg), ContextCompat.getColor(c, text));
    }
}