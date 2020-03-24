package com.stubhub.messaging.networkInvoke.brazeModel;

public class TestObj {
    public String pin = "test_pin_id";
    public String first_name = "Alex";
    public alert_obj alert_obj = new alert_obj();


    public class alert_obj{
        public String id = "msg01";
        public String msg = "from backend";
    }
}
