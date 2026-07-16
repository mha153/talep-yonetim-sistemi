package com.requestmanagement.base.ui;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.Request;
import com.requestmanagement.base.model.RequestStatus;
import com.requestmanagement.base.repository.RequestRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

import java.util.function.Supplier;

/** The customer-facing "create a new request" form. */
class RequestFormPanel extends VerticalLayout {

    RequestFormPanel(RequestRepository requestRepository, Supplier<AppUser> currentCustomer, Runnable onSaved) {
        TextField titleField = new TextField("Talep Başlığı");
        TextArea descriptionField = new TextArea("Açıklama");
        descriptionField.setMinHeight("120px");

        FormLayout formLayout = new FormLayout();
        formLayout.add(titleField, descriptionField);
        formLayout.setColspan(descriptionField, 2);

        Button saveButton = new Button("Talebi Gönder");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> {
            if (titleField.isEmpty() || descriptionField.isEmpty()) {
                Toast.show("Lütfen Talep Başlığı ve Açıklama alanlarını doldurun.");
                return;
            }

            Request newRequest = new Request();
            newRequest.setCustomer(currentCustomer.get());
            newRequest.setTitle(titleField.getValue());
            newRequest.setDescription(descriptionField.getValue());
            newRequest.setStatus(RequestStatus.NEW);
            requestRepository.save(newRequest);

            Toast.show("Talep başarıyla gönderildi!");
            titleField.clear();
            descriptionField.clear();
            onSaved.run();
        });

        setWidthFull();
        setPadding(false);
        add(new H3("Yeni Talep Oluştur"), formLayout, saveButton);
    }
}
