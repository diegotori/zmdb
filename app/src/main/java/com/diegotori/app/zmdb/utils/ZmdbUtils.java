package com.diegotori.app.zmdb.utils;

import android.content.res.Resources;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created by Diego on 10/23/2016.
 */

public final class ZmdbUtils {

    private ZmdbUtils() {
        throw new UnsupportedOperationException();
    }

    public static String generatePlaceholderUrl(final Resources res, final int w, final int h){
        final Uri.Builder builder = Uri.parse("http://www.lorempixel.com").buildUpon();
        final DisplayMetrics dm = res.getDisplayMetrics();
        final int dpW = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, w, dm);
        final int dpH = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, h, dm);
        builder.appendPath(Integer.toString(dpW))
                .appendPath(Integer.toString(dpH));
        return builder.build().toString();
    }
}
