package com.talep.base.ui;

import com.talep.base.Talep;
import com.talep.base.TalepRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.context.SecurityContextHolder;

@Route(value = "talep-olustur", layout = MainLayout.class)
@RolesAllowed("MUSTERI")
public class TalepView extends VerticalLayout {

    private final TalepRepository repository;
    private final Grid<Talep> grid = new Grid<>(Talep.class, false);

    public TalepView(TalepRepository repository) {
        this.repository = repository;
        setWidthFull();

        H3 baslik = new H3("Yeni Talep Oluştur");

        TextField baslikKutusu = new TextField("Talep Başlığı");
        
        TextArea aciklamaKutusu = new TextArea("Açıklama");
        aciklamaKutusu.setMinHeight("120px");

        FormLayout formLayout = new FormLayout();
        formLayout.add(baslikKutusu, aciklamaKutusu);
        formLayout.setColspan(aciklamaKutusu, 2); 

        Button kaydetButonu = new Button("Talebi Gönder");
        kaydetButonu.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        kaydetButonu.addClickListener(e -> {
            if(baslikKutusu.isEmpty()) {
                Notification.show("Lütfen Talep Başlığı alanını doldurun.");
                return;
            }

            // Giriş yapan kullanıcının adını sistemden otomatik çekiyoruz
            String aktifKullanici = SecurityContextHolder.getContext().getAuthentication().getName();

            Talep yeniTalep = new Talep();
            yeniTalep.setMusteri(aktifKullanici);
            yeniTalep.setBaslik(baslikKutusu.getValue());
            yeniTalep.setAciklama(aciklamaKutusu.getValue());
            yeniTalep.setDurum("YENİ");

            repository.save(yeniTalep);
            Notification.show("Talep başarıyla gönderildi!");

            baslikKutusu.clear();
            aciklamaKutusu.clear();
            listeyiGuncelle();
        });

        H3 altBaslik = new H3("Geçmiş Taleplerim / Takip");
        grid.addColumn(Talep::getBaslik).setHeader("Talep Başlığı").setFlexGrow(1);
        grid.addColumn(Talep::getAciklama).setHeader("Açıklama").setFlexGrow(2);
        grid.addColumn(Talep::getDurum).setHeader("Son Durum").setWidth("180px").setFlexGrow(0);
        
        grid.addComponentColumn(talep -> {
            if ("YENİ".equals(talep.getDurum())) {
                Button iptalButonu = new Button("İptal Et", e -> {
                    talep.setDurum("İPTAL EDİLDİ");
                    repository.save(talep);
                    listeyiGuncelle();
                    Notification.show("Talep iptal edildi.");
                });
                iptalButonu.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
                return iptalButonu;
            }
            return new com.vaadin.flow.component.html.Span();
        }).setHeader("İşlem").setWidth("120px").setFlexGrow(0);

        add(baslik, formLayout, kaydetButonu, new Hr(), altBaslik, grid);
        listeyiGuncelle();
    }

    private void listeyiGuncelle() {
        grid.setItems(repository.findAll());
    }
}