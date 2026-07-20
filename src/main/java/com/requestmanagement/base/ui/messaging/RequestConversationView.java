package com.requestmanagement.base.ui.messaging;

import com.requestmanagement.base.model.MessageChannel;
import com.requestmanagement.base.model.Request;
import com.requestmanagement.base.model.RequestMessage;
import com.requestmanagement.base.repository.RequestMessageRepository;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.List;

/** Read-only view of a message thread — lets a developer see the customer/PO conversation without joining it. */
public class RequestConversationView extends Dialog {

    public RequestConversationView(Request request, MessageChannel channel, String title,
                                    RequestMessageRepository messageRepository) {
        setHeaderTitle(title + " — Talep #" + request.getRequestId());
        setWidth("420px");

        VerticalLayout thread = new VerticalLayout();
        thread.setPadding(false);
        List<RequestMessage> messages =
                messageRepository.findByRequestAndChannelOrderByCreatedAtAsc(request, channel);
        if (messages.isEmpty()) {
            thread.add(new Span("Henüz mesaj yok."));
        } else {
            messages.forEach(m -> thread.add(new Span(m.getAuthor().getNameSurname() + ": " + m.getBody())));
        }

        Scroller scroller = new Scroller(thread);
        scroller.setHeight("240px");
        add(scroller);
    }
}
