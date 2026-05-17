package com.barabanov.metricsExchange.mapper;

import com.barabanov.metricsExchange.interfaces.rest.dto.*;
import com.barabanov.metricsExchange.utils.QPredicates;
import com.querydsl.core.types.Predicate;
import org.mapstruct.Mapper;

import static com.barabanov.metricsExchange.entity.QAllianceEntity.allianceEntity;
import static com.barabanov.metricsExchange.entity.QAlliancePointEntity.alliancePointEntity;
import static com.barabanov.metricsExchange.entity.QCompanyEntity.companyEntity;
import static com.barabanov.metricsExchange.entity.QCompanyPointEntity.companyPointEntity;
import static com.barabanov.metricsExchange.entity.QTransferRequestEntity.transferRequestEntity;
import static com.barabanov.metricsExchange.entity.QUserEntity.userEntity;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;


@Mapper(componentModel = SPRING)
public interface PredicateDataMapper {


    default Predicate mapUserFilterToPredicate(UserFilter userFilter) {
        if (userFilter == null)
            return QPredicates.builder().build();

        return QPredicates.builder()
                .add(userFilter.getUserRole(), userEntity.role::in)
                .add(userFilter.getLinkedCompanyId(), userEntity.linkedCompany.id::eq)
                .build();
    }

    default Predicate mapCompanyFilterToPredicate(CompanyFilter companyFilter) {
        if (companyFilter == null)
            return QPredicates.builder().build();

        return QPredicates.builder()
                .add(companyFilter.getCompanyNameSubstring(), companyEntity.name::contains)
                .add(companyFilter.getSuppUserProfileExchange(), companyEntity.suppUserProfileExchange::eq)
                .build();
    }


    default Predicate mapCompanyPointFilterToPredicate(CompanyPointFilter companyPointFilter) {
        if (companyPointFilter == null)
            return QPredicates.builder().build();

        return QPredicates.builder()
                .add(companyPointFilter.getCompanyId(), companyPointEntity.company.id::eq)
                .add(companyPointFilter.getAlliancePointId(), companyPointEntity.alliancePoint.id::eq)
                .build();
    }


    default Predicate mapAllianceFilterToPredicate(AllianceFilter allianceFilter) {
        if (allianceFilter == null)
            return QPredicates.builder().build();

        return QPredicates.builder()
                .add(allianceFilter.getAllianceId(), allianceEntity.id::eq)
                .add(allianceFilter.getAllianceNameSubstring(), allianceEntity.name::contains)
                .build();
    }


    default Predicate mapTransferFilterToPredicate(TransferFilter transferFilter) {
        if (transferFilter == null)
            return QPredicates.builder().build();

        return QPredicates.builder()
                .add(transferFilter.getId(), transferRequestEntity.id::eq)
                .add(transferFilter.getFromProfileId(), transferRequestEntity.fromProfileId::eq)
                .add(transferFilter.getToProfileId(), transferRequestEntity.toProfileId::eq)
                .add(transferFilter.getDecisions(), transferRequestEntity.decision::in)
                .add(transferFilter.getFromCompanyId(), transferRequestEntity.fromCompany.id::eq)
                .add(transferFilter.getToCompanyId(), transferRequestEntity.toCompany.id::eq)
                .build();
    }

    default Predicate mapAlliancePointFilterToPredicate(AlliancePointFilter alliancePointFilter) {
        if (alliancePointFilter == null)
            return QPredicates.builder().build();

        return QPredicates.builder()
                .add(alliancePointFilter.getId(), alliancePointEntity.id::eq)
                .add(alliancePointFilter.getAllianceId(), alliancePointEntity.alliance.id::eq)
                .add(alliancePointFilter.getStatuses(), alliancePointEntity.status::in)
                .build();
    }
}
