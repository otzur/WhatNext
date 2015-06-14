package learn2crack.chat;

/**
 * Created by otzur on 5/26/2015.
 */
public class WnOption {

    private String name;
    private boolean selected;

    public WnOption(String name) {
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
