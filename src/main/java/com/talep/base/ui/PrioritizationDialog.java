package com.talep.base.ui;

import com.talep.base.model.Prioritization;
import com.talep.base.model.Request;
import com.talep.base.model.RequestStatus;
import com.talep.base.repository.PrioritizationRepository;
import com.talep.base.repository.RequestRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Ekran 3: PO Detaylı Parametre Giriş ve Skor Hesaplama Formu.
 * priority_score = impact x urgency (1-25), dokümandaki "Talep Değerlendirme &
 * Önceliklendirme Giriş Ekranı" ile aynı davranışı sergiler: seçim yapıldıkça skor
 * ve öncelik etiketi anlık güncellenir.
 */
public class PrioritizationDialog extends Dialog {

    private static final Map<Integer, String> IMPACT_OPTIONS = new LinkedHashMap<>();
    private static final Map<Integer, String> URGENCY_OPTIONS = new LinkedHashMap<>();

    static {
        IMPACT_OPTIONS.put(1, "1 - Çok Düşük: Kozmetik/görsel, iş süreçlerini etkilemiyor");
        IMPACT_OPTIONS.put(2, "2 - Düşük: Küçük rahatsızlık, nadiren kullanılan bir alanı etkiliyor");
        IMPACT_OPTIONS.put(3, "3 - Orta: Alternatif çözümü (workaround) bulunan iş süreçleri");
        IMPACT_OPTIONS.put(4, "4 - Yüksek: Ana iş sürecini yavaşlatan, workaround'u zor bir sorun");
        IMPACT_OPTIONS.put(5, "5 - Kritik: Sistemin veya ana iş sürecinin tamamen durmasına yol açıyor");

        URGENCY_OPTIONS.put(1, "1 - Çok Düşük: Zaman baskısı yok, ileride ele alınabilir");
        URGENCY_OPTIONS.put(2, "2 - Düşük (Uzun Vadeli): Önümüzdeki birkaç sprint içinde planlanabilir");
        URGENCY_OPTIONS.put(3, "3 - Orta (Orta Vadeli): Yakın gelecekte ele alınmalı");
        URGENCY_OPTIONS.put(4, "4 - Kısa Vadeli (Yüksek): Mevcut aktif Sprint içinde tamamlanmalı");
        URGENCY_OPTIONS.put(5, "5 - Kritik: Derhal müdahale gerektiriyor, üretim ortamını etkiliyor");
    }

    public PrioritizationDialog(Request request, PrioritizationRepository prioritizationRepository,
                                 RequestRepository requestRepository, Runnable listeyiGuncelleMetodu) {
        setHeaderTitle("Talep Değerlendirme & Önceliklendirme Giriş Ekranı - Talep #" + request.getRequestId());

        Prioritization mevcut = prioritizationRepository.findByRequest(request).orElse(null);

        Select<Integer> etkiSecimi = new Select<>();
        etkiSecimi.setLabel("İş Etkisi (Impact) Seçimi");
        etkiSecimi.setItems(IMPACT_OPTIONS.keySet());
        etkiSecimi.setItemLabelGenerator(IMPACT_OPTIONS::get);
        etkiSecimi.setWidthFull();

        Select<Integer> aciliyetSecimi = new Select<>();
        aciliyetSecimi.setLabel("Aciliyet (Urgency) Seçimi");
        aciliyetSecimi.setItems(URGENCY_OPTIONS.keySet());
        aciliyetSecimi.setItemLabelGenerator(URGENCY_OPTIONS::get);
        aciliyetSecimi.setWidthFull();

        if (mevcut != null) {
            etkiSecimi.setValue(mevcut.getImpact());
            aciliyetSecimi.setValue(mevcut.getUrgency());
        }

        Span skorEtiketi = new Span("-");
        skorEtiketi.getStyle().set("font-size", "var(--lumo-font-size-xxl)").set("font-weight", "bold");
        Span oncelikRozeti = new Span();
        oncelikRozeti.getElement().getThemeList().add("badge");

        Runnable skoruGuncelle = () -> {
            Integer etki = etkiSecimi.getValue();
            Integer aciliyet = aciliyetSecimi.getValue();
            if (etki == null || aciliyet == null) {
                skorEtiketi.setText("-");
                oncelikRozeti.setText("");
                return;
            }
            int skor = etki * aciliyet;
            skorEtiketi.setText(String.valueOf(skor));
            oncelikRozeti.getElement().getThemeList().remove("error");
            oncelikRozeti.getElement().getThemeList().remove("warning");
            oncelikRozeti.getElement().getThemeList().remove("success");
            if (skor >= 20) {
                oncelikRozeti.setText("KRİTİK ÖNCELİKLİ");
                oncelikRozeti.getElement().getThemeList().add("error");
            } else if (skor >= 10) {
                oncelikRozeti.setText("ORTA ÖNCELİKLİ");
                oncelikRozeti.getElement().getThemeList().add("warning");
            } else {
                oncelikRozeti.setText("DÜŞÜK ÖNCELİKLİ");
                oncelikRozeti.getElement().getThemeList().add("success");
            }
        };
        etkiSecimi.addValueChangeListener(e -> skoruGuncelle.run());
        aciliyetSecimi.addValueChangeListener(e -> skoruGuncelle.run());
        skoruGuncelle.run();

        VerticalLayout skorKutusu = new VerticalLayout(new H4("HESAPLANAN SKOR"), skorEtiketi, oncelikRozeti);
        skorKutusu.setAlignItems(VerticalLayout.Alignment.CENTER);
        skorKutusu.getStyle().set("border", "1px solid var(--lumo-contrast-20pct)").set("border-radius", "var(--lumo-border-radius-m)");

        Button kaydetButonu = new Button("Değerleri Kaydet", e -> {
            Integer etki = etkiSecimi.getValue();
            Integer aciliyet = aciliyetSecimi.getValue();
            if (etki == null || aciliyet == null) {
                Notification.show("Lütfen İş Etkisi ve Aciliyet seçimlerini tamamlayın.");
                return;
            }

            Prioritization p = mevcut != null ? mevcut : new Prioritization();
            p.setRequest(request);
            p.setImpact(etki);
            p.setUrgency(aciliyet);
            p.setPriorityScore(etki * aciliyet);
            prioritizationRepository.save(p);

            request.setStatus(RequestStatus.PRIORITIZED);
            requestRepository.save(request);

            Notification.show("Talep önceliklendirildi ve skor hesaplandı.");
            listeyiGuncelleMetodu.run();
            close();
        });
        kaydetButonu.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button iptalButonu = new Button("İptal", e -> close());

        HorizontalLayout icerik = new HorizontalLayout(
                new VerticalLayout(etkiSecimi, aciliyetSecimi),
                skorKutusu
        );
        icerik.setWidthFull();
        icerik.setFlexGrow(1, icerik.getComponentAt(0));

        add(new VerticalLayout(icerik, new HorizontalLayout(kaydetButonu, iptalButonu)));
    }
}
