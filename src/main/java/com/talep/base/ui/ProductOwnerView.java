package com.talep.base.ui;

import com.talep.base.Talep;
import com.talep.base.TalepRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("po")
public class ProductOwnerView extends VerticalLayout {

    public ProductOwnerView(TalepRepository repository) {
        Grid<Talep> grid = new Grid<>(Talep.class);
        grid.setColumns("id", "musteri", "baslik", "durum", "skor");

        grid.addComponentColumn(talep -> {
            Button btnOncelik = new Button("Önceliklendir");
            
            Button btnSprint = new Button("İş Akışına Çevir", e -> {
                talep.setDurum("SPRINT");
                repository.save(talep);
                Notification.show("Talep Sprint'e aktarıldı!");
                grid.getDataProvider().refreshItem(talep);
            });
            btnSprint.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

            btnOncelik.addClickListener(e -> {
                Dialog dialog = new Dialog();
                TextField aciliyetField = new TextField("Aciliyet (1-5)");
                TextField etkiField = new TextField("Etki (1-5)");
                Button kaydetBtn = new Button("Hesapla", saveEvent -> {
                    try {
                        int a = Integer.parseInt(aciliyetField.getValue());
                        int eVal = Integer.parseInt(etkiField.getValue());
                        
                        // 1-5 aralığı kontrolü
                        if (a < 1 || a > 5 || eVal < 1 || eVal > 5) {
                            Notification.show("Lütfen 1 ile 5 arasında bir sayı girin!");
                            return; // Hatalıysa işlemi burada kes
                        }

                        talep.setAciliyet(a);
                        talep.setEtki(eVal);
                        talep.setSkor(a * eVal * 4);
                        repository.save(talep);
                        dialog.close();
                        grid.getDataProvider().refreshItem(talep);
                    } catch (NumberFormatException ex) { 
                        Notification.show("Lütfen geçerli bir sayı girin!"); 
                    }
                });
                dialog.add(aciliyetField, etkiField, kaydetBtn);
                dialog.open();
            });

            return new HorizontalLayout(btnOncelik, btnSprint);
        })
        .setHeader("İşlemler")
        .setWidth("360px") 
        .setFlexGrow(0);

        grid.setItems(repository.findAll());
        add(grid);
    }
}