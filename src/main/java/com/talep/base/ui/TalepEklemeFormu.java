package com.talep.base.ui;

import com.talep.base.Talep;
import com.talep.base.TalepRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

public class TalepEklemeFormu extends HorizontalLayout {

    private final Binder<Talep> binder = new Binder<>(Talep.class);

    public TalepEklemeFormu(TalepRepository repository, Runnable listeyiGuncelleMetodu) {
        TextField yeniMusteri = new TextField("Müşteri");
        TextField yeniBaslik = new TextField("Başlık");
        TextField yeniDurum = new TextField("Durum");
        Button ekleButonu = new Button("Ekle");

        binder.forField(yeniMusteri).asRequired("Müşteri ismi boş bırakılamaz").bind(Talep::getMusteri, Talep::setMusteri);
        binder.forField(yeniBaslik).asRequired("Başlık boş bırakılamaz").bind(Talep::getBaslik, Talep::setBaslik);
        binder.forField(yeniDurum).asRequired("Durum boş bırakılamaz").bind(Talep::getDurum, Talep::setDurum);

        ekleButonu.addClickListener(click -> {
            Talep talep = new Talep();
            if (binder.writeBeanIfValid(talep)) {
                repository.save(talep);
                listeyiGuncelleMetodu.run();
                
                
                binder.readBean(new Talep()); 
            } else {
                Notification.show("Lütfen tüm alanları doldurunuz!");
            }
        });

        setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        add(yeniMusteri, yeniBaslik, yeniDurum, ekleButonu);
    }
}