package com.znupy.chattyowl.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.znupy.chattyowl.R;
import com.znupy.chattyowl.activities.MainActivity;
import com.znupy.chattyowl.models.Property;
import com.znupy.chattyowl.network.PropertiesLoader;

import java.util.List;

public class PropertiesWidget extends FrameLayout {
    private static final String TAG = VoiceWidget.class.getSimpleName();

    private ListView listView;
    private PropertiesAdapter propertiesAdapter;

    private RequestQueue requestQueue;
    private PropertiesLoader propertiesLoader;

    public PropertiesWidget(Context context) {
        super(context);
        build(context);
    }

    public PropertiesWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        build(context);
    }

    private void build(Context context) {
        addView(View.inflate(context, R.layout.widget_properties, null));

        if(isInEditMode()) return;

        listView = (ListView)findViewById(R.id.properties_list);

        setVisibility(View.GONE);

        requestQueue = ((MainActivity)context).getRequestQueue();
        propertiesLoader = new PropertiesLoader(context);
        loadProperties();
    }

    private void loadProperties() {
        propertiesLoader.get(new PropertiesLoader.ResponseListener() {
            @Override
            public void onSuccess(List<Property> properties) {
                propertiesAdapter = new PropertiesAdapter(getContext(), properties);
                listView.setAdapter(propertiesAdapter);
                PropertiesWidget.this.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(String message) {

            }
        });
    }


    public static class PropertiesAdapter extends ArrayAdapter<Property> {
        public PropertiesAdapter(Context context, List<Property> items) {
            super(context, 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;

            if (v == null) {
                LayoutInflater vi = LayoutInflater.from(getContext());
                v = vi.inflate(R.layout.row_property, null);

            }

            Property p = getItem(position);

            if (p != null) {
                TextView name = (TextView) v.findViewById(R.id.name);
                TextView value = (TextView) v.findViewById(R.id.value);

                name.setText(p.getName());
                value.setText(p.getValue());
            }

            return v;

        }
    }
}
