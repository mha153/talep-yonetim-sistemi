package com.talep.base.ui;

import com.talep.base.Talep;
import com.talep.base.TalepRepository;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import java.util.stream.Collectors;

@Route("arsiv")
public class CompletedView extends VerticalLayout {

    public CompletedView(TalepRepository repository) {
        Grid<Talep> grid = new Grid<>(Talep.class);
        grid.setColumns("id", "musteri", "baslik", "aciklama", "skor");

        
        grid.setItems(repository.findAll().stream()
                .filter(t -> "TAMAMLANDI".equalsIgnoreCase(t.getDurum()))
                .collect(Collectors.toList()));

        add(grid);
    }
}