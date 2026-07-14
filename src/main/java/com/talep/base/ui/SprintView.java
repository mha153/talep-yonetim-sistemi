package com.talep.base.ui;

import com.talep.base.Talep;
import com.talep.base.TalepRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import java.util.stream.Collectors;

@Route("sprint")
public class SprintView extends VerticalLayout {

    public SprintView(TalepRepository repository) {
        Grid<Talep> grid = new Grid<>(Talep.class);
        // Yazılımcı için sadece gerekli sütunları gösteriyoruz
        grid.setColumns("id", "baslik", "aciklama", "skor");

        grid.addComponentColumn(talep -> {
            Button btnTamamla = new Button("Tamamla", e -> {
                talep.setDurum("TAMAMLANDI");
                repository.save(talep);
                Notification.show("İş başarıyla tamamlandı!");
                
                // Tabloyu güncelleyip tamamlanan işi listeden çıkar
                grid.setItems(repository.findAll().stream()
                        .filter(t -> "SPRINT".equalsIgnoreCase(t.getDurum()))
                        .collect(Collectors.toList()));
            });
            btnTamamla.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            return btnTamamla;
        }).setHeader("İşlem");

        
        grid.setItems(repository.findAll().stream()
                .filter(t -> "SPRINT".equalsIgnoreCase(t.getDurum()))
                .collect(Collectors.toList()));

        add(grid);
    }
}