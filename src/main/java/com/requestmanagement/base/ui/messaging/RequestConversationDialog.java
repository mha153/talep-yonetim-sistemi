package com.requestmanagement.base.ui.messaging;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.MessageChannel;
import com.requestmanagement.base.model.Request;
import com.requestmanagement.base.model.RequestMessage;
import com.requestmanagement.base.repository.NotificationRepository;
import com.requestmanagement.base.repository.RequestMessageRepository;
import com.requestmanagement.base.repository.UserRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import java.util.List;

/** A simple message thread scoped to one request + one channel (customer↔PO or PO↔developer). */
public class RequestConversationDialog extends Dialog {

    public RequestConversationDialog(Request request, MessageChannel channel, String title,
                                      RequestMessageRepository messageRepository,
                                      NotificationRepository notificationRepository, UserRepository userRepository,
                                      AppUser currentUser, Runnable onMessageSent) {
        setHeaderTitle(title + " — Talep #" + request.getRequestId());
        setWidth("560px");

        markIncomingAsRead(request, channel, currentUser, messageRepository);
        onMessageSent.run();

        boolean customerInactive = MessageChannel.CUSTOMER_PO.equals(channel) && !request.getCustomer().isActive()
                && !currentUser.getUserId().equals(request.getCustomer().getUserId());
        if (customerInactive) {
            Span warning = new Span("Bu müşteri hesabı pasif; mesajınıza yanıt alamayabilirsiniz.");
            warning.getStyle().set("color", "#e65100").set("font-size", "0.85rem");
            add(warning);
        }

        VerticalLayout thread = new VerticalLayout();
        thread.setPadding(false);
        thread.setWidthFull();
        loadMessages(thread, request, channel, messageRepository, currentUser);

        Scroller scroller = new Scroller(thread);
        scroller.setWidthFull();
        scroller.setHeight("240px");

        TextField input = new TextField();
        input.setWidthFull();
        input.setPlaceholder("Mesajınızı yazın...");

        Button sendButton = new Button("Gönder", e -> {
            if (input.isEmpty()) {
                return;
            }
            RequestMessage message = new RequestMessage();
            message.setRequest(request);
            message.setAuthor(currentUser);
            message.setChannel(channel);
            message.setBody(input.getValue());
            messageRepository.save(message);
            MessageNotifier.notifyOtherParty(request, channel, currentUser, notificationRepository, userRepository);
            input.clear();
            thread.removeAll();
            loadMessages(thread, request, channel, messageRepository, currentUser);
            onMessageSent.run();
        });
        sendButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout inputRow = new HorizontalLayout(input, sendButton);
        inputRow.setWidthFull();
        inputRow.setFlexGrow(1, input);

        VerticalLayout content = new VerticalLayout(scroller, inputRow);
        content.setWidthFull();
        content.setPadding(false);
        add(content);
    }

    private void markIncomingAsRead(Request request, MessageChannel channel, AppUser currentUser,
                                     RequestMessageRepository messageRepository) {
        List<RequestMessage> unread = messageRepository.findByRequestAndChannelOrderByCreatedAtAsc(request, channel)
                .stream()
                .filter(m -> !m.isRead() && !m.getAuthor().getUserId().equals(currentUser.getUserId()))
                .toList();
        unread.forEach(m -> m.setRead(true));
        messageRepository.saveAll(unread);
    }

    private void loadMessages(VerticalLayout thread, Request request, MessageChannel channel,
                               RequestMessageRepository messageRepository, AppUser currentUser) {
        List<RequestMessage> messages =
                messageRepository.findByRequestAndChannelOrderByCreatedAtAsc(request, channel);
        if (messages.isEmpty()) {
            thread.add(new Span("Henüz mesaj yok."));
            return;
        }
        messages.forEach(m -> thread.add(MessageBubble.create(m, currentUser.getUserId())));
    }
}
