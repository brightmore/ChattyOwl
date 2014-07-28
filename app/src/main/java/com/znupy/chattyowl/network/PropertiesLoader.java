package com.znupy.chattyowl.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.znupy.chattyowl.R;
import com.znupy.chattyowl.activities.MainActivity;
import com.znupy.chattyowl.models.Property;

import java.util.List;

/**
 * Created by samok on 28/07/14.
 */
public class PropertiesLoader {
    private static final String TAG = PropertiesLoader.class.getSimpleName();

    private static final int SOCKET_TIMEOUT_MS = 30000;
    private static final int MAX_RETRIES = 0;

    private final Context context;
    private final RequestQueue requestQueue;

    private String serverUrl;

    public PropertiesLoader(Context context) {
        this.context = context;
        requestQueue = ((MainActivity)context).getRequestQueue();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        serverUrl = prefs.getString("properties_endpoint", context.getString(R.string.pref_default_endpoint));
    }

    public void get(final ResponseListener listener) {

        GsonRequest<PropertiesResponse> request = new GsonRequest<PropertiesResponse>(
                Request.Method.GET,
                serverUrl,
                null,
                PropertiesResponse.class,
                new Response.Listener<PropertiesResponse>() {

                    @Override
                    public void onResponse(PropertiesResponse response) {
                        Log.d(TAG, "onResponse: " + response);

                        if(listener == null) return;
                        if(response.success)
                            listener.onSuccess(response.properties);
                        else
                            listener.onError(response.message == null ?
                                    context.getString(R.string.response_unknown_error) :
                                    response.message);
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error);
                        if(listener == null) return;

                        listener.onError(error.getMessage());
                    }
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                SOCKET_TIMEOUT_MS,
                MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        requestQueue.add(request);
    }

    public interface ResponseListener {
        public void onSuccess(List<Property> properties);
        public void onError(String message);
    }
}
