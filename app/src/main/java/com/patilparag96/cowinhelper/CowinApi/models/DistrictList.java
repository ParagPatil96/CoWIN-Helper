package com.patilparag96.cowinhelper.CowinApi.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DistrictList {
    public List<District> districts ;

    public static class District{
        public String district_id;
        public String district_name;
    }

    public Map<String,String> idToName(){
        Map<String,String> results =  new HashMap<>();
        for(District district: districts){
            results.put(district.district_id, district.district_name);
        }

        return results;
    }
}