package com.requestmanagement.base.model;

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
