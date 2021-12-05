package io.none.model;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.codecs.pojo.annotations.BsonProperty;

@MongoEntity(database = "myFirstDatabase", collection = "projects")
public class Project extends PanacheMongoEntity {
    @BsonProperty("name")
    private String name;

    @BsonProperty("budget")
    private double budget;

    @BsonProperty("hasBudget")
    private boolean hasBudget;

    @BsonProperty("ownerId")
    String ownerId;

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = Double.parseDouble(String.format("%.2f", budget));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getHasBudget() {
        return hasBudget;
    }

    public void setHasBudget(boolean hasBudget) {
        this.hasBudget = hasBudget;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}
