package learn2crack.models;

/**
 * Created by samzaleg on 8/23/2015.
 */
public class WnMessageResultRaw {

    private String name;
    private boolean matched;

    public WnMessageResultRaw(String name) {
        this.name = name;
        matched = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }
}