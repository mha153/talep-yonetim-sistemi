package com.requestmanagement.base.ui.shared;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;

/** A generic "are you sure?" confirmation dialog shown before a destructive action. */
public class DeleteConfirmationDialog extends Dialog {

    public DeleteConfirmationDialog(String message, Runnable onConfirm) {
        setHeaderTitle("Emin misiniz?");
        add(message);

        Button confirmButton = new Button("Evet, Sil", e -> {
            onConfirm.run();
            close();
        });
        confirmButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button cancelButton = new Button("İptal", e -> close());

        getFooter().add(confirmButton, cancelButton);
    }
}
