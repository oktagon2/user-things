package quarkus;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Path("/userthings")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserThingResource {
    
    @Inject
    UserThingRepository userThingRepository;
    
    @Inject
    ThingRepository thingRepository;

    @GET
    public List<Thing> allByUserId( @QueryParam("userId") Long userId) {
        List<Thing> ret= new ArrayList<Thing>();
        for(UserThing userThing: userThingRepository.findByUserId(userId)) {
            ret.add( userThing.getThing());
        }
        return ret;
    }

    @PUT
    @Transactional
    public List<Thing> updateForUserId(@QueryParam("userId") Long userId, List<Long> thingIds) {
      List<UserThing> existingUserThings=  userThingRepository.findByUserId(userId);
      int existingSize= existingUserThings.size();
      int newSize= thingIds.size();

      for( int i= 0; (i< existingSize)||( i< newSize); i++) {
        if(( i< existingSize)&&( i< newSize)) {
            UserThing existingUserThing= existingUserThings.get( i);
            Long thingId= thingIds.get(i);
            existingUserThing.setThing( null);
            //existingUserThing.setThingId(thingId);
        }
        else {
            if( i< existingSize) {
                UserThing existingUserThing= existingUserThings.get( i);
                userThingRepository.delete( existingUserThing);
            }
            else {
                Long thingId= thingIds.get(i);
                UserThing newUserThing= new UserThing();
                newUserThing.setUserId(userId);
                //newUserThing.setThingId(thingId);
                newUserThing.setOrderSequence( Long.valueOf( i));
                userThingRepository.persist( newUserThing);
            }
        }
      }
      return allByUserId(userId);
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