package com.diegotori.app.zmdb;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.util.Pair;

import java.io.InputStream;
import java.net.HttpURLConnection;

import okhttp3.mockwebserver.MockResponse;
import okio.Buffer;

/**
 * Created by Diego on 10/22/2016.
 */

public final class AndroidTestUtils {

    private AndroidTestUtils() {
        throw new UnsupportedOperationException();
    }

    private static final String TAG = "SdkTestIntegrationUtils";

    /**
     * Safely creates a {@link MockResponse} based on the given asset path for the provided
     * {@link Context} via {@link #loadAssetStream(Context, String)}.
     * @param context the Context to use
     * @param assetPath the asset's path
     * @return a MockResponse for the given Context's asset path, or one with error code {@link
     * HttpURLConnection#HTTP_NOT_FOUND HTTP_NOT_FOUND} if the asset could not be found.
     */
    public static MockResponse getMockResponseFromAssets(final Context context,
                                                         final String assetPath) {
        final MockResponse response = new MockResponse();
        final Pair<Integer, InputStream> pair = loadAssetStream(context, assetPath);
        if (pair == null || (pair.first == null || pair.second == null) ) {
            response.setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
        } else {
            //final InputStream is = pair.second;
            response.setResponseCode(HttpURLConnection.HTTP_OK)
                    .setHeader("Content-Type", "application/json;charset=UTF-8")
                    .setHeader("Content-Length", String.valueOf(pair.first))
                    .setBody(wrapInputStreamIntoBuffer(pair));
        }
        return response;
    }

    /**
     * Creates a {@link Pair Pair&lt;Integer,InputStream&gt;} from the {@link AssetManager} of
     * the given {@link Context}.
     * @param context the Context to use
     * @param assetPath the asset path to load.
     * @return the Pair&lt;Integer,InputStream&gt; from the given asset path, or {@code null} if
     * none could be created.
     */
    public static Pair<Integer, InputStream> loadAssetStream(final Context context,
                                                             final String assetPath){
        try {
            final AssetManager am = context.getAssets();
            final InputStream is = am.open(assetPath);
            return new Pair<>(is.available(), is);
        }
        catch (Exception e) {
            Log.d(TAG, "Caught while trying to load asset stream: " + e.getMessage(), e);
            return null;
        }
    }

    private static Buffer wrapInputStreamIntoBuffer(final Pair<Integer, InputStream> pair){
        final Buffer result = new Buffer();
        if(pair != null && (pair.first != null && pair.second != null)){
            try {
                result.readFrom(pair.second, pair.first);
            } catch (Exception e){
                Log.d(TAG, "Caught while trying to wrap InputStream pair into buffer: " +
                        e.getMessage(), e);
            }
        }

        return result;
    }
}
