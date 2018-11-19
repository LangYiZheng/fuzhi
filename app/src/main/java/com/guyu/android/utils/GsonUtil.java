package com.guyu.android.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Type;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public class GsonUtil {
    public static Gson getGson(){
        return new GsonBuilder().serializeNulls().create();
    }

	/**
    *
    * 函数名称: parseData
    * 函数描述: 将json字符串转换为map
    * @param data
    * @return
    */
   public static Map<String, String> parseData(String data){
       Gson g = getGson();
       Map<String, String> map = g.fromJson(data, new TypeToken<Map<String, String>>() {}.getType());
       return map;
   }
   /**
    * 将Map转化为Json
    * 
    * @param map
    * @return String
    */
   public static <T> String mapToJson(Map<String, T> map) {
    Gson gson = getGson();
    String jsonStr = gson.toJson(map);
    return jsonStr;
   }
   
   /**
    * list转换成json
    * @param list
    * @return
    */
   public static String listToJson(List<?> list) {
	   Gson gson =getGson();
	   String jsonstring = gson.toJson(list);
	   return jsonstring;
   }
   
   /**
    * json转换成List
    * @param json
    * @param cls
    * @return
    */
   public static <T> ArrayList<T> jsonToList(String json, Class<T> cls) {
       Type type = new TypeToken<ArrayList<JsonObject>>(){}.getType();
       ArrayList<JsonObject> jsonObjs = new Gson().fromJson(json, type);// 反序列化出ArrayList<JsonObject>，
       ArrayList<T> listOfT = new ArrayList<T>();
       for (JsonObject jsonObj : jsonObjs) {
           listOfT.add(new Gson().fromJson(jsonObj, cls));
       }
       
       return listOfT;
   }

   /**
    * json转换成List<Map<String, T>>
    * @param gsonString
    * @return
    */
   public static <T> List<Map<String, T>> jsonToListMaps(
           String gsonString) {
       List<Map<String, T>> list = null;
       Gson gson = getGson();
       list = gson.fromJson(gsonString, new TypeToken<List<Map<String, T>>>() {
       }.getType());
       return list;
   }
}
