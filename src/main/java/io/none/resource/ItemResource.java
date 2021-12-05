package io.none.resource;

import io.none.model.Item;
import io.none.model.Pagination;
import io.quarkus.mongodb.panache.PanacheQuery;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ItemResource {

  @GET
  @Path("/{id}")
  public Response getItem(@PathParam("id") String id) {
    Item item = Item.findById(new ObjectId(id));
    if (item == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    return Response.ok(item).build();
  }

  @GET
  public Response getItems(
          @QueryParam("count") @DefaultValue("50") int count,
          @QueryParam("page") @DefaultValue("0") int page
  ) {
    PanacheQuery<Item> query = Item.findAll().page(page, count);
    Pagination<Item> data = new Pagination<>();
    data.setData(query.list());
    data.setHasNextPage(query.hasNextPage());
    data.setHasPreviousPage(query.hasPreviousPage());
    data.setPageSize(count);
    data.setPage(page);
    return Response.ok(data).build();
  }

  @POST
  public Response createItem(@RequestBody Item item) {
    item.persist();
    return Response.ok(item).build();
  }

  @PATCH
  public Response updateItem(@RequestBody Item item) {
    item.update();
    return Response.ok(item).build();
  }

  @DELETE
  @Path("/{id}")
  public Response deleteItem(@PathParam("id") String id) {
    Item item = Item.findById(new ObjectId(id));
    if (item == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    item.delete();
    return Response.ok().build();
  }
}
