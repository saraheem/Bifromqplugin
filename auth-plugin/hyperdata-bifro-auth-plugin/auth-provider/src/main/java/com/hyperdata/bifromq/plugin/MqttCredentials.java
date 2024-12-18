package com.hyperdata.bifromq.plugin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MqttCredentials {
    private String userName;
    private String password;
    private String clientId;

    
    public String getUserName() {
        return userName;
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public String getClientId() {
        return clientId;
    }


    public void setClientId(String clientId) {
        this.clientId = clientId;
    }


    public MqttCredentials(String userName, String password, String clientId) {
        this.userName = userName;
        this.password = password;
        this.clientId = clientId;
    }

    public String toJson(){
        String json = null;
        try {
            json = new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            
            e.printStackTrace();

        }

        return json;
    }

}
