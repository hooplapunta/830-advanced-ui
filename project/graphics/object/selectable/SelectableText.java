package graphics.object.selectable;

import java.awt.Color;
import java.awt.Font;

import constraint.Constraint;
import constraint.NoConstraint;
import graphics.object.Text;

public class SelectableText extends Text implements SelectableGraphicalObject {
    /**
     * SelectableText class: selectable Texts
     */
    private boolean interimSelected = false;
    private boolean selected = false;

    private Constraint<Boolean> interimSelectedConstraint = new NoConstraint<>();
    private Constraint<Boolean> selectedConstraint = new NoConstraint<>();

    /**
     * Constructors
     */
    public SelectableText(String text, int x, int y, Font font, Color color) {
        super(text, x, y, font, color);
    }

    public SelectableText(String text, int x, int y) {
        super(text, x, y);
    }

    public SelectableText(String text) {
        super(text);
    }

    public SelectableText() {
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