package com.example.eventy.eventy;

public class Myuser {
    public static boolean isorg = false;
    public static String name ="";
    public static String Uid = "";
    public static String PhotoURL= "";
    public static Event myevent= new Event();

    public static void signout()  {
        isorg = false;
        name ="";
        Uid = "";
        PhotoURL= "";
    }
}
