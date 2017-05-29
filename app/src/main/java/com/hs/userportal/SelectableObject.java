package com.hs.userportal;

/**
 * Created by ravimathpal on 17/04/17.
 */

public class SelectableObject {

    private Object object;
    private boolean selected;

    public SelectableObject(Object object, boolean selected) {
        this.object = object;
        this.selected = selected;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
