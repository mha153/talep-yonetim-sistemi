package com.requestmanagement.base;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.Prioritization;
import com.requestmanagement.base.model.Request;
import com.requestmanagement.base.model.Role;
import com.requestmanagement.base.model.Workflow;
import com.requestmanagement.base.model.WorkflowStatus;
import com.requestmanagement.base.repository.PrioritizationRepository;
import com.requestmanagement.base.repository.RequestRepository;
import com.requestmanagement.base.repository.UserRepository;
import com.requestmanagement.base.repository.WorkflowRepository;
import org.jspecify.annotations.Nullable;

import java.util.Map;

/** Converts a single MUSTAFA_REQUESTS_LEGACY row into Request/Prioritization/Workflow rows. */
class LegacyRowConverter {

    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final PrioritizationRepository prioritizationRepository;
    private final WorkflowRepository workflowRepository;

    LegacyRowConverter(UserRepository userRepository, RequestRepository requestRepository,
                        PrioritizationRepository prioritizationRepository, WorkflowRepository workflowRepository) {
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.prioritizationRepository = prioritizationRepository;
        this.workflowRepository = workflowRepository;
    }

    void convert(Map<String, Object> row, Map<String, AppUser> customersByName) {
        String customerName = str(row.get("MUSTERI"));
        String title = str(row.get("BASLIK"));
        String description = str(row.get("ACIKLAMA"));
        String status = str(row.get("DURUM"));
        Number urgency = (Number) row.get("ACILIYET");
        Number impact = (Number) row.get("ETKI");

        Request request = new Request();
        request.setCustomer(resolveCustomer(customerName, customersByName));
        request.setTitle(title != null && !title.isBlank() ? title : "(başlıksız)");
        request.setDescription(description != null && !description.isBlank() ? description : "(açıklama girilmemiş)");
        request.setStatus(LegacyStatusMapper.toRequestStatus(status));
        request = requestRepository.save(request);

        savePrioritization(request, urgency, impact);
        saveWorkflow(request, LegacyStatusMapper.toWorkflowStatus(status));
    }

    private void savePrioritization(Request request, @Nullable Number urgency, @Nullable Number impact) {
        if (urgency == null || impact == null) {
            return;
        }
        Prioritization prioritization = new Prioritization();
        prioritization.setRequest(request);
        prioritization.setUrgency(urgency.intValue());
        prioritization.setImpact(impact.intValue());
        prioritization.setPriorityScore(urgency.intValue() * impact.intValue());
        prioritizationRepository.save(prioritization);
    }

    private void saveWorkflow(Request request, @Nullable WorkflowStatus workflowStatus) {
        if (workflowStatus == null) {
            return;
        }
        Workflow workflow = new Workflow();
        workflow.setRequest(request);
        workflow.setWorkflowStatus(workflowStatus);
        workflowRepository.save(workflow);
    }

    private AppUser resolveCustomer(@Nullable String customerName, Map<String, AppUser> customersByName) {
        String name = customerName != null && !customerName.isBlank() ? customerName : "Bilinmeyen Müşteri";
        return customersByName.computeIfAbsent(name, n -> {
            AppUser user = new AppUser();
            user.setNameSurname(n);
            user.setEmail(LegacyStatusMapper.slugify(n) + "@migrated.local");
            user.setRole(Role.CUSTOMER);
            return userRepository.save(user);
        });
    }

    private @Nullable String str(@Nullable Object value) {
        return value == null ? null : value.toString();
    }
}
