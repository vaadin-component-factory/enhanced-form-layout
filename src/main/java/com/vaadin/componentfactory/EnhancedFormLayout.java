package com.vaadin.componentfactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.FormItem;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep.LabelsPosition;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.shared.Registration;

import elemental.json.JsonArray;
import elemental.json.JsonObject;
import elemental.json.JsonValue;

@CssImport(value = "./styles/enhanced-form-item.css", themeFor = "vaadin-form-item")
public class EnhancedFormLayout extends FormLayout {

    private static final String MIN_WIDTH_JSON_KEY = "minWidth";
    private static final String COLUMNS_JSON_KEY = "columns";
    private static final String LABELS_POSITION_JSON_KEY = "labelsPosition";
    private String formItemLabelWidth;
    private String formItemRowSpacing;
    
    @Override
    public EnhancedFormItem addFormItem(Component field, String label) {
        return addFormItem(field, new Label(label));
    }

    @Override
    public EnhancedFormItem addFormItem(Component field, Component label) {
        EnhancedFormItem formItem = new EnhancedFormItem(field, label);
        add(formItem);
        return formItem;
    }

    /**
     * Get the list of {@link ResponsiveStep}s used to configure this layout.
     *
     * @see ResponsiveStep
     *
     * @return the list of {@link ResponsiveStep}s used to configure this layout
     */
    @Override
    public List<ResponsiveStep> getResponsiveSteps() {
        JsonArray stepsJsonArray = (JsonArray) getElement()
                .getPropertyRaw("responsiveSteps");
        if (stepsJsonArray == null) {
            return Collections.emptyList();
        }
        List<ResponsiveStep> steps = new ArrayList<>();
        for (int i = 0; i < stepsJsonArray.length(); i++) {
            JsonObject stepJson = stepsJsonArray.get(i);
            if (stepJson != null) {
                ResponsiveStep step = responsiveStepFromJson(stepJson);
                steps.add(step);
            }
        }
        return steps;
    }
    
    private ResponsiveStep responsiveStepFromJson(JsonObject stepJson) {
        String minWidth = stepJson.getString(MIN_WIDTH_JSON_KEY);
        int columns = (int) stepJson.getNumber(COLUMNS_JSON_KEY);
        String labelsPositionString = stepJson.hasKey(LABELS_POSITION_JSON_KEY) ? stepJson
                .getString(LABELS_POSITION_JSON_KEY) : "aside";
        LabelsPosition labelsPosition = null;
        if ("aside".equals(labelsPositionString)) {
            labelsPosition = LabelsPosition.ASIDE;
        } else if ("top".equals(labelsPositionString)) {
            labelsPosition = LabelsPosition.TOP;
        }
        return new ResponsiveStep(minWidth, columns, labelsPosition);
    }

    /**
     * This is a convenience API to set the width value subsequently
     * used by form items created after setting.  
     * 
     * @param width A CSS accepted width as string
     */
    public void setFormItemLabelWidth(String width) {
        formItemLabelWidth = width;
    }

    /**
     * This is a convenience API to set the row spacing value subsequently
     * used by form items created after setting.  
     * 
     * @param spacing A CSS accepted value as string
     */
    public void setFormItemRowSpacing(String spacing) {
        formItemRowSpacing = spacing;
    }

    /**
     * This is a convenience API to set the column spacing value used by
     * this form layout.  
     * 
     * @param spacing A CSS accepted value as string
     */
    public void setColSpacing(String spacing) {
        getStyle().set("--vaadin-form-layout-column-spacing", spacing);
    }    

    public class EnhancedFormItem extends FormItem {

        Registration listenerReg;
        HasText label = null;

        /**
         * Constructs a new EnhancedFormItem which inherits required status
         * indicator from the component wrapped in it.
         * 
         * @param comp
         *            The field component
         * @param label
         *            The label
         */
        public EnhancedFormItem(Component comp, Component label) {
            if (label instanceof HasText) {
                this.label = (HasText) label;                
            }
            add(comp);
            getElement().getStyle().set("--required-dot-opacity", "0");
            addToLabel(label);
            if (comp instanceof HasValue) {
                HasValue field = (HasValue) comp;
                field.addValueChangeListener(event -> {
                    if (event.getValue() != null) {
                        getElement().getStyle().set("--required-dot-opacity",
                                "0");
                    }
                });
            }
            listenerReg = comp.getElement()
                    .addPropertyChangeListener("required", event -> {
                        if (((HasValue) comp).isRequiredIndicatorVisible()) {
                            getElement().getStyle()
                                    .set("--required-dot-opacity", "1");
                        } else {
                            getElement().getStyle()
                                    .set("--required-dot-opacity", "0");
                        }
                    });
            if (formItemLabelWidth != null) setLabelWidth(formItemLabelWidth);
            if (formItemRowSpacing != null) setRowSpacing(formItemRowSpacing);
        }

        /**
         * Sets the label text if label component is instance of HasText.
         * 
         * @param text Label text as String
         */
        public void setLabel(String text) {
            if (label != null) {
                label.setText(text);
            }
        }
        
        /**
         * Sets the new label component
         * 
         * @param label Component
         */
        public void setLabel(Component label) {
            if (label instanceof HasText) {
                this.label = (HasText) label;                
            }
            clearLabel();
            addToLabel(label);
        }

        private void clearLabel() {
            getElement().getChildren()
                .filter(child -> "label".equals(child.getAttribute("slot")))
                .collect(Collectors.toList())
                .forEach(getElement()::removeChild);
        }

        /**
         * This is a convenience API to set the width value of this
         * form item.
         * 
         * @param width A CSS accepted width as string
         */
        public void setLabelWidth(String width) {
            getStyle().set("--vaadin-form-item-label-width", width);
        }
        
        /**
         * This is a convenience API to set the row spacing
         * value of this form item.
         * 
         * @param spacing A CSS accepted value as string
         */
        public void setRowSpacing(String spacing) {
            getStyle().set("--vaadin-form-item-row-spacing", spacing);
        }
        
    }
}
