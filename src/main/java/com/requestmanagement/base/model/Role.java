package com.requestmanagement.base.model;

/** The three user roles the system supports; drives both screen access and Spring Security authorities. */
public enum Role {
    CUSTOMER,
    PRODUCT_OWNER,
    DEVELOPER;

    public String displayLabel() {
        return switch (this) {
            case CUSTOMER -> "Müşteri";
            case PRODUCT_OWNER -> "Ürün Sorumlusu";
            case DEVELOPER -> "Yazılımcı";
        };
    }
}
