package io.none.resource;

import io.none.model.*;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {
    public HashSet<String> loggedIn = new HashSet<>();

    @GET
    public Response getAllLogins() {
        return Response.ok(new ArrayList<>(loggedIn)).build();
    }

    @POST
    public Response login(@RequestBody AuthBody body) {
        User user = User.find("username", body.username).singleResult();

        if (user == null) return Response.status(Response.Status.NOT_FOUND).build();

        if (!Objects.equals(user.getUserPassword(), body.password)) {
            return Response.status(401, "Unauthorized").build();
        }

        loggedIn.add(user.id.toHexString());

        AccountData data = new AccountData();
        data.setUser(user);

        List<ProjectUserUnion> unions = ProjectUserUnion.find("userId", user.id.toHexString()).list();
        List<Project> projects = new ArrayList<>();
        for (final ProjectUserUnion union : unions) {
            Project project = Project.findById(new ObjectId(union.getProjectId()));
            if (project == null)
                union.delete();
            else
                projects.add(project);
        }
        data.setProjects(projects);

        return Response.ok(data).build();
    }


    @POST
    @Path("/{id}")
    public Response logout(@PathParam("id") String userId) {
        if (loggedIn.contains(userId)) {
            loggedIn.remove(userId);
            return Response.ok("Logged-Out").build();
        }
        return Response.ok("No-Action").build();
    }
}
