package com.barabanov.metricsExchange.mapper;

import com.barabanov.metricsExchange.entity.UserEntity;
import com.barabanov.metricsExchange.interfaces.rest.dto.SortOrder;
import com.barabanov.metricsExchange.interfaces.rest.dto.SortSpecifier;
import com.barabanov.metricsExchange.interfaces.rest.dto.UserSortField;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;


@Mapper(componentModel = SPRING)
public interface SortDataMapper {


    default Sort mapUserSortSpecifiersToSpringSort(List<SortSpecifier<UserSortField>> userSortOrderSpecifiers) {
        if (CollectionUtils.isEmpty(userSortOrderSpecifiers))
            return Sort.unsorted();

        Sort userEntitySort = Sort.sort(UserEntity.class);
        for (SortSpecifier<UserSortField> userSortOrderSpecifier : userSortOrderSpecifiers)
            userEntitySort = userEntitySort.and(mapUserOrderSpecifierToSpringSort(userSortOrderSpecifier));

        return userEntitySort;
    }


    private Sort mapUserOrderSpecifierToSpringSort(SortSpecifier<UserSortField> userSortOrderSpecifier) {
        if (userSortOrderSpecifier == null)
            throw new IllegalArgumentException("Элемент массива userSortOrderSpecifier не может быть null");
        if (userSortOrderSpecifier.getSortBy() == null)
            throw new IllegalArgumentException("У элемента массива userSortOrderSpecifier поле sortBy не может быть null");

        Sort.TypedSort<UserEntity> userEntityTypeSort = Sort.sort(UserEntity.class);
        Sort.TypedSort<?> userEntitySort = switch (userSortOrderSpecifier.getSortBy()) {
            case ROLE -> userEntityTypeSort.by(UserEntity::getRole);
            case EMAIL -> userEntityTypeSort.by(UserEntity::getEmail);
            case CREATED_AT -> userEntityTypeSort.by(UserEntity::getCreatedAt);
        };

        return userSortOrderSpecifier.getSortOrder() == null || userSortOrderSpecifier.getSortOrder() == SortOrder.ASC
                ? userEntitySort.ascending()
                : userEntitySort.descending();
    }
}
