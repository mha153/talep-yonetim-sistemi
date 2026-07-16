package com.requestmanagement.base.ui;

import com.vaadin.flow.component.select.Select;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

/** Impact/urgency 1-5 scale definitions and a shared {@link Select} factory. */
final class PriorityScaleOptions {

    static final Map<Integer, String> IMPACT = new LinkedHashMap<>();
    static final Map<Integer, String> URGENCY = new LinkedHashMap<>();

    static {
        IMPACT.put(1, "1 - Çok Düşük: Kozmetik/görsel, iş süreçlerini etkilemiyor");
        IMPACT.put(2, "2 - Düşük: Küçük rahatsızlık, nadiren kullanılan bir alanı etkiliyor");
        IMPACT.put(3, "3 - Orta: Alternatif çözümü (workaround) bulunan iş süreçleri");
        IMPACT.put(4, "4 - Yüksek: Ana iş sürecini yavaşlatan, workaround'u zor bir sorun");
        IMPACT.put(5, "5 - Kritik: Sistemin veya ana iş sürecinin tamamen durmasına yol açıyor");

        URGENCY.put(1, "1 - Çok Düşük: Zaman baskısı yok, ileride ele alınabilir");
        URGENCY.put(2, "2 - Düşük (Uzun Vadeli): Önümüzdeki birkaç sprint içinde planlanabilir");
        URGENCY.put(3, "3 - Orta (Orta Vadeli): Yakın gelecekte ele alınmalı");
        URGENCY.put(4, "4 - Kısa Vadeli (Yüksek): Mevcut aktif Sprint içinde tamamlanmalı");
        URGENCY.put(5, "5 - Kritik: Derhal müdahale gerektiriyor, üretim ortamını etkiliyor");
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
