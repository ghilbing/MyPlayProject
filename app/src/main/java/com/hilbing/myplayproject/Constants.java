package com.hilbing.myplayproject;



public class Constants {
    public interface ACTION{
        public static String MAIN_ACTION = "com.hilbing.myplayproject.action.main";
        public static String PREV_ACTION = "com.hilbing.myplayproject.action.prev";
        public static String PLAY_ACTION = "com.hilbing.myplayproject.action.play";
        public static String NEXT_ACTION = "com.hilbing.myplayproject.action.next";
        public static String STARTFOREGROUND_ACTION = "com.hilbing.myplayproject.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "com.hilbing.myplayproject.action.stopforeground";
        }

    public interface NOTIFICATION_ID{
        public static int FOREGROUND_SERVICE = 101;
    }

}

