package com.requestmanagement.base.ui.shared;

import java.util.List;
import java.util.function.Function;

/** Filters a list of items by customer name or title, case-insensitively. */
public final class RequestSearchFilter {

    private RequestSearchFilter() {
    }

    public static <T> List<T> apply(List<T> items, String searchText, Function<T, String> customerName,
                                     Function<T, String> title) {
        if (searchText == null || searchText.isBlank()) {
            return items;
        }
        String lower = searchText.toLowerCase();
        return items.stream()
                .filter(item -> customerName.apply(item).toLowerCase().contains(lower)
                        || title.apply(item).toLowerCase().contains(lower))
                .toList();
    }
}
