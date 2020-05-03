package ui.toolkit.widget;

import java.awt.Color;
import java.util.List;

import ui.toolkit.behavior.BehaviorEvent;
import ui.toolkit.behavior.ChoiceBehavior;
import ui.toolkit.constraint.Constraint;
import ui.toolkit.graphics.group.LayoutGroup;
import ui.toolkit.graphics.group.SimpleGroup;
import ui.toolkit.graphics.object.FilledEllipse;
import ui.toolkit.graphics.object.GraphicalObject;
import ui.toolkit.graphics.object.selectable.SelectableEllipse;
import ui.toolkit.graphics.object.selectable.SelectableGraphicalObject;

public class RadioButtonPanel extends Widget<RadioButton> {
    private ChoiceBehavior choiceBehavior;

    /**
     * RadioButtonPanel constructor
     * 
     * @param x
     * @param y
     * @param layout
     * @param offset
     */
    public RadioButtonPanel(int x, int y, int layout, int offset) {
        if (layout == NO_LAYOUT) {
            this.widget = new SimpleGroup(x, y, 0, 0);
        } else {
            this.widget = new LayoutGroup(x, y, 0, 0, layout, offset);
        }

        this.widget.addBehavior(
            choiceBehavior = new ChoiceBehavior(ChoiceBehavior.SINGLE, true) {
                @Override
                public boolean stop(BehaviorEvent event) {
                    boolean eventConsumed = super.stop(event);
                    List<SelectableGraphicalObject> selection = getSelection();
                    RadioButton selected = selection.isEmpty() ? null : (RadioButton) selection.get(0);
                    if (value != selected) {
                        setValue(selected);
                        callback.update(selected);
                    }
                    return eventConsumed;
                }
            }
        );

        choiceBehavior.setRoot(this);
    }

    public RadioButtonPanel(int x, int y) {
        this(x, y, VERTICAL_LAYOUT, 5);
    }

    public RadioButtonPanel() {
        this(0, 0);
    }

    /**
     * Override addChild to add constraints
     */
    @Override
    public Widget<RadioButton> addChild(GraphicalObject child) {
        super.addChild(child);

        SelectableEllipse o = ((RadioButton) child).getOption();
        FilledEllipse i = ((RadioButton) child).getIndicator();
        i.setColor(new Constraint<Color>(o.useInterimSelected(), o.useSelected()) {
            public Color getValue() {
                if (o.isSelected()) {
                    return o.isInterimSelected() ? Color.BLUE : Color.BLACK;
                } else {
                    return o.isInterimSelected() ? Color.LIGHT_GRAY : Color.WHITE;
                }
            }
        });
        return this;
    }

    @Override
    public RadioButtonPanel addChildren(GraphicalObject... children) {
        for (GraphicalObject child : children) {
            addChild(child);
        }
        return this;
    }

    public RadioButtonPanel setSelection(String type) {
        choiceBehavior.select(type);
        return this;
    }
}