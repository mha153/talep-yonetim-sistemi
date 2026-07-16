package com.talep.base.ui;

import com.talep.base.model.Prioritization;
import com.talep.base.model.Request;
import com.talep.base.model.RequestStatus;
import com.talep.base.model.Workflow;
import com.talep.base.repository.PrioritizationRepository;
import com.talep.base.repository.RequestRepository;
import com.talep.base.repository.WorkflowRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

@Route(value = "gelen-talepler", layout = MainLayout.class)
@PageTitle("Gelen Talepler")
@RolesAllowed("PRODUCT_OWNER")
public class GelenTaleplerView extends VerticalLayout {

    private static final List<RequestStatus> LISTELENEN_DURUMLAR =
            List.of(RequestStatus.NEW, RequestStatus.UNDER_REVIEW, RequestStatus.PRIORITIZED);

    private final RequestRepository requestRepository;
    private final PrioritizationRepository prioritizationRepository;
    private final WorkflowRepository workflowRepository;
    private final Grid<Request> grid = new Grid<>(Request.class, false);
    private final TextField aramaKutusu = new TextField();

    public GelenTaleplerView(RequestRepository requestRepository,
                              PrioritizationRepository prioritizationRepository,
                              WorkflowRepository workflowRepository) {
        this.requestRepository = requestRepository;
        this.prioritizationRepository = prioritizationRepository;
        this.workflowRepository = workflowRepository;

        aramaKutusu.setPlaceholder("Müşteri veya Başlık ara...");
        aramaKutusu.setClearButtonVisible(true);
        aramaKutusu.setValueChangeMode(ValueChangeMode.LAZY);
        aramaKutusu.addValueChangeListener(e -> listeyiGuncelle());

        tabloyuAyarla();

        add(aramaKutusu, grid);
        setSizeFull();
        listeyiGuncelle();
    }

    private void tabloyuAyarla() {
        grid.setSizeFull();

        grid.addColumn(Request::getRequestId).setHeader("ID").setWidth("60px").setFlexGrow(0);
        grid.addColumn(request -> request.getCustomer().getNameSurname()).setHeader("Müşteri").setFlexGrow(1);
        grid.addColumn(Request::getTitle).setHeader("Talep Başlığı").setFlexGrow(2);

        grid.addColumn(new ComponentRenderer<>(request -> {
            Span skorEtiketi = new Span();
            Prioritization p = prioritizationRepository.findByRequest(request).orElse(null);
            if (p == null) {
                skorEtiketi.setText("Belirlenmedi");
                return skorEtiketi;
            }

            skorEtiketi.setText(String.valueOf(p.getPriorityScore()));
            skorEtiketi.getElement().getThemeList().add("badge");

            if (p.getPriorityScore() >= 20) {
                skorEtiketi.getElement().getThemeList().add("error");
            } else if (p.getPriorityScore() >= 10) {
                skorEtiketi.getElement().getThemeList().add("warning");
            } else {
                skorEtiketi.getElement().getThemeList().add("success");
            }
            return skorEtiketi;
        })).setHeader("Skor").setWidth("130px").setFlexGrow(0);

        grid.addColumn(request -> request.getStatus().name()).setHeader("Durum").setWidth("200px").setFlexGrow(0);

        grid.addComponentColumn(request -> {
            Button onceliklendirButonu = new Button("Önceliklendir", e -> onceliklendirmePenceresiAc(request));
            onceliklendirButonu.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);

            boolean isAkisindaVar = workflowRepository.existsByRequest(request);
            Button isAkisinaCevirButonu = new Button("İş Akışına Çevir", e -> {
                Workflow workflow = new Workflow();
                workflow.setRequest(request);
                workflowRepository.save(workflow);
                Notification.show("Talep iş akışına aktarıldı.");
                listeyiGuncelle();
            });
            isAkisinaCevirButonu.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
            isAkisinaCevirButonu.setVisible(RequestStatus.PRIORITIZED.equals(request.getStatus()) && !isAkisindaVar);

            Button reddetButonu = new Button("Reddet", e -> {
                request.setStatus(RequestStatus.REJECTED);
                requestRepository.save(request);
                listeyiGuncelle();
            });
            reddetButonu.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);

            return new HorizontalLayout(onceliklendirButonu, isAkisinaCevirButonu, reddetButonu);
        }).setHeader("İşlemler").setWidth("340px").setFlexGrow(0);
    }

    private void onceliklendirmePenceresiAc(Request request) {
        PrioritizationDialog dialog = new PrioritizationDialog(
                request, prioritizationRepository, requestRepository, this::listeyiGuncelle);
        dialog.open();
    }

    private void listeyiGuncelle() {
        List<Request> talepler = requestRepository.findByStatusIn(LISTELENEN_DURUMLAR);
        if (!aramaKutusu.isEmpty()) {
            String arananMetin = aramaKutusu.getValue().toLowerCase();
            talepler = talepler.stream()
                    .filter(r -> r.getCustomer().getNameSurname().toLowerCase().contains(arananMetin)
                            || r.getTitle().toLowerCase().contains(arananMetin))
                    .toList();
        }
        grid.setItems(talepler);
    }
}
