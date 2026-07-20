package com.requestmanagement.base.ui.shared;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import java.util.function.Consumer;

public class DeleteConfirmationDialog extends Dialog {

    public DeleteConfirmationDialog(Runnable deleteAction, Consumer<Boolean> onSkipConfirmationChanged) {
        setHeaderTitle("Silme Onayı");
        add("Bu talebi silmek istediğinize emin misiniz?");

        Checkbox skipConfirmationCheckbox = new Checkbox("Bir daha sorma");

        Button confirmButton = new Button("Evet, Sil", e -> {
            onSkipConfirmationChanged.accept(skipConfirmationCheckbox.getValue());
            deleteAction.run();
            close();
        });
        confirmButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button cancelButton = new Button("İptal", e -> close());

        getFooter().add(skipConfirmationCheckbox, confirmButton, cancelButton);
    }
}
