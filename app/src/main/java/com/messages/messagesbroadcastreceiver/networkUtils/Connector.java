package com.messages.messagesbroadcastreceiver.networkUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.messages.messagesbroadcastreceiver.utils.Helper;

import java.util.Map;

/**
 * Created by Abdelrahman Hesham on 3/13/2018.
 */

public class Connector {

    private Context mContext;
    private LoadCallback mLoadCallback;
    private ErrorCallback mErrorCallback;
    private RequestQueue mQueue;
    private Map<String, String> mMap;


    public interface LoadCallback {

        void onComplete(String tag, String response);

    }

    public interface ErrorCallback {

        void onError(VolleyError error);

    }

    public Connector(Context mContext, LoadCallback mLoadCallback, ErrorCallback mErrorCallback) {
        this.mContext = mContext;
        this.mLoadCallback = mLoadCallback;
        this.mErrorCallback = mErrorCallback;
    }


    public void getRequest(final String tag, final String url) {
        Helper.writeToLog(url);
        if (isOnline(mContext)) {
            mQueue = Volley.newRequestQueue(mContext);
            StringRequest mStringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Helper.writeToLog(response);
                            mLoadCallback.onComplete(tag, response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    mErrorCallback.onError(error);
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    return mMap;
                }
            };
            mStringRequest.setTag(tag);
            mQueue.add(mStringRequest);
        } else {
            mErrorCallback.onError(new NoConnectionError());
        }


    }

    public void cancelAllRequests(final String tag) {
        if (mQueue != null) {
            mQueue.cancelAll(tag);
        }
    }


    private static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnectedOrConnecting();
        } else {
            return false;
        }
    }


    public void setMap(Map<String, String> mMap) {
        this.mMap = mMap;
    }

}
