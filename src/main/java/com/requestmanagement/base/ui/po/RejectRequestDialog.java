package com.requestmanagement.base.ui.po;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.Request;
import com.requestmanagement.base.repository.NotificationRepository;
import com.requestmanagement.base.repository.RequestActivityRepository;
import com.requestmanagement.base.repository.RequestRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;

/** Prompts the PO for an optional rejection reason before rejecting a request. */
class RejectRequestDialog extends Dialog {

    RejectRequestDialog(Request request, RequestRepository requestRepository,
                         NotificationRepository notificationRepository,
                         RequestActivityRepository activityRepository, AppUser currentPo, Runnable onRejected) {
        setHeaderTitle("Talebi Reddet - #" + request.getRequestId());

        TextArea reasonField = new TextArea("Red Nedeni (isteğe bağlı)");
        reasonField.setWidthFull();

        Button rejectButton = new Button("Reddet", e -> {
            RequestStatusActions.reject(request, requestRepository, notificationRepository, activityRepository,
                    currentPo, reasonField.getValue());
            onRejected.run();
            close();
        });
        rejectButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        Button cancelButton = new Button("Vazgeç", e -> close());

        add(reasonField);
        getFooter().add(new HorizontalLayout(rejectButton, cancelButton));
    }
}
