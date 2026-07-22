package com.requestmanagement.base.ui.developer;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.Role;
import com.requestmanagement.base.model.Workflow;
import com.requestmanagement.base.model.WorkflowStatus;
import com.requestmanagement.base.repository.NotificationRepository;
import com.requestmanagement.base.repository.PrioritizationRepository;
import com.requestmanagement.base.repository.RequestActivityRepository;
import com.requestmanagement.base.repository.RequestAttachmentRepository;
import com.requestmanagement.base.repository.RequestMessageRepository;
import com.requestmanagement.base.repository.UserRepository;
import com.requestmanagement.base.repository.WorkflowRepository;
import com.requestmanagement.base.ui.shared.CurrentUserResolver;
import com.requestmanagement.base.ui.shared.GridRowHighlighter;
import com.requestmanagement.base.ui.shared.MainLayout;
import com.requestmanagement.base.ui.shared.RequestDetailsPanel;
import com.requestmanagement.base.ui.shared.RequestScoreBadge;
import com.requestmanagement.base.ui.shared.RequestSearchFilter;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.context.SecurityContextHolder;

/** Shared Sprint pool: workflow items no developer has claimed yet. */
@Route(value = "sprint", layout = MainLayout.class)
@PageTitle("Sprint Havuzu")
@RolesAllowed("DEVELOPER")
public class SprintView extends VerticalLayout implements BeforeEnterObserver {

    private final transient WorkflowRepository workflowRepository;
    private final transient PrioritizationRepository prioritizationRepository;
    private final Grid<Workflow> grid = new Grid<>(Workflow.class, false);
    private String searchText = "";

    public SprintView(WorkflowRepository workflowRepository, PrioritizationRepository prioritizationRepository,
                       UserRepository userRepository, NotificationRepository notificationRepository,
                       RequestActivityRepository activityRepository, RequestAttachmentRepository attachmentRepository,
                       RequestMessageRepository messageRepository) {
        this.workflowRepository = workflowRepository;
        this.prioritizationRepository = prioritizationRepository;
        AppUser currentDeveloper = CurrentUserResolver.findOrCreate(
                userRepository, SecurityContextHolder.getContext().getAuthentication(), Role.DEVELOPER);

        grid.addColumn(workflow -> workflow.getRequest().getRequestId())
                .setHeader("ID").setWidth("60px").setFlexGrow(0);
        grid.addColumn(workflow -> workflow.getRequest().getCustomer().getNameSurname())
                .setHeader("Müşteri").setFlexGrow(1);
        grid.addColumn(workflow -> workflow.getRequest().getTitle()).setHeader("Başlık").setFlexGrow(2);
        grid.addComponentColumn(workflow -> RequestScoreBadge.create(
                prioritizationRepository.findByRequest(workflow.getRequest()).orElse(null)))
                .setHeader("Skor").setWidth("150px").setFlexGrow(0);

        grid.addComponentColumn(workflow -> SprintPoolRowActions.build(workflow, workflowRepository,
                userRepository, notificationRepository, activityRepository, messageRepository, currentDeveloper,
                grid, this::refresh))
                .setHeader("İşlem").setWidth("300px").setFlexGrow(0);

        grid.setItemDetailsRenderer(new ComponentRenderer<>(
                workflow -> new RequestDetailsPanel(workflow.getRequest(), activityRepository, attachmentRepository)));
        grid.setDetailsVisibleOnClick(true);
        grid.setSizeFull();

        TextField searchField = new TextField();
        searchField.setPlaceholder("Müşteri veya Başlık ara...");
        searchField.setClearButtonVisible(true);
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> search(e.getValue()));

        setSizeFull();
        add(searchField, grid);
        refresh();
    }

    private void search(String text) {
        this.searchText = text;
        refresh();
    }

    private void refresh() {
        var workflows = workflowRepository.findByDeveloperIsNullAndWorkflowStatusNot(WorkflowStatus.DONE);
        workflows = RequestSearchFilter.apply(workflows, searchText,
                w -> w.getRequest().getCustomer().getNameSurname(), w -> w.getRequest().getTitle());
        grid.setItems(SprintPoolSorter.byScoreDescending(workflows, prioritizationRepository));
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        event.getLocation().getQueryParameters().getSingleParameter("highlight").map(Long::valueOf)
                .ifPresent(id -> GridRowHighlighter.apply(grid, w -> w.getRequest().getRequestId(), id));
    }
}
