package quarkus;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class UserThing {

    @Id
    private Long userId;
    @Id
    private Long thingId;
    
    private Long orderSequence;

    public Long getUserId() {
        return userId;
    }

    public void setUserId( Long userId) {
        this.userId= userId;
    }

    public Long getThingId() {
        return thingId;
    }

    public void setThingId( Long thingId) {
        this.thingId= thingId;
    }

    public Long getOrderSequence() {
        return orderSequence;
    }

    public void setOrderSequence( Long orderSequence) {
        this.orderSequence= orderSequence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserThing userThing = (UserThing) o;
        return userId.equals(userThing.userId) &&
            thingId.equals(userThing.thingId) &&
            orderSequence.equals(userThing.orderSequence);
    }

    @Override
    public int hashCode() {
        return Objects.hash( userId, orderSequence);
    }
}
