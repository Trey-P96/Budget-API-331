package io.none.resource;

import io.none.model.*;
import io.quarkus.mongodb.panache.PanacheQuery;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@Path("/purchases")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PurchaseResource {
  @GET
  public Response listPurchases(
      @QueryParam("projectId") String projectId,
      @QueryParam("count") @DefaultValue("100") int count,
      @QueryParam("page") @DefaultValue("0") int offset
  ) {
    final Document query = new Document();

    if (projectId != null) query.put("projectId", projectId);

    PanacheQuery<Purchase> purchases = Purchase.find(query).page(offset, count);

    final Pagination<Purchase> pagination = new Pagination<>();
    pagination.setPageSize(count);
    pagination.setPage(offset);
    pagination.setData(purchases.list());
    pagination.setHasNextPage(purchases.hasNextPage());
    pagination.setHasPreviousPage(purchases.hasPreviousPage());

    return Response.ok(pagination).build();
  }

  @DELETE
  @Path("/{id}")
  public Response deletePurchase(@PathParam("id") String id) {
    return Response.status(Purchase.deleteById(id) ? 200 : 400).build();
  }

  // Create a post request to create a new purchase
  @POST
  public Response createPurchase(@RequestBody Purchase purchase) {
    Project project = Project.findById(purchase.projectId);
    Item item = Item.findById(purchase.userId);

    if (project == null || item == null)
      return Response.status(404).build();

    purchase.name = item.getName();
    if (purchase.purchasePrice != item.getPrice())
      purchase.purchasePrice = item.getPrice();

    if (project.getHasBudget()) {
      if (project.getBudget() < purchase.purchasePrice)
        return Response.status(400, "Naughty naughty").build();

      project.setBudget(project.getBudget() - purchase.purchasePrice);
    }

    purchase.persist();
    return Response.ok(purchase).build();
  }

  Logger logger = Logger.getLogger(PurchaseResource.class.getName());

  @POST
  @Path("/many")
  public Response createPurchases(@RequestBody List<Purchase> purchases) {
    if (purchases.isEmpty())
      return Response.status(Response.Status.BAD_REQUEST).build();

    int spent = 0;
    String projectId = purchases.get(0).projectId;
    logger.info("Project id is " + projectId);
    Project project = Project.findById(new ObjectId(projectId));
    logger.info("Project: " + project);
    if (project == null) return Response.status(Response.Status.NOT_FOUND).build();

    for (final Purchase purchase : purchases) {
      if (!Objects.equals(projectId, purchase.projectId))
        return Response.status(Response.Status.BAD_REQUEST).build();

      Item item = Item.findById(new ObjectId(purchase.itemId));

      logger.info("Item is null: " + (item == null));
      if (item == null) return Response.status(Response.Status.NOT_FOUND).build();

      purchase.name = item.getName();
      purchase.purchasePrice = item.getPrice();

      if (project.getHasBudget()) {
        if (project.getBudget() < purchase.purchasePrice)
          return Response.status(Response.Status.BAD_REQUEST).build();

        project.setBudget(project.getBudget() - purchase.purchasePrice);
        spent += purchase.purchasePrice;
      }

      purchase.persist();
    }
    project.update();

    PurchaseData data = new PurchaseData();
    data.purchased = purchases;
    data.remaining = project.getBudget() - spent;
    data.budget = project.getBudget();
    data.spent = spent;

    return Response.ok(data).build();
  }
}
