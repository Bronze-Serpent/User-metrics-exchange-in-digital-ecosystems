package com.barabanov.metricsExchange.mapper;

import com.barabanov.metricsExchange.entity.TransferRequestEntity;
import com.barabanov.metricsExchange.entity.UserEntity;
import com.barabanov.metricsExchange.interfaces.rest.dto.SortOrder;
import com.barabanov.metricsExchange.interfaces.rest.dto.SortSpecifier;
import com.barabanov.metricsExchange.interfaces.rest.dto.TransferRequestSortField;
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

    default Sort mapTransferRqSortSpecifiersToSpringSort(List<SortSpecifier<TransferRequestSortField>> transferRqSortOrderSpecifiers) {
        if (CollectionUtils.isEmpty(transferRqSortOrderSpecifiers))
            return Sort.unsorted();

        Sort transferRqSort = Sort.sort(TransferRequestEntity.class);
        for (SortSpecifier<TransferRequestSortField> transferRqOrderSpecifier : transferRqSortOrderSpecifiers)
            transferRqSort = transferRqSort.and(mapTransferRqOrderSpecifierToSpringSort(transferRqOrderSpecifier));

        return transferRqSort;
    }


    private Sort mapTransferRqOrderSpecifierToSpringSort(SortSpecifier<TransferRequestSortField> transferRqOrderSpecifier) {
        if (transferRqOrderSpecifier == null)
            throw new IllegalArgumentException("Переданный userSortOrderSpecifier не может быть null");

        Sort.TypedSort<TransferRequestEntity> transferRqTypedSort = Sort.sort(TransferRequestEntity.class);
        Sort.TypedSort<?> transferRqSort = switch (transferRqOrderSpecifier.getSortBy()) {
            case CREATED_AT -> transferRqTypedSort.by(TransferRequestEntity::getCreatedAt);
            case STATUS -> transferRqTypedSort.by(TransferRequestEntity::getStatus);
            case null ->
                    throw new IllegalArgumentException("У переданного transferRqOrderSpecifier поле sortBy не может быть null");
        };

        return transferRqOrderSpecifier.getSortOrder() == null || transferRqOrderSpecifier.getSortOrder() == SortOrder.ASC
                ? transferRqSort.ascending()
                : transferRqSort.descending();
    }


    private Sort mapUserOrderSpecifierToSpringSort(SortSpecifier<UserSortField> userSortOrderSpecifier) {
        if (userSortOrderSpecifier == null)
            throw new IllegalArgumentException("Переданный userSortOrderSpecifier не может быть null");

        Sort.TypedSort<UserEntity> userEntityTypeSort = Sort.sort(UserEntity.class);
        Sort.TypedSort<?> userEntitySort = switch (userSortOrderSpecifier.getSortBy()) {
            case ROLE -> userEntityTypeSort.by(UserEntity::getRole);
            case EMAIL -> userEntityTypeSort.by(UserEntity::getEmail);
            case CREATED_AT -> userEntityTypeSort.by(UserEntity::getCreatedAt);
            case null ->
                    throw new IllegalArgumentException("У переданного userSortOrderSpecifier поле sortBy не может быть null");
        };

        return userSortOrderSpecifier.getSortOrder() == null || userSortOrderSpecifier.getSortOrder() == SortOrder.ASC
                ? userEntitySort.ascending()
                : userEntitySort.descending();
    }
}
