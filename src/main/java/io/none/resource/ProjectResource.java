package io.none.resource;

import io.none.model.*;
import io.quarkus.mongodb.panache.PanacheQuery;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Path("/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProjectResource {
  @GET
  @Path("/{id}")
  public Response getProjectById(@PathParam("id") String id) {
    Project p = Project.findById(new ObjectId(id));
    if (p == null) return Response.status(Response.Status.NOT_FOUND).build();
    else return Response.ok(p).build();
  }

  @POST
  @Path("/{id}")
  public Response updateProject(@PathParam("id") String id, @RequestBody Project project) {
    Project p = Project.findById(new ObjectId(id));
    if (p == null) return Response.status(Response.Status.NOT_FOUND).build();

    project.update();

    return Response.ok(p).build();
  }

  @GET
  public Response getProjects(
      @QueryParam("userId") String userId,
      @QueryParam("count") @DefaultValue("25") int count,
      @QueryParam("page") @DefaultValue("0") int page
  ) {
    Document document = new Document();

    if (userId != null)
      document.append("userId", userId);

    final PanacheQuery<ProjectUserUnion> query = ProjectUserUnion.find(document).page(page, count);
    List<ProjectUserUnion> unions = query.list();
    List<Project> projects = new ArrayList<>();
    for (ProjectUserUnion union : unions) {
      Project project = Project.findById(new ObjectId(union.getProjectId()));
      projects.add(Objects.requireNonNull(project));
    }

    Pagination<Project> pagination = new Pagination<>();
    pagination.setData(projects);
    pagination.setPage(page);
    pagination.setPageSize(count);
    pagination.setHasNextPage(query.hasNextPage());
    pagination.setHasPreviousPage(query.hasPreviousPage());

    return Response.ok(pagination).build();
  }

  public static final Random random = new Random();

  @POST
  @Path("/{id}/make-me-rich")
  public Response makeMeRich(@PathParam("id") String id) {
    Project project = Project.findById(new ObjectId(id));
    if (project == null) return Response.status(Response.Status.NOT_FOUND).build();

    double amount;
    if (random.nextDouble() < 0.001)
      amount = 1000000.00;
    else
      amount = random.nextDouble() * 1000;

    project.setBudget(project.getBudget() + amount);
    project.update();

    return Response.ok(project).build();
  }

  @POST
  public Response createProject(@RequestBody Project project) {
    project.persist();

    ProjectUserUnion union = new ProjectUserUnion();
    union.setUserId(project.getOwnerId());
    union.setProjectId(project.id.toHexString());
    union.persist();

    JoinedProject joinedProject = new JoinedProject();
    joinedProject.setUnion(union);
    joinedProject.setProject(project);

    return Response.ok(joinedProject).build();
  }

  @DELETE
  @Path("/{id}")
  public Response deleteProject(@PathParam("id") String id) {
    Project project = Project.findById(new ObjectId(id));
    if (project == null) return Response.status(Response.Status.NOT_FOUND).build();

    ProjectUserUnion.delete(new Document("projectId", project.id.toHexString()));
    project.delete();
    return Response.ok().build();
  }

  @DELETE
  @Path("/unions/{id}")
  public Response deleteUnion(@PathParam("id") String id) {
    return Response.status(ProjectUserUnion.deleteById(new ObjectId(id)) ? Response.Status.OK : Response.Status.NOT_FOUND).build();
  }

  @DELETE
  @Path("/unions/{userId}/{projectId}")
  public Response deleteUnionByAssociations(
      @PathParam("userId") String userId,
      @PathParam("projectId") String projectId
  ) {
    ProjectUserUnion union = ProjectUserUnion.findByAssociations(userId, projectId);
    if (union == null) return Response.status(Response.Status.NOT_FOUND).build();
    union.delete();
    return Response.ok().build();
  }

  @GET
  @Path("/unions")
  public Response listUnions(
      @QueryParam("count") @DefaultValue("25") int count,
      @QueryParam("page") @DefaultValue("0") int page
  ) {
    return Response.ok(ProjectUserUnion.findAll().page(page, count).list()).build();
  }

  @POST
  @Path("/{projectId}/join/{username}")
  public Response joinProject(
      @PathParam("projectId") String projectId,
      @PathParam("username") String username
  ) {
    Project project = Project.findById(new ObjectId(projectId));
    User user = User.find("username", username).firstResult();

    if (user == null || project == null)
      return Response.status(Response.Status.NOT_FOUND).build();

    Document document = new Document();
    document.put("userId", user.id.toHexString());
    document.put("projectId", project.id.toHexString());

    ProjectUserUnion union = ProjectUserUnion.find(document).firstResult();

    if (union != null)
      return Response.status(Response.Status.BAD_REQUEST).build();

    union = new ProjectUserUnion();
    union.setUserId(user.id.toHexString());
    union.setProjectId(project.id.toHexString());
    union.persist();

    JoinedProject joinedProject = new JoinedProject();
    joinedProject.setUnion(union);
    joinedProject.setProject(project);

    return Response.ok(joinedProject).build();
  }
}
