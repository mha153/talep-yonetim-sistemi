package com.requestmanagement.base.ui.developer;

import com.requestmanagement.base.model.Workflow;
import com.requestmanagement.base.repository.PrioritizationRepository;

import java.util.Comparator;
import java.util.List;

/** Sorts Sprint-pool workflow items by priority score, highest first. */
final class SprintPoolSorter {

    private SprintPoolSorter() {
    }

    static List<Workflow> byScoreDescending(List<Workflow> workflows,
                                             PrioritizationRepository prioritizationRepository) {
        return workflows.stream()
                .sorted(Comparator.comparingInt((Workflow w) -> scoreOf(w, prioritizationRepository)).reversed())
                .toList();
    }

    private static int scoreOf(Workflow workflow, PrioritizationRepository prioritizationRepository) {
        return prioritizationRepository.findByRequest(workflow.getRequest())
                .map(p -> p.getPriorityScore())
                .orElse(-1);
    }
}
