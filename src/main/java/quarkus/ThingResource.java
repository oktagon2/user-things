package quarkus;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Path("/things")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ThingResource {
    
    @Inject
    ThingRepository thingRepository;

    @GET
    public List<Thing> allThings() {
        return thingRepository.listAll();
    }

    @GET
    @Path("/{name}")
    public Thing getThing( @PathParam("name") String name) {
        Thing thing= thingRepository.findByName(name);

        if(thing== null) {
            throw new WebApplicationException("Thing with " + name + " does not exist.", 404);
        }

        return thing;
    }

    @POST
    @Transactional
    public Response createThing(Thing thing) {
      if (thing.getId() != null) {
        throw new WebApplicationException("Id was invalidly set on request.", 400);
      }
  
      thingRepository.persist(thing);
      return Response.status(201).entity(thing).build();
    }
 
    @PUT
    @Path("{name}")
    @Transactional
    public Thing updateThing(@PathParam("name") String name, Thing thing) {
      Thing existingThing = getThing(name);
      existingThing.setName(thing.getName());
      return thing;
    }

    @DELETE
    @Path("{name}")
    @Transactional
    public Response deleteThing(@PathParam("name") String name) {
      Thing existingThing = getThing(name);
      thingRepository.delete(existingThing);
      return Response.noContent().build();
    }
    
    @Provider
    public static class ErrorMapper implements ExceptionMapper<Exception> {
        @Override
        public Response toResponse( Exception exception) {
            int code= 500;
            if( exception instanceof WebApplicationException) {
                code = ((WebApplicationException)exception).getResponse().getStatus();
            }

            JsonObjectBuilder entityBuilder= Json.createObjectBuilder()
                .add("exceptionType", exception.getClass().getName())
                .add("code",code);

            if( exception.getMessage()!= null) {
                entityBuilder.add("error", exception.getMessage());
            }

            return Response.status(code)
                .entity(entityBuilder.build())
                .build();
        }
    }
}