package com.patilparag96.cowinhelper.CowinApi.models;

import java.util.List;

public class CenterList {
    public List<Center> centers ;

    public static class Center{
        public Integer center_id;
        public Integer pincode;

        public String name;
        public String address;
        public String state_name;
        public String district_name;
        public String block_name;
        public String fee_type;

        public List<Session> sessions;

        public String getNotification() {
            return "[\n" +
                    "Name: " + name + "\n" +
                    "Pincode: " + pincode + "\n" +
                    "Address: " + address+ "\n" +
                    "District: " + district_name + "\n" +
                    "Fee Type: " + fee_type+ "\n"+
                    "]\n";
        }
    }

    public static class Session{
        public String date;
        public Integer available_capacity;
        public String min_age_limit;
        public String vaccine;
        public List<String> slots;
    }

    public enum Vaccine{
        COVISHIELD,
        COVAXIN,
    }
}