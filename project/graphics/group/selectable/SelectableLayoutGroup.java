package graphics.group.selectable;

import constraint.Constraint;
import constraint.NoConstraint;
import graphics.group.LayoutGroup;

public class SelectableLayoutGroup extends LayoutGroup implements SelectableGroup {
    /**
     * SelectableLayoutGroup class: a selectable LayoutGroup
     * 
     * Automatically places its children in a certain layout
     * Options include horizontal, vertical and grid layouts
     */
    private boolean interimSelected;
    private boolean selected;

    private Constraint<Boolean> interimSelectedConstraint = new NoConstraint<>();
    private Constraint<Boolean> selectedConstraint = new NoConstraint<>();

    /**
     * Constructors
     */
    public SelectableLayoutGroup(int x, int y, int width, int height, int layout, int offset) {
        super(x, y, width, height, layout, offset);
    }

    public SelectableLayoutGroup(int x, int y, int width, int height, int layout, int offset,
            int nRows, int nColumns) {
        super(x, y, width, height, layout, offset, nRows, nColumns);
    }

    public SelectableLayoutGroup() {
        super();
    }

    /**
     * Getters, setters and "users"
     */
    public boolean isInterimSelected() {
        if (interimSelectedConstraint.isConstrained()) {
            this.interimSelected = interimSelectedConstraint.evaluate();
        }
        return this.interimSelected;
    }

    public void setInterimSelected(boolean interimSelected) {
        if (this.interimSelected != interimSelected) {
            if (!interimSelectedConstraint.isConstrained()) {
                this.interimSelected = interimSelected;
                interimSelectedConstraint.notifyValueChange(false);
            } else if (interimSelectedConstraint.hasCycle()) {
                interimSelectedConstraint.setValue(interimSelected);
                interimSelectedConstraint.notifyValueChange(false);
            }
        }
    }

    public void setInterimSelected(Constraint<Boolean> constraint) {
        interimSelectedConstraint.replaceWithConstraint(constraint);
        interimSelectedConstraint = constraint;
        interimSelectedConstraint.setValue(this.interimSelected);
        interimSelectedConstraint.notifyValueChange(true);
    }

    public Constraint<Boolean> useInterimSelected() {
        return this.interimSelectedConstraint;
    }

    public boolean isSelected() {
        if (selectedConstraint.isConstrained()) {
            this.selected = selectedConstraint.evaluate();
        }
        return this.selected;
    }

    public void setSelected(boolean selected) {
        if (this.selected != selected) {
            if (!selectedConstraint.isConstrained()) {
                this.selected = selected;
                selectedConstraint.notifyValueChange(false);
            } else if (selectedConstraint.hasCycle()) {
                selectedConstraint.setValue(selected);
                selectedConstraint.notifyValueChange(false);
            }
        }
    }

    public void setSelected(Constraint<Boolean> constraint) {
        selectedConstraint.replaceWithConstraint(constraint);
        selectedConstraint = constraint;
        selectedConstraint.setValue(this.selected);
        selectedConstraint.notifyValueChange(true);
    }

    public Constraint<Boolean> useSelected() {
        return this.selectedConstraint;
    }
}