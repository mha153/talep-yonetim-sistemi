package com.talep.base.ui;

import com.talep.base.Talep;
import com.talep.base.TalepRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "gelen-talepler", layout = MainLayout.class)
@PageTitle("Gelen Talepler")
@RolesAllowed("PO")
public class GelenTaleplerView extends VerticalLayout {

    private final TalepRepository repository;
    private final Grid<Talep> grid = new Grid<>(Talep.class, false);
    private final TextField aramaKutusu = new TextField();

    public GelenTaleplerView(TalepRepository repository) {
        this.repository = repository;
        
        aramaKutusu.setPlaceholder("Müşteri veya Başlık ara...");
        aramaKutusu.setClearButtonVisible(true);
        aramaKutusu.setValueChangeMode(ValueChangeMode.LAZY);
        aramaKutusu.addValueChangeListener(e -> listeyiGuncelle());

        tabloyuAyarla();

        add(aramaKutusu, grid);
        setSizeFull();
        listeyiGuncelle();
    }

    private void tabloyuAyarla() {
        grid.setSizeFull();

        grid.addColumn(Talep::getId).setHeader("ID").setWidth("60px").setFlexGrow(0);
        grid.addColumn(Talep::getMusteri).setHeader("Müşteri").setFlexGrow(1);
        grid.addColumn(Talep::getBaslik).setHeader("Talep Başlığı").setFlexGrow(2);

        grid.addColumn(new ComponentRenderer<>(talep -> {
            Span skorEtiketi = new Span();
            if (talep.getSkor() == null || talep.getSkor() == 0) {
                skorEtiketi.setText("Belirlenmedi");
                return skorEtiketi;
            }
            
            skorEtiketi.setText(String.valueOf(talep.getSkor()));
            skorEtiketi.getElement().getThemeList().add("badge");
            
            if (talep.getSkor() >= 80) {
                skorEtiketi.getElement().getThemeList().add("error"); 
            } else if (talep.getSkor() >= 40) {
                skorEtiketi.getElement().getThemeList().add("warning"); 
            } else {
                skorEtiketi.getElement().getThemeList().add("success"); 
            }
            return skorEtiketi;
        })).setHeader("Skor").setWidth("130px").setFlexGrow(0).setSortable(true);

        grid.addColumn(Talep::getDurum).setHeader("Durum").setWidth("200px").setFlexGrow(0);

        grid.addComponentColumn(talep -> {
            Button onceliklendirButonu = new Button("Önceliklendir", e -> onceliklendirmePenceresiAc(talep));
            onceliklendirButonu.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
            
            Button ataButonu = new Button("Ata", e -> {
                talep.setDurum("YAZILIMCIYA ATANDI");
                repository.save(talep);
                listeyiGuncelle();
            });
            ataButonu.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
            ataButonu.setVisible("YENİ".equals(talep.getDurum()) || "ÖNCELİKLENDİRİLDİ".equals(talep.getDurum()));
            
            Button reddetButonu = new Button("Reddet", e -> {
                talep.setDurum("REDDEDİLDİ");
                repository.save(talep);
                listeyiGuncelle();
            });
            reddetButonu.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
            reddetButonu.setVisible("YENİ".equals(talep.getDurum()) || "ÖNCELİKLENDİRİLDİ".equals(talep.getDurum()));

            return new HorizontalLayout(onceliklendirButonu, ataButonu, reddetButonu);
        }).setHeader("İşlemler").setWidth("280px").setFlexGrow(0);
    }

    private void onceliklendirmePenceresiAc(Talep talep) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Talebi Önceliklendir");

        TextField aciliyetAlan = new TextField("Aciliyet (1-5)");
        TextField etkiAlan = new TextField("Etki (1-5)");

        if (talep.getAciliyet() != null) aciliyetAlan.setValue(String.valueOf(talep.getAciliyet()));
        if (talep.getEtki() != null) etkiAlan.setValue(String.valueOf(talep.getEtki()));

        Button kaydetButonu = new Button("Kaydet", e -> {
            boolean hataVar = false;
            int aciliyet = 0;
            int etki = 0;

            
            aciliyetAlan.setInvalid(false);
            etkiAlan.setInvalid(false);

            try {
                aciliyet = Integer.parseInt(aciliyetAlan.getValue());
                if (aciliyet < 1 || aciliyet > 5) {
                    aciliyetAlan.setInvalid(true);
                    hataVar = true;
                }
            } catch (NumberFormatException ex) {
                aciliyetAlan.setInvalid(true);
                hataVar = true;
            }

            try {
                etki = Integer.parseInt(etkiAlan.getValue());
                if (etki < 1 || etki > 5) {
                    etkiAlan.setInvalid(true);
                    hataVar = true;
                }
            } catch (NumberFormatException ex) {
                etkiAlan.setInvalid(true);
                hataVar = true;
            }

            if (hataVar) {
                Notification.show("Lütfen 1 ile 5 arasında değerler giriniz!", 3000, Notification.Position.BOTTOM_START);
                return;
            }

            talep.setAciliyet(aciliyet);
            talep.setEtki(etki);
            talep.setSkor((aciliyet * etki) * 4);
            talep.setDurum("ÖNCELİKLENDİRİLDİ");

            repository.save(talep);
            listeyiGuncelle();
            dialog.close();
            Notification.show("Talep önceliklendirildi ve skor hesaplandı.");
        });
        kaydetButonu.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button iptalButonu = new Button("İptal", e -> dialog.close());

        HorizontalLayout butonlar = new HorizontalLayout(kaydetButonu, iptalButonu);
        VerticalLayout layout = new VerticalLayout(aciliyetAlan, etkiAlan, butonlar);
        
        dialog.add(layout);
        dialog.open();
    }

    private void listeyiGuncelle() {
        if (aramaKutusu.isEmpty()) {
            grid.setItems(repository.findAll());
        } else {
            grid.setItems(repository.findByMusteriContainingIgnoreCaseOrBaslikContainingIgnoreCase(
                aramaKutusu.getValue(), aramaKutusu.getValue()
            ));
        }
    }
}