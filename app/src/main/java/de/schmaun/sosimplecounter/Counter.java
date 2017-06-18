package de.schmaun.sosimplecounter;

public class Counter {
    public String name = "";
    public int year = 0;
    public int month = 0;
    public int day = 0;

    public String font = "fonts/Bukhari Script.ttf";

    public Counter() {}

    public Counter(String name, int year, int month, int day) {
        this.name = name;
        this.year = year;
        this.month = month;
        this.day = day;
    }
}
