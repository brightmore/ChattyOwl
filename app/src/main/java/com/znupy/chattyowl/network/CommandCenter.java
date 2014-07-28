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
import com.android.volley.toolbox.Volley;
import com.znupy.chattyowl.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by samok on 27/07/14.
 */
public class CommandCenter {
    private static final String TAG = CommandCenter.class.getSimpleName();
    private static final int SOCKET_TIMEOUT_MS = 30000;
    private static final int MAX_RETRIES = 0;

    private final Context context;
    private final RequestQueue requestQueue;

    private String serverUrl;

    public CommandCenter(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        serverUrl = prefs.getString("endpoint", context.getString(R.string.pref_default_endpoint));
    }

    public void post(final String command, final ResponseListener listener) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("command", command);

        GsonRequest<BaseResponse> request = new GsonRequest<BaseResponse>(
                Request.Method.POST,
                serverUrl,
                params,
                BaseResponse.class,
                new Response.Listener<BaseResponse>() {

                    @Override
                    public void onResponse(BaseResponse response) {
                        Log.d(TAG, "onResponse: " + response);

                        if(listener == null) return;
                        if(response.success)
                            listener.onSuccess();
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
        public void onSuccess();
        public void onError(String message);
    }
}
