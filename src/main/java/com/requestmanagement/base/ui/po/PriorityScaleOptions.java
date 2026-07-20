package com.requestmanagement.base.ui.po;

import com.vaadin.flow.component.select.Select;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

/** Impact/urgency 1-5 scale definitions and a shared {@link Select} factory. */
final class PriorityScaleOptions {

    static final Map<Integer, String> IMPACT = new LinkedHashMap<>();
    static final Map<Integer, String> URGENCY = new LinkedHashMap<>();

    static {
        IMPACT.put(1, "1 - Çok Düşük");
        IMPACT.put(2, "2 - Düşük");
        IMPACT.put(3, "3 - Orta");
        IMPACT.put(4, "4 - Yüksek");
        IMPACT.put(5, "5 - Kritik");

        URGENCY.put(1, "1 - Çok Uzun Vadeli");
        URGENCY.put(2, "2 - Uzun Vadeli");
        URGENCY.put(3, "3 - Orta Vadeli");
        URGENCY.put(4, "4 - Kısa Vadeli");
        URGENCY.put(5, "5 - Kritik");
    }

    private PriorityScaleOptions() {
    }

    static Select<Integer> buildSelect(String label, Map<Integer, String> options, @Nullable Integer initialValue) {
        Select<Integer> select = new Select<>();
        select.setLabel(label);
        select.setItems(options.keySet());
        select.setItemLabelGenerator(options::get);
        select.setWidthFull();
        if (initialValue != null) {
            select.setValue(initialValue);
        }
        return select;
    }
}
