package learn2crack.models;

/**
 * Created by otzur on 5/26/2015.
 */
public class WnMessageRowOption {

    private String name;
    private boolean selected;

    public WnMessageRowOption(String name) {
        this.name = name;
        selected = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
