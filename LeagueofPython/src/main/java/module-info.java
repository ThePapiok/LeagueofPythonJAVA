open module com.pliki.leagueofpython {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.persistence;
    requires java.sql;


    //opens com.pliki.leagueofpython to javafx.fxml;
    exports com.pliki.leagueofpython;
    exports com.pliki.leagueofpython.controllers;
    requires org.hibernate.orm.core;
    requires jbcrypt;

}