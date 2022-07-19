package quarkus;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;

@ApplicationScoped
public class UserThingRepository implements PanacheRepository<UserThing> {
    public List<UserThing> findByUserId( Long userId) {
        Sort sort= Sort.ascending("orderSequence");
        return find( "userId = ?1", sort, userId).list();
    }
}
