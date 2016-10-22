package com.diegotori.app.zmdb;

import com.google.gson.stream.JsonReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import okhttp3.mockwebserver.MockResponse;
import okio.Buffer;

/**
 * Created by Diego on 10/22/2016.
 */

public final class TestUtils {

    private TestUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Safely creates a {@link MockResponse} based on the given resource path for the provided
     * {@link Class} via {@link #loadClassResourceStream(Class, String)}.
     * @param clazz the Class to use
     * @param resourcePath the resource's path
     * @return a MockResponse for the given Class's resource path, or {@code null} if none could
     * be found.
     */
    public static MockResponse createMockResponse(final Class<?> clazz, final String resourcePath) {
        return createMockResponse(loadClassResourceStream(clazz, resourcePath));
    }

    /**
     * Safely creates a {@link MockResponse} for the given resource path based on the provided
     * {@link Class} via {@link #loadClassLoaderResourceStream(ClassLoader, String)}.
     * @param classLoader the ClassLoader to use
     * @param resourcePath the resource's path
     * @return a MockResponse for the given ClassLoader's resource path, or {@code null} if none
     * could be found.
     */
    public static MockResponse createMockResponse(final ClassLoader classLoader,
                                                  final String resourcePath) {
        return createMockResponse(loadClassLoaderResourceStream(classLoader, resourcePath));
    }

    /**
     *
     * Safely creates a {@link MockResponse} based on the given {@link InputStream}.
     * @param is the InputStream to use
     * @return a MockResponse for the given InputStream, or one with error code {@link
     * HttpURLConnection#HTTP_NOT_FOUND HTTP_NOT_FOUND} if the incoming InputStream is {@code null}.
     */
    public static MockResponse createMockResponse(final InputStream is) {
        final MockResponse response = new MockResponse();
        if (is == null) {
            response.setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
        } else {
            try {
                final Buffer respBuffer = new Buffer();
                final int streamLength = is.available();
                respBuffer.readFrom(is, streamLength);
                response.setBody(respBuffer);
                response.setHeader("Content-Type", "application/json;charset=UTF-8");
                response.setHeader("Content-Length", String.valueOf(streamLength));
                response.setResponseCode(HttpURLConnection.HTTP_OK);
            } catch (Exception e) {
                response.setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
            }
        }
        return response;
    }

    /**
     * Wraps a byte array into a {@link Buffer}.
     * @param bytes the byte array to wrap
     * @return the wrapped byte array Buffer.
     */
    private static Buffer wrapByteArrayIntoBuffer(final byte[] bytes) {
        final Buffer result = new Buffer();
        if (bytes != null) {
            try {
                result.read(bytes);
            } catch (Exception e) {
                System.out.println("Caught while trying to wrap byte array into buffer: "
                        + e.getMessage());
            }
        }
        return result;
    }

    /**
     * Safely creates an {@link InputStream} for the given resource path based on the provided
     * {@link Class} via {@link Class#getResourceAsStream(String)}.
     * @param clazz the Class to use
     * @param resourcePath the resource's path
     * @return an InputStream for the given Class's resource path, or {@code null} if none could
     * be found.
     */
    public static InputStream loadClassResourceStream(final Class<?> clazz,
                                                      final String resourcePath) {
        try {
            return clazz.getResourceAsStream(resourcePath);
        } catch (Exception e) {
            System.out.println("Caught while trying to load class resource as stream: "
                    + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Safely creates an {@link InputStream} for the given resource path based on the provided
     * {@link ClassLoader} via {@link ClassLoader#getResourceAsStream(String)}.
     * @param classLoader the ClassLoader to use
     * @param resourcePath the resource's path
     * @return an InputStream for the given ClassLoader's resource path, or {@code null} if none
     * could be found.
     */
    public static InputStream loadClassLoaderResourceStream(final ClassLoader classLoader,
                                                            final String resourcePath) {
        try {
            return classLoader.getResourceAsStream(resourcePath);
        } catch (Exception e) {
            System.out.println("Caught while trying to load class resource as stream: "
                    + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Safely creates a {@link JsonReader} for the given resource path based on the provided
     * {@link Class} via {@link Class#getResourceAsStream(String)}.
     * @param clazz the Class to use
     * @param resourcePath the resource's path
     * @return a JsonReader for the given Class's resource path, or {@code null} if none could
     * be found.
     */
    public static JsonReader createJsonReaderFromClassResourceStream(final Class<?> clazz,
                                                                     final String resourcePath) {
        return createJsonReaderFromStream(loadClassResourceStream(clazz, resourcePath));
    }

    /**
     * Safely creates a {@link JsonReader} for the given resource path based on the provided
     * {@link ClassLoader} via {@link ClassLoader#getResourceAsStream(String)}.
     * @param classLoader the ClassLoader to use
     * @param resourcePath the resource's path
     * @return a JsonReader for the given ClassLoader's resource path, or {@code null} if none
     * could be found.
     */
    public static JsonReader createJsonReaderFromClassLoaderResourceStream(final ClassLoader
                                                                                   classLoader,
                                                                           final String
                                                                                   resourcePath) {
        return createJsonReaderFromStream(loadClassLoaderResourceStream(classLoader, resourcePath));
    }

    /**
     * Safely creates a {@link JsonReader} based on the given {@link InputStream}.
     * @param is the InputStream to use
     * @return a JsonReader for the given InputStream, or {@code null} if none could
     * be created due to a {@code null} InputStream.
     */
    public static JsonReader createJsonReaderFromStream(final InputStream is) {
        JsonReader result = null;
        if (is != null) {
            try {
                result = new JsonReader(new InputStreamReader(is, "UTF-8"));
            } catch (Exception e) {
                System.out.println("Caught while trying to create JsonReader from InputStream: "
                        + e.getMessage());
            }
        }
        return result;
    }
}
