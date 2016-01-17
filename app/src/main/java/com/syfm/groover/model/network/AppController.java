package com.syfm.groover.model.network;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.jdeferred.android.AndroidDeferredManager;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;

/**
 * Created by lycoris on 2015/09/24.
 */
public class AppController extends Application {
    public static final String TAG = AppController.class.getSimpleName();

    private RequestQueue mRequestQueue;

    private static AppController sInstance;

    private static CookieManager cookieManager;

    private static OkHttpClient client;

    private static final String SET_COOKIE_KEY = "Set-Cookie";
    private static final String SESSION_COOKIE = "PHPSESSID";
    private static final String USER_AGENT_KEY = "User-Agent";
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.85 Safari/537.36";

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        client = new OkHttpClient.Builder()
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .build();
    }

    public static synchronized AppController getInstance() {
        return sInstance;
    }


    public RequestQueue getRequestQueue() {

        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext(), new HurlStack() {

                @Override
                public HttpURLConnection createConnection(URL url) throws IOException {
                    HttpURLConnection connection = super.createConnection(url);
                    connection.setInstanceFollowRedirects(false);
                    connection.setRequestProperty(USER_AGENT_KEY, USER_AGENT);
                    connection.setDoInput(true);
                    connection.setConnectTimeout(15000);
                    connection.setReadTimeout(10000);

                    return connection;
                }
            }, 1);
        }
        if(cookieManager.getCookieStore().getCookies().size() != 0) {
            cookieManager.getCookieStore().getCookies().get(0).setMaxAge(60 * 60 * 24 * 365);
        }

        return mRequestQueue;
    }


    public <T> void addToRequestQueue(JsonObjectRequest req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
        Log.d("AppController", "API accessed");
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public final boolean checkSessionCookie(Map<String, String> headers) {
        if (headers.containsKey(SET_COOKIE_KEY) && headers.get(SET_COOKIE_KEY).startsWith(SESSION_COOKIE)) {
            return true;
        }
        return false;
    }

    public final boolean checkLoginCookie() {
        // Groove Coasterだけで判断すること
        if(cookieManager.getCookieStore().getCookies().size() > 0) {
            return true;
        }
        return false;
    }

    public static OkHttpClient getOkHttpClient() {
        Log.d("AppController", "API accessed");
        return client;
    }

}
