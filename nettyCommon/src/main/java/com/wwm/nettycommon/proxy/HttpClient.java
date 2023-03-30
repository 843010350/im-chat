package com.wwm.nettycommon.proxy;

import okhttp3.*;

import java.io.IOException;

/**
 *
 * @since JDK 1.8
 */
public final class HttpClient {

    private static MediaType mediaType = MediaType.parse("application/json");

    public static Response call(OkHttpClient okHttpClient, String params, String url) throws IOException {
        RequestBody requestBody = RequestBody.create(mediaType, params);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }

        return response;
    }
}
