open module com.pliki.leagueofpython {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    exports com.pliki.leagueofpython;
    exports com.pliki.leagueofpython.controllers;
    requires org.json;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

}