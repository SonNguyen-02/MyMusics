package com.snnc_993.mymusic.service;

import static com.snnc_993.mymusic.config.Config.BASE_API;

public class APIService {

    public static DataService getService(){
        return APIRetrofitClient.getClient(BASE_API).create(DataService.class);
    }
}
