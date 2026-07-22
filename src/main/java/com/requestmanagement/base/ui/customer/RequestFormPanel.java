package com.requestmanagement.base.ui.customer;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.Request;
import com.requestmanagement.base.model.RequestStatus;
import com.requestmanagement.base.repository.NotificationRepository;
import com.requestmanagement.base.repository.RequestActivityRepository;
import com.requestmanagement.base.repository.RequestAttachmentRepository;
import com.requestmanagement.base.repository.RequestRepository;
import com.requestmanagement.base.repository.UserRepository;
import com.requestmanagement.base.ui.notifications.NotificationCenter;
import com.requestmanagement.base.ui.shared.ActivityRecorder;
import com.requestmanagement.base.ui.shared.AttachmentRecorder;
import com.requestmanagement.base.ui.shared.Toast;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.server.streams.UploadHandler;
import com.vaadin.flow.server.streams.UploadMetadata;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/** The customer-facing "create a new request" form. */
class RequestFormPanel extends VerticalLayout {

    RequestFormPanel(RequestRepository requestRepository, UserRepository userRepository,
                      NotificationRepository notificationRepository, RequestActivityRepository activityRepository,
                      RequestAttachmentRepository attachmentRepository, Supplier<AppUser> currentCustomer,
                      Runnable onSaved) {
        TextField titleField = new TextField("Talep Başlığı");
        titleField.setPlaceholder("Örn: Ödeme ekranında kredi kartı hata uyarısı alınıyor...");
        TextArea descriptionField = new TextArea("Açıklama");
        descriptionField.setPlaceholder("Yaşanan problemi veya eklenmesini istediğiniz özelliği detaylıca yazınız...");
        descriptionField.setMinHeight("120px");

        AtomicReference<byte[]> uploadedData = new AtomicReference<>();
        AtomicReference<UploadMetadata> uploadedMeta = new AtomicReference<>();
        UploadHandler uploadHandler = UploadHandler.inMemory((metadata, data) -> {
            uploadedMeta.set(metadata);
            uploadedData.set(data);
        });
        Upload attachmentUpload = new Upload(uploadHandler);
        attachmentUpload.setAcceptedMimeTypes("image/png", "image/jpeg", "image/gif");
        attachmentUpload.setMaxFiles(1);
        attachmentUpload.setI18n(new UploadI18N()
                .setAddFiles(new UploadI18N.AddFiles().setOne("Dosya Yükle").setMany("Dosya Yükle"))
                .setDropFiles(new UploadI18N.DropFiles()
                        .setOne("Dosyayı buraya bırakın").setMany("Dosyaları buraya bırakın")));

        FormLayout formLayout = new FormLayout();
        formLayout.add(titleField, descriptionField, attachmentUpload);
        formLayout.setColspan(descriptionField, 2);
        formLayout.setColspan(attachmentUpload, 2);

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
            newRequest = requestRepository.save(newRequest);

            if (uploadedData.get() != null) {
                AttachmentRecorder.record(attachmentRepository, newRequest, uploadedMeta.get().fileName(),
                        uploadedMeta.get().contentType(), uploadedData.get());
            }

            ActivityRecorder.record(activityRepository, newRequest, "Talep oluşturuldu");
            NotificationCenter.notifyProductOwner(notificationRepository, userRepository, newRequest,
                    newRequest.getCustomer(), "yeni bir talep oluşturdu: " + newRequest.getTitle());

            Toast.show("Talep başarıyla gönderildi!");
            titleField.clear();
            descriptionField.clear();
            uploadedData.set(null);
            attachmentUpload.clearFileList();
            onSaved.run();
        });

        setWidthFull();
        setPadding(false);
        add(new H3("Yeni Talep Oluştur"), formLayout, saveButton);
    }
}
