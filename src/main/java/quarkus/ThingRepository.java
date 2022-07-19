package quarkus;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class ThingRepository implements PanacheRepository<Thing> {
    public Thing findByName( String name) {
        return find( "LOWER(name) = ?1", name.toLowerCase() ).firstResult();
    }
}
