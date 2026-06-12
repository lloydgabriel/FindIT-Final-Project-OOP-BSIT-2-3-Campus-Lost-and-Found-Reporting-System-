package com.example.findit.model;

public class ItemReport {
    private final int id;
    private final String type;
    private final String itemName;
    private final String category;
    private final String date;
    private final String location;
    private final String reportedBy;
    private final String contact;
    private final String description;
    private final String imagePath;

    public ItemReport(int id, String type, String itemName, String category, String date,
                      String location, String reportedBy, String contact, String description,
                      String imagePath) {
        this.id = id;
        this.type = type;
        this.itemName = itemName;
        this.category = category;
        this.date = date;
        this.location = location;
        this.reportedBy = reportedBy;
        this.contact = contact;
        this.description = description;
        this.imagePath = imagePath;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getItemName() {
        return itemName;
    }

    public String getCategory() {
        return category;
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public String getContact() {
        return contact;
    }

    public String getDescription() {
        return description;
    }

    public String getImagePath() {
        return imagePath;
    }
}
