package com.vaadin.componentfactory;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.shared.Registration;

@CssImport(value = "./styles/enhanced-form-item.css", themeFor = "vaadin-form-item")
public class EnhancedFormLayout extends FormLayout {

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

    public class EnhancedFormItem extends FormItem {

        Registration listenerReg;

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
            add(comp);
            getElement().getStyle().set("--required-dot-opacity", "0");
            addToLabel(label);
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
        }
    }
}
