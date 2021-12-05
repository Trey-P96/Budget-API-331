package io.none.resource;

import io.none.model.Pagination;
import io.none.model.ProjectUserUnion;
import io.none.model.User;
import io.quarkus.mongodb.panache.PanacheQuery;
import org.bson.types.ObjectId;

import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
  @GET
  @Transactional
  @Path("/{userId}")
  public Response getUser(@PathParam("userId") String userId) {
    User user = User.findById(new ObjectId(userId));
    return user == null ? Response.status(Response.Status.NOT_FOUND).build() : Response.ok(user).build();
  }

  @GET()
  public Response getUsers(
      @QueryParam("count")
      @DefaultValue("25")
          int count,
      @QueryParam("page")
      @DefaultValue("0")
          int page,
      @QueryParam("projectId") String projectId
  ) {
    Pagination<User> pagination = new Pagination<>();
    pagination.setPageSize(count);
    pagination.setPage(page);

    if (projectId != null) {
      PanacheQuery<ProjectUserUnion> query = ProjectUserUnion.find("projectId", projectId).page(page, count);
      List<ProjectUserUnion> projectUserUnions = query.list();
      List<User> users = new ArrayList<>();
      for (ProjectUserUnion projectUserUnion : projectUserUnions) {
        User user = User.findById(new ObjectId(projectUserUnion.getUserId()));
        Objects.requireNonNull(user);
        users.add(user);
      }
      pagination.setData(users);
      pagination.setHasNextPage(query.hasNextPage());
      pagination.setHasPreviousPage(query.hasPreviousPage());
    } else {
      PanacheQuery<User> query = User.findAll().page(page, count);
      pagination.setData(query.list());
      pagination.setHasNextPage(query.hasNextPage());
      pagination.setHasPreviousPage(query.hasPreviousPage());
    }

    return Response.ok(pagination).build();
  }

  @POST
  @Path("/purge")
  public Response purgeUsers() {
    User.delete("id = *");
    return Response.ok().build();
  }

  @POST
  @Transactional
  public Response createUser(User user) {
    if (User.find("username", user.getUserName()).firstResult() != null)
      return Response.status(400, "Username already exists").build();

    user.persist();
    return Response.ok(user).build();
  }

  @DELETE
  @Transactional
  @Path("/{userId}")
  public Response deleteUser(@PathParam("userId") String userId) {
    User user = User.findById(new ObjectId(userId));
    if (user == null) return Response.status(Response.Status.NOT_FOUND).build();

    user.delete();
    return Response.ok().build();
  }
}
