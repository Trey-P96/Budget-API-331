package io.none.model;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;

import java.util.Date;

@MongoEntity(collection = "project_Logs", database = "myFirstDatabase")
public class Purchase extends PanacheMongoEntity {
  public String userId;
  public String itemId;
  public String projectId;
  public Date datePurchased;
  public String name;
  public double purchasePrice;
}
