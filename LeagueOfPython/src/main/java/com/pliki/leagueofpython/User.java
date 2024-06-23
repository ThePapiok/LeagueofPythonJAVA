package com.pliki.leagueofpython;

public class User {
    private static Long Id;
    private static String Uprawienie;
    private static String Ograniczenie;

    public static void setId(Long newId){
        Id=newId;

    }
    public static Long getId(){
        return Id;
    }

    public static String getUprawienie() {
        return Uprawienie;
    }

    public static void setUprawienie(String uprawienie) {
        Uprawienie = uprawienie;
    }

    public static String getOgraniczenie() {
        return Ograniczenie;
    }

    public static void setOgraniczenia(String ograniczenie) {
        Ograniczenie = ograniczenie;
    }
}
