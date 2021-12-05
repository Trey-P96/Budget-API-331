package io.none.model;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.Document;
import org.bson.codecs.pojo.annotations.BsonProperty;

@MongoEntity(collection = "projects-user-unions", database = "myFirstDatabase")
public class ProjectUserUnion extends PanacheMongoEntity {
    @BsonProperty("projectId")
    String projectId;

    @BsonProperty("userId")
    String userId;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public static ProjectUserUnion findByAssociations(String projectId, String userId) {
        Document document = new Document();
        document.put("projectId", projectId);
        document.put("userId", userId);
        return find(document).singleResult();
    }
}