package com.talep.base.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import java.util.function.Consumer;

public class SilmeOnayPenceresi extends Dialog {

    public SilmeOnayPenceresi(Runnable silmeIslemi, Consumer<Boolean> sormaDurumuGuncelle) {
        setHeaderTitle("Silme Onayı");
        add("Bu talebi silmek istediğinize emin misiniz?");

        Checkbox sormaCheckbox = new Checkbox("Bir daha sorma");

        Button evetButonu = new Button("Evet, Sil", e -> {
            sormaDurumuGuncelle.accept(sormaCheckbox.getValue()); 
            silmeIslemi.run(); 
            close();
        });
        evetButonu.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button hayirButonu = new Button("İptal", e -> close());

        getFooter().add(sormaCheckbox, evetButonu, hayirButonu);
    }
}