package com.requestmanagement.base.ui.po;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.Prioritization;
import com.requestmanagement.base.model.Request;
import com.requestmanagement.base.model.RequestStatus;
import com.requestmanagement.base.repository.NotificationRepository;
import com.requestmanagement.base.repository.PrioritizationRepository;
import com.requestmanagement.base.repository.RequestActivityRepository;
import com.requestmanagement.base.repository.RequestRepository;
import com.requestmanagement.base.ui.notifications.NotificationCenter;
import com.requestmanagement.base.ui.shared.ActivityRecorder;
import com.requestmanagement.base.ui.shared.RequestScoreBadge;
import com.requestmanagement.base.ui.shared.Toast;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import org.jspecify.annotations.Nullable;

/**
 * priority_score = WSJF-style base: (impact + urgency) / effort x 10 (max 100); effort defaults to 1 until a
 * developer sets it. The score shown elsewhere in the app also ages +3%/day while waiting, see RequestScoreBadge.
 */
public class PrioritizationDialog extends Dialog {

    public PrioritizationDialog(Request request, PrioritizationRepository prioritizationRepository,
                                 RequestRepository requestRepository, NotificationRepository notificationRepository,
                                 RequestActivityRepository activityRepository, AppUser currentPo, Runnable onSaved) {
        setHeaderTitle("Talep Değerlendirme & Önceliklendirme Giriş Ekranı - Talep #" + request.getRequestId());

        Prioritization existing = prioritizationRepository.findByRequest(request).orElse(null);
        Integer initialImpact = existing != null ? existing.getImpact() : null;
        Integer initialUrgency = existing != null ? existing.getUrgency() : null;

        Select<Integer> impactSelect = PriorityScaleOptions.buildSelect(
                "İş Etkisi (Impact) Seçimi", PriorityScaleOptions.IMPACT, initialImpact);
        Select<Integer> urgencySelect = PriorityScaleOptions.buildSelect(
                "Aciliyet (Urgency) Seçimi", PriorityScaleOptions.URGENCY, initialUrgency);

        PriorityScoreDisplay scoreDisplay = new PriorityScoreDisplay();
        Runnable refreshScore = () -> scoreDisplay.update(impactSelect.getValue(), urgencySelect.getValue());
        impactSelect.addValueChangeListener(e -> refreshScore.run());
        urgencySelect.addValueChangeListener(e -> refreshScore.run());
        refreshScore.run();

        Button saveButton = new Button("Değerleri Kaydet", e -> save(
                request, prioritizationRepository, requestRepository, notificationRepository, activityRepository,
                existing, impactSelect.getValue(), urgencySelect.getValue(), currentPo, onSaved));
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelButton = new Button("İptal", e -> close());

        HorizontalLayout content = new HorizontalLayout(
                new VerticalLayout(impactSelect, urgencySelect), scoreDisplay);
        content.setWidthFull();
        content.setFlexGrow(1, content.getComponentAt(0));

        add(new VerticalLayout(content, new HorizontalLayout(saveButton, cancelButton)));
    }

    private void save(Request request, PrioritizationRepository prioritizationRepository,
                       RequestRepository requestRepository, NotificationRepository notificationRepository,
                       RequestActivityRepository activityRepository, Prioritization existing,
                       @Nullable Integer impact, @Nullable Integer urgency, AppUser currentPo, Runnable onSaved) {
        if (impact == null || urgency == null) {
            Toast.show("Lütfen İş Etkisi ve Aciliyet seçimlerini tamamlayın.");
            return;
        }

        int effort = existing != null && existing.getEffort() != null
                ? existing.getEffort() : RequestScoreBadge.DEFAULT_EFFORT;
        int score = RequestScoreBadge.compute(impact, urgency, effort);
        Prioritization prioritization = existing != null ? existing : new Prioritization();
        prioritization.setRequest(request);
        prioritization.setImpact(impact);
        prioritization.setUrgency(urgency);
        prioritization.setPriorityScore(score);
        prioritizationRepository.save(prioritization);

        request.setStatus(RequestStatus.PRIORITIZED);
        requestRepository.save(request);
        ActivityRecorder.record(activityRepository, request, "Önceliklendirildi (skor: " + score + ")");
        NotificationCenter.notifyCustomer(notificationRepository, request, currentPo,
                "talebinizi önceliklendirdi (skor: " + score + "): " + request.getTitle());

        Toast.show("Talep önceliklendirildi ve skor hesaplandı.");
        onSaved.run();
        close();
    }
}
