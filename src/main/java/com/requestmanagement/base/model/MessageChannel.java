package com.requestmanagement.base.model;

/** Which conversation a {@link RequestMessage} belongs to: customer-facing, or developer-only. */
public enum MessageChannel {
    CUSTOMER_PO,
    INTERNAL
}
