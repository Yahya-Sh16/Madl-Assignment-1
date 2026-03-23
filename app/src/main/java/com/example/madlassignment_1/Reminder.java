package com.example.madlassignment_1;

public class Reminder {
    private int id;
    private String title;
    private String description;
    private String time;
    private String location;
    private String priority;

    public Reminder(int id, String title, String description, String time, String location, String priority) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.time = time;
        this.location = location;
        this.priority = priority;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getTime() { return time; }
    public String getLocation() { return location; }
    public String getPriority() { return priority; }
}
