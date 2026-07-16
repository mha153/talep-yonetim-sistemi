package com.requestmanagement.base.ui;

import com.requestmanagement.base.model.Request;
import com.requestmanagement.base.model.RequestStatus;
import com.requestmanagement.base.repository.RequestRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.List;
import java.util.function.Supplier;

/** Shows a customer's own past requests, with a cancel action on new ones. */
class RequestHistoryGrid extends VerticalLayout {

    private final RequestRepository requestRepository;
    private final Supplier<List<Request>> requestsSupplier;
    private final Grid<Request> grid = new Grid<>(Request.class, false);

    RequestHistoryGrid(RequestRepository requestRepository, Supplier<List<Request>> requestsSupplier) {
        this.requestRepository = requestRepository;
        this.requestsSupplier = requestsSupplier;

        grid.addColumn(Request::getTitle).setHeader("Talep Başlığı").setFlexGrow(1);
        grid.addColumn(Request::getDescription).setHeader("Açıklama").setFlexGrow(2);
        grid.addColumn(request -> request.getStatus().displayLabel())
                .setHeader("Son Durum").setWidth("180px").setFlexGrow(0);
        grid.addComponentColumn(this::buildCancelAction).setHeader("İşlem").setWidth("120px").setFlexGrow(0);

        setWidthFull();
        setPadding(false);
        add(new H3("Geçmiş Taleplerim / Takip"), grid);
        refresh();
    }

    private Component buildCancelAction(Request request) {
        if (!RequestStatus.NEW.equals(request.getStatus())) {
            return new Span();
        }
        Button cancelButton = new Button("İptal Et", e -> {
            request.setStatus(RequestStatus.REJECTED);
            requestRepository.save(request);
            refresh();
            Toast.show("Talep iptal edildi.");
        });
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
        return cancelButton;
    }

    void refresh() {
        grid.setItems(requestsSupplier.get());
    }
}
