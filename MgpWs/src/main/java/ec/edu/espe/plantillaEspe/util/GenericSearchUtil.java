package ec.edu.espe.plantillaEspe.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GenericSearchUtil {
    
    public static <T> Page<T> search(List<T> items, Map<String, String> searchCriteria, Pageable pageable) {
        List<T> filteredItems = items.stream()
            .filter(item -> matchesCriteria(item, searchCriteria))
            .collect(Collectors.toList());
    
        // Ordenar según el pageable
        if (pageable.getSort().isSorted()) {
            for (org.springframework.data.domain.Sort.Order order : pageable.getSort()) {
                filteredItems = filteredItems.stream()
                    .sorted((a, b) -> {
                        try {
                            String aValue = getFieldValue(a, order.getProperty());
                            String bValue = getFieldValue(b, order.getProperty());
                            if (aValue == null) aValue = "";
                            if (bValue == null) bValue = "";
                            int cmp = aValue.compareToIgnoreCase(bValue);
                            return order.isAscending() ? cmp : -cmp;
                        } catch (Exception e) {
                            return 0;
                        }
                    })
                    .collect(Collectors.toList());
            }
        }
    
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredItems.size());
    
        if (start > filteredItems.size()) {
            return new PageImpl<>(List.of(), pageable, filteredItems.size());
        }
    
        return new PageImpl<>(filteredItems.subList(start, end), pageable, filteredItems.size());
    }

    private static <T> boolean matchesCriteria(T item, Map<String, String> searchCriteria) {
        return searchCriteria.entrySet().stream()
            .allMatch(entry -> {
                try {
                    String fieldValue = getFieldValue(item, entry.getKey());
                    String searchValue = entry.getValue().toLowerCase();
                    return fieldValue != null && fieldValue.toLowerCase().contains(searchValue);
                } catch (Exception e) {
                    return false;
                }
            });
    }

    private static <T> String getFieldValue(T item, String fieldName) {
        try {
            java.lang.reflect.Method getter = item.getClass().getMethod("get" + 
                fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1));
            Object value = getter.invoke(item);
            return value != null ? value.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }
} 