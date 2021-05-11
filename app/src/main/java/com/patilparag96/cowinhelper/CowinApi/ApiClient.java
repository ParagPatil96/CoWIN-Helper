package com.patilparag96.cowinhelper.CowinApi;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.patilparag96.cowinhelper.CowinApi.models.*;


import org.glassfish.jersey.client.ClientProperties;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.UriBuilder;


public class ApiClient {
   private static final URI DOMAIN = URI.create("https://cdn-api.co-vin.in/api");
   private static final String WEB_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.182 Safari/537.36";

   private static final String  STATE_LIST = "v2/admin/location/states";
   private static final String  DISTRICT_LIST = "v2/admin/location/districts/{state_id}";
   private static final String CENTER_LIST = "v2/appointment/sessions/public/calendarByDistrict";

   private static ObjectMapper MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
   private static final Client CLIENT = buildClient();

   public static StateList fetchStateList(){
      String url = getURL(STATE_LIST,  new HashMap<>()).toString();

      return fetchWithRetry(url, StateList.class);
   }

   public static DistrictList fetchDistrictList(String stateId){
      String url = getURL(DISTRICT_LIST.replace("{state_id}", stateId), new HashMap<>()).toString();

      return fetchWithRetry(url, DistrictList.class);
   }

   public static CenterList fetchCenters(String districtId, String date, String vaccineType){
      System.out.println("******** Fetching Centers ********");
      Map<String,String> params = new HashMap<>();
      params.put("district_id", districtId);
      if(date != null) {
         params.put("date", date);
      }

      if(vaccineType != null) {
         params.put("vaccine", vaccineType);
      }

      String url = getURL(CENTER_LIST, params).toString();

      return fetchWithRetry(url, CenterList.class);
   }

   private static URI getURL(String path, Map<String, String> params){
      UriBuilder builder = UriBuilder.fromUri(DOMAIN).path(path);

      for(Map.Entry<String, String> entry: params.entrySet()){
         builder.queryParam(entry.getKey(),entry.getValue());
      }

      return builder.build();
   }

   private static <T> T fetchWithRetry(String url, Class<T> type){
      StringBuilder failure =  new StringBuilder();
      for(int i=1; i<=5; i++){
         try {
            return CLIENT.target(url).request().header("user-agent", WEB_USER_AGENT).get(type);
         }catch (Exception e){
            failure.append("Failure ").append(i).append(": ").append(e.toString()).append("\n");
         }
      }
      throw new RuntimeException(failure.toString());
   }

   private static Client buildClient(){
     return ClientBuilder.newBuilder()
              .register(new JacksonJsonProvider(MAPPER))
              .property(ClientProperties.CONNECT_TIMEOUT, 5 * 1000)
              .property(ClientProperties.READ_TIMEOUT, 5 * 1000)
              .build();
   }
}
