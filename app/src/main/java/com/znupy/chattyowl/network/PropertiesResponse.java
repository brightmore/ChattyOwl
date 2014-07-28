package com.znupy.chattyowl.network;

import com.google.gson.annotations.SerializedName;
import com.znupy.chattyowl.models.Property;

import java.util.List;

/**
 * Created by samok on 28/07/14.
 */
public class PropertiesResponse extends BaseResponse {
    @SerializedName("data")
    public List<Property> properties;
}
