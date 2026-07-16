package com.talep.base.ui;

import com.talep.base.model.AppUser;
import com.talep.base.model.Request;
import com.talep.base.model.RequestStatus;
import com.talep.base.model.Role;
import com.talep.base.repository.RequestRepository;
import com.talep.base.repository.UserRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@Route(value = "talep-olustur", layout = MainLayout.class)
@RolesAllowed("CUSTOMER")
public class TalepView extends VerticalLayout {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final Grid<Request> grid = new Grid<>(Request.class, false);

    public TalepView(RequestRepository requestRepository, UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
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
            if (baslikKutusu.isEmpty()) {
                Notification.show("Lütfen Talep Başlığı alanını doldurun.");
                return;
            }

            AppUser aktifMusteri = aktifMusteriyiGetir();

            Request yeniTalep = new Request();
            yeniTalep.setCustomer(aktifMusteri);
            yeniTalep.setTitle(baslikKutusu.getValue());
            yeniTalep.setDescription(aciklamaKutusu.getValue());
            yeniTalep.setStatus(RequestStatus.NEW);

            requestRepository.save(yeniTalep);
            Notification.show("Talep başarıyla gönderildi!");

            baslikKutusu.clear();
            aciklamaKutusu.clear();
            listeyiGuncelle();
        });

        H3 altBaslik = new H3("Geçmiş Taleplerim / Takip");
        grid.addColumn(Request::getTitle).setHeader("Talep Başlığı").setFlexGrow(1);
        grid.addColumn(Request::getDescription).setHeader("Açıklama").setFlexGrow(2);
        grid.addColumn(request -> request.getStatus().name()).setHeader("Son Durum").setWidth("180px").setFlexGrow(0);

        grid.addComponentColumn(request -> {
            if (RequestStatus.NEW.equals(request.getStatus())) {
                Button iptalButonu = new Button("İptal Et", e -> {
                    request.setStatus(RequestStatus.REJECTED);
                    requestRepository.save(request);
                    listeyiGuncelle();
                    Notification.show("Talep iptal edildi.");
                });
                iptalButonu.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
                return iptalButonu;
            }
            return new Span();
        }).setHeader("İşlem").setWidth("120px").setFlexGrow(0);

        add(baslik, formLayout, kaydetButonu, new Hr(), altBaslik, grid);
        listeyiGuncelle();
    }

    private void listeyiGuncelle() {
        AppUser aktifMusteri = aktifMusteriyiGetir();
        List<Request> talepler = requestRepository.findByCustomer_Email(aktifMusteri.getEmail());
        grid.setItems(talepler);
    }

    private AppUser aktifMusteriyiGetir() {
        String kullaniciAdi = SecurityContextHolder.getContext().getAuthentication().getName();
        String email = kullaniciAdi + "@talep.local";
        return userRepository.findByEmail(email).orElseGet(() -> {
            AppUser yeniKullanici = new AppUser();
            yeniKullanici.setEmail(email);
            yeniKullanici.setNameSurname(kullaniciAdi);
            yeniKullanici.setRole(Role.CUSTOMER);
            return userRepository.save(yeniKullanici);
        });
    }
}
