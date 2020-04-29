package ui.talk;

import com.google.cloud.dialogflow.v2.QueryResult;
import com.google.protobuf.StringValue;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;

import ui.toolkit.behavior.BehaviorEvent;
import ui.toolkit.behavior.NewRectBehavior;
import ui.toolkit.graphics.group.Group;
import ui.toolkit.graphics.object.FilledEllipse;
import ui.toolkit.graphics.object.FilledRect;
import ui.toolkit.graphics.object.GraphicalObject;
import ui.toolkit.graphics.object.Text;
import ui.toolkit.widget.RadioButtonPanel;

import java.awt.*;
import java.util.List;

import static ui.toolkit.behavior.BehaviorEvent.*;


class HandleResponse {
    public static void handle(QueryResult queryResult, Group panel) {
        String intentName = queryResult.getIntent().getDisplayName();

        if (intentName.equals(Intent.INITIALIZATION)) {
            Struct params = queryResult.getParameters();

            Value newObject = params.getFieldsOrDefault(Intent.InitializationParams.GRAPHICAL_OBJECT, null);
            if (newObject != null) { // DialogFlow will ensure that this param is provided
                switch (newObject.getStringValue()) {

                    case Entity.GraphicalObjectType.FILLED_RECT: {
                        int width = (int) params.getFieldsOrDefault(Intent.InitializationParams.WIDTH, null)
                                .getNumberValue();
                        int height = (int) params.getFieldsOrDefault(Intent.InitializationParams.HEIGHT, null)
                                .getNumberValue();
                        String color = params.getFieldsOrDefault(Intent.InitializationParams.COLOR, null)
                                .getStringValue();
                        System.out.println("Drawing FilledRect: " + width + " " + height + " " + color);
                        panel.addChild(new FilledRect(20, 20, width, height, Entity.stringToColor.get(color)));
                        break;
                    }

                    case Entity.GraphicalObjectType.FILLED_ELLIPSE: {
                        int width = (int) params.getFieldsOrDefault(Intent.InitializationParams.WIDTH, null)
                                .getNumberValue();
                        int height = (int) params.getFieldsOrDefault(Intent.InitializationParams.HEIGHT, null)
                                .getNumberValue();
                        String color = params.getFieldsOrDefault(Intent.InitializationParams.COLOR, null)
                                .getStringValue();
                        System.out.println("Drawing FilledEllipse: " + width + " " + height + " " + color);
                        panel.addChild(new FilledEllipse(20, 20, width, height, Entity.stringToColor.get(color)));
                        break;
                    }

                    case Entity.GraphicalObjectType.TEXT: {
                        String text = params.getFieldsOrDefault(Intent.InitializationParams.TEXT, null)
                                .getStringValue();
                        String color = params.getFieldsOrDefault(Intent.InitializationParams.COLOR, null)
                                .getStringValue();
                        System.out.println("Drawing Text: " + text + " " + color);
                        panel.addChild(new Text(text, 20,20, Text.DEFAULT_FONT, Entity.stringToColor.get(color)));
                        break;
                    }

                    case Entity.GraphicalObjectType.RADIOBUTTON_PANEL: {
                        List<Value> values = params.getFieldsOrDefault(Intent.InitializationParams.OPTIONS, null)
                                .getListValue().getValuesList();
                        String color = params.getFieldsOrDefault(Intent.InitializationParams.COLOR, null)
                                .getStringValue();

                        System.out.println("Drawing RadioButton: " + values.size());
                        RadioButtonPanel radios = new RadioButtonPanel(20, 20);

                        for (Value v: values) {
                            String text = params.getFieldsOrDefault(Intent.InitializationParams.TEXT, null)
                                    .getStringValue();
                            radios.addChild(new Text(text, 0,0, Text.DEFAULT_FONT, Entity.stringToColor.get(color)));
                        }

                        panel.addChild(radios);
                        break;
                    }

                    default: {
                        // do nothing
                        break;
                    }
                }
            }
        } else if (intentName.equals((Intent.INTERACTION))){

            // check if using move/choice
            Struct params = queryResult.getParameters();

            Value behavior = params.getFieldsOrDefault(Intent.InteractionParams.BEHAVIOR, null);

            if (behavior != null) { // DialogFlow will ensure that this param is provided
                switch (behavior.getStringValue()) {

                    case "ChoiceBehavior": {
                        // existing behavior on radio button
                        // need to add constraint to circle 1
                        panel.getChildren();

                        // is there a callback method instead?


                        break;
                    }

                    case "MoveBehavior": {
                        // for the purposes of the demo, use the MoveBehavior
                        // already on the canvas, since we do not have a full play mode

                        break;
                    }

                    case "NewBehavior": {
                        // attach the new behavior to the base group
                        panel.addBehavior(new NewRectBehavior(NewRectBehavior.OUTLINE_RECT, Color.YELLOW, 2)
                                .setGroup(panel)
                                .setStartEvent(new BehaviorEvent(CONTROL_MODIFIER, LEFT_MOUSE_KEY, MOUSE_DOWN_ID))
                                .setStopEvent(new BehaviorEvent(ANY_MODIFIER, LEFT_MOUSE_KEY, MOUSE_UP_ID))
                                .setCancelEvent(new BehaviorEvent(NO_MODIFIER, 'z', KEY_UP_ID)));

                        // set priority

                        break;
                    }

                    default: {
                        break;
                    }

                }
            }


        } else {
            // fallback or chit chat
        }

        // all other intents don't need anything to be done

        String detectedText = queryResult.getQueryText();
        panel.addChild(new Text(detectedText));
    }
}