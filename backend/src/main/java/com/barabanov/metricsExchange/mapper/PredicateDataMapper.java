package com.barabanov.metricsExchange.mapper;

import com.barabanov.metricsExchange.interfaces.rest.dto.*;
import com.barabanov.metricsExchange.utils.QPredicates;
import com.querydsl.core.types.Predicate;
import org.mapstruct.Mapper;

import static com.barabanov.metricsExchange.entity.QAllianceEntity.allianceEntity;
import static com.barabanov.metricsExchange.entity.QCompanyEntity.companyEntity;
import static com.barabanov.metricsExchange.entity.QCompanyPointEntity.companyPointEntity;
import static com.barabanov.metricsExchange.entity.QTransferRequestEntity.transferRequestEntity;
import static com.barabanov.metricsExchange.entity.QUserEntity.userEntity;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;


@Mapper(componentModel = SPRING)
public interface PredicateDataMapper {


    default QPredicates mapUserFilterToPredicate(UserFilter userFilter) {
        if (userFilter == null)
            return QPredicates.builder();

        return QPredicates.builder()
                .add(userFilter.getUserId(), userEntity.id::eq)
                .add(userFilter.getUserEmailSubstr(), userEntity.email::contains)
                .add(userFilter.getUserRole(), userEntity.role::in)
                .add(userFilter.getLinkedCompanyId(), userEntity.linkedCompany.id::eq);
    }


    default Predicate mapCompanyFilterToPredicate(CompanyFilter companyFilter) {
        if (companyFilter == null)
            return QPredicates.builder().build();

        return QPredicates.builder()
                .add(companyFilter.getCompanyNameSubstring(), companyEntity.name::contains)
                .add(companyFilter.getCompanyId(), companyEntity.id::eq)
                .add(companyFilter.getSuppUserProfileExchange(), companyEntity.suppUserProfileExchange::eq)
                .build();
    }


    default QPredicates mapCompanyPointFilterToPredicate(CompanyPointFilter companyPointFilter) {
        if (companyPointFilter == null)
            return QPredicates.builder();

        return QPredicates.builder()
                .add(companyPointFilter.getCompanyId(), companyPointEntity.company.id::eq)
                .add(companyPointFilter.getAlliancePointId(), companyPointEntity.alliancePoint.id::eq);
    }


    default Predicate mapAllianceFilterToPredicate(AllianceFilter allianceFilter) {
        if (allianceFilter == null)
            return QPredicates.builder().build();

        return QPredicates.builder()
                .add(allianceFilter.getAllianceId(), allianceEntity.id::eq)
                .add(allianceFilter.getAllianceNameSubstring(), allianceEntity.name::contains)
                .build();
    }


    default QPredicates mapTransferFilterToQPredicates(TransferFilter transferFilter) {
        if (transferFilter == null)
            return QPredicates.builder();

        return QPredicates.builder()
                .add(transferFilter.getId(), transferRequestEntity.id::eq)
                .add(transferFilter.getFromProfileId(), transferRequestEntity.fromProfileId::eq)
                .add(transferFilter.getToProfileId(), transferRequestEntity.toProfileId::eq)
                .add(transferFilter.getStatuses(), transferRequestEntity.status::in)
                .add(transferFilter.getDecisions(), transferRequestEntity.decision::in)
                .add(transferFilter.getFromCompanyId(), transferRequestEntity.fromCompany.id::eq)
                .add(transferFilter.getToCompanyId(), transferRequestEntity.toCompany.id::eq);
    }
}
