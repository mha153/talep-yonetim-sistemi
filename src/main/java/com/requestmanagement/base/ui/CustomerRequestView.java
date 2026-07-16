package com.requestmanagement.base.ui;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.Request;
import com.requestmanagement.base.model.Role;
import com.requestmanagement.base.repository.RequestRepository;
import com.requestmanagement.base.repository.UserRepository;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@Route(value = "create-request", layout = MainLayout.class)
@RolesAllowed("CUSTOMER")
public class CustomerRequestView extends VerticalLayout {

    private static final String EMAIL_DOMAIN = "@requestmanagement.local";

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final RequestHistoryGrid historyGrid;

    public CustomerRequestView(RequestRepository requestRepository, UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        setWidthFull();

        RequestFormPanel formPanel = new RequestFormPanel(requestRepository, this::getCurrentCustomer, this::refreshHistory);
        historyGrid = new RequestHistoryGrid(requestRepository, this::currentCustomerRequests);

        add(formPanel, new Hr(), historyGrid);
    }

    private void refreshHistory() {
        historyGrid.refresh();
    }

    private List<Request> currentCustomerRequests() {
        return requestRepository.findByCustomer_Email(getCurrentCustomer().getEmail());
    }

    private AppUser getCurrentCustomer() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String email = username + EMAIL_DOMAIN;
        return userRepository.findByEmail(email).orElseGet(() -> {
            AppUser newUser = new AppUser();
            newUser.setEmail(email);
            newUser.setNameSurname(username);
            newUser.setRole(Role.CUSTOMER);
            return userRepository.save(newUser);
        });
    }
}
