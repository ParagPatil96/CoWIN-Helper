package com.patilparag96.cowinhelper.CowinApi.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateList {
    public List<State> states ;

    public static class State{
        public String state_id;
        public String state_name;
    }

    public Map<String,String> idToname(){
        Map<String,String> results =  new HashMap<>();
        for(State state: states){
            results.put(state.state_id, state.state_name);
        }

        return results;
    }
}