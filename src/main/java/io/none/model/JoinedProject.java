package io.none.model;

import org.bson.types.ObjectId;

public class JoinedProject {
  Project project;
  ProjectUserUnion union;
  public User getUser() {
    return User.findById(new ObjectId(union.getUserId()));
  }

  public Project getProject() {
    return project;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public ProjectUserUnion getUnion() {
    return union;
  }

  public void setUnion(ProjectUserUnion union) {
    this.union = union;
  }
}