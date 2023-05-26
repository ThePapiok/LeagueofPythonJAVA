package com.pliki.leagueofpython;

public class User {
    private static Long Id;

    public static void setId(Long newId){
        Id=newId;

    }
    public static Long getId(){
        return Id;
    }
}
