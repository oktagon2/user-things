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

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    
    @Inject
    UserRepository userRepository;

    @GET
    public List<User> allUsers() {
        return userRepository.listAll();
    }

    @GET
    @Path("/{name}")
    public User getUser( @PathParam("name") String name) {
        User user= userRepository.findByName(name);

        if(user== null) {
            throw new WebApplicationException("User with " + name + " does not exist.", 404);
        }

        return user;
    }

    @POST
    @Transactional
    public Response createUser(User user) {
      if (user.getId() != null) {
        throw new WebApplicationException("Id was invalidly set on request.", 400);
      }
  
      userRepository.persist(user);
      return Response.status(201).entity(user).build();
    }
 
    @PUT
    @Path("{name}")
    @Transactional
    public User updateUser(@PathParam("name") String name, User user) {
      User existingUser = getUser(name);
      existingUser.setName(user.getName());
      return user;
    }

    @DELETE
    @Path("{name}")
    @Transactional
    public Response deleteUser(@PathParam("name") String name) {
      User existingUser = getUser(name);
      userRepository.delete(existingUser);
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