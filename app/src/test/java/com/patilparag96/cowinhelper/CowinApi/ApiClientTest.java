package com.patilparag96.cowinhelper.CowinApi;

import com.patilparag96.cowinhelper.CowinApi.models.DistrictList;
import com.patilparag96.cowinhelper.CowinApi.models.StateList;

import org.junit.Test;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ApiClientTest {
    @Test
    public void fetchStateList_shouldFetchStates(){
        StateList list = ApiClient.fetchStateList();
        System.out.println(list.states);
    }

    @Test
    public void fetchDistrictList_shouldFetchDistricts(){
        DistrictList list = ApiClient.fetchDistrictList("21");
        System.out.println(list.districts);
    }
}