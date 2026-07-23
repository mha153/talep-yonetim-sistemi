package com.requestmanagement.base.ui.developer;

import com.requestmanagement.base.model.Prioritization;
import com.requestmanagement.base.model.Workflow;
import com.requestmanagement.base.repository.NotificationRepository;
import com.requestmanagement.base.repository.PrioritizationRepository;
import com.requestmanagement.base.repository.RequestActivityRepository;
import com.requestmanagement.base.repository.UserRepository;
import com.requestmanagement.base.ui.notifications.NotificationCenter;
import com.requestmanagement.base.ui.shared.ActivityRecorder;
import com.requestmanagement.base.ui.shared.RequestScoreBadge;
import com.requestmanagement.base.ui.shared.Toast;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.select.Select;

import java.util.LinkedHashMap;
import java.util.Map;

/** Lets a developer estimate how hard a claimed task is; this recalculates its WSJF priority score. */
class EffortDialog extends Dialog {

    private static final Map<Integer, String> EFFORT_OPTIONS = new LinkedHashMap<>();

    static {
        EFFORT_OPTIONS.put(1, "1 - Çok Kolay");
        EFFORT_OPTIONS.put(2, "2 - Kolay");
        EFFORT_OPTIONS.put(3, "3 - Orta");
        EFFORT_OPTIONS.put(4, "4 - Zor");
        EFFORT_OPTIONS.put(5, "5 - Çok Zor");
    }

    EffortDialog(Workflow workflow, PrioritizationRepository prioritizationRepository,
                 NotificationRepository notificationRepository, UserRepository userRepository,
                 RequestActivityRepository activityRepository, Runnable onSaved) {
        setHeaderTitle("Efor Tahmini - Talep #" + workflow.getRequest().getRequestId());

        Prioritization prioritization = prioritizationRepository.findByRequest(workflow.getRequest()).orElseThrow();

        Select<Integer> effortSelect = new Select<>();
        effortSelect.setLabel("Bu iş ne kadar zor?");
        effortSelect.setItems(EFFORT_OPTIONS.keySet());
        effortSelect.setItemLabelGenerator(EFFORT_OPTIONS::get);
        effortSelect.setWidthFull();
        if (prioritization.getEffort() != null) {
            effortSelect.setValue(prioritization.getEffort());
        }

        Button saveButton = new Button("Kaydet", e -> save(workflow, prioritization, effortSelect.getValue(),
                prioritizationRepository, notificationRepository, userRepository, activityRepository, onSaved));
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelButton = new Button("İptal", e -> close());

        add(effortSelect);
        getFooter().add(saveButton, cancelButton);
    }

    private void save(Workflow workflow, Prioritization prioritization, Integer effort,
                       PrioritizationRepository prioritizationRepository,
                       NotificationRepository notificationRepository, UserRepository userRepository,
                       RequestActivityRepository activityRepository, Runnable onSaved) {
        if (effort == null) {
            Toast.show("Lütfen bir efor seviyesi seçin.");
            return;
        }
        prioritization.setEffort(effort);
        int score = RequestScoreBadge.compute(prioritization.getImpact(), prioritization.getUrgency(), effort);
        prioritization.setPriorityScore(score);
        prioritizationRepository.save(prioritization);

        ActivityRecorder.record(activityRepository, workflow.getRequest(),
                "Efor tahmini girildi, skor güncellendi: " + score);
        NotificationCenter.notifyProductOwner(notificationRepository, userRepository, workflow.getRequest(),
                workflow.getDeveloper(),
                "efor tahmini girdi, skor güncellendi: " + workflow.getRequest().getTitle());

        Toast.show("Efor tahmini kaydedildi.");
        onSaved.run();
        close();
    }
}
