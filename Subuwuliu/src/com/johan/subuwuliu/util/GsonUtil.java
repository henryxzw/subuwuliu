package com.johan.subuwuliu.util;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

public class GsonUtil {

	public static <T> T jsonToObjct(String json, Class<T> clazz) {
		Gson gson = new Gson();
		return gson.fromJson(json, clazz);
	}
	
	public static String objectToJson(Object object) {
		Gson gson = new Gson();
		return gson.toJson(object);
	}
	
	public static String getJsonValue(String json, String key) {
		try {
			JSONObject object = new JSONObject(json);
			return object.getString(key);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
