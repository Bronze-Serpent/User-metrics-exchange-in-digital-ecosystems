package com.barabanov.metricsExchange.service;

import com.barabanov.metricsExchange.entity.AlliancePointEntity;
import com.barabanov.metricsExchange.entity.CompanyPointEntity;
import com.barabanov.metricsExchange.interfaces.rest.dto.AlliancePointCreateDto;
import com.barabanov.metricsExchange.interfaces.rest.dto.AlliancePointDto;
import com.barabanov.metricsExchange.interfaces.rest.dto.AlliancePointPageRequest;
import com.barabanov.metricsExchange.interfaces.rest.dto.PageResponse;
import com.barabanov.metricsExchange.mapper.AlliancePointMapper;
import com.barabanov.metricsExchange.mapper.PredicateDataMapper;
import com.barabanov.metricsExchange.repository.AlliancePointRepository;
import com.barabanov.metricsExchange.repository.CompanyPointRepository;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;


//TODO: ещё всю кафку реализовать

@Slf4j
@RequiredArgsConstructor
@Service
public class AlliancePointService {

    //TODO: сконфигурировать это
    private final ExecutorService userMetricsRequestsExecutorService;
    @Value("${user-metrics-request.passed-parameters:usrLogin, usrEmail}")
    private final Set<String> userMetricsRqPassedParameters;
    private final UserMetricsWebService userMetricsWebService;
    private final AlliancePointMapper alliancePointMapper;
    private final PredicateDataMapper predicateDataMapper;
    private final AlliancePointRepository alliancePointRepository;
    private final TransactionTemplate transactionTemplate;
    private final CompanyPointRepository companyPointRepository;


    public UserMetricsDto getUserMetricsFrom(Long alliancePointId, MultiValueMap<String, String> requesterParameters) {
        // TODO: сделать с fetch запросом на точки компаний
        AlliancePointEntity alliancePoint = transactionTemplate.execute(
                transactionStatus -> alliancePointRepository.findById(alliancePointId)
                        .orElseThrow(() -> new RuntimeException(String.format("Не удалось найти точку с  id: %s", alliancePointId))));

        MultiValueMap<String, String> parametersForUserMetricsRq = requesterParameters.entrySet().stream()
                .filter(requestParameterEntry -> userMetricsRqPassedParameters.contains(requestParameterEntry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (list1, list2) -> {
                            list1.addAll(list2);
                            return list1;
                        }, LinkedMultiValueMap::new));

        List<Future<String>> userMetricFutures = new ArrayList<>();
        for (CompanyPointEntity companyPoint : alliancePoint.getCompanyPoints())
            userMetricFutures.add(userMetricsRequestsExecutorService.submit(()
                    -> userMetricsWebService.getUserMetricsFrom(companyPoint.getUrl(), parametersForUserMetricsRq)));

        return UserMetricsDto.builder()
                .metrics(userMetricFutures.stream()
                        .map(userMetricsJsonFuture -> {
                            try {
                                return userMetricsJsonFuture.get();
                            } catch (InterruptedException e) {
                                //TODO: прочитать про прерывания в Java и как и на каком уровне их обрабатывать. Прочитать что случается с потоком прерваным и с тем который завершился с ошибкой. вовзращаются ли они в пул и как-то очищаются или в поле создаётся новый вместо этого
                                Thread.currentThread().interrupt(); // Восстановление флага
                                throw new RuntimeException(
                                        String.format("Получение метрик пользователя для точки альянса с id: %s было прервано", alliancePointId), e);
                            } catch (ExecutionException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .toList()
                )
                .build();

    }

    @Transactional
    public AlliancePointDto createAlliancePoint(AlliancePointCreateDto alliancePointCreateDto) {
        AlliancePointEntity creatingAlliancePoint = alliancePointMapper.mapToEntity(alliancePointCreateDto);

        return alliancePointMapper.mapToAlliancePointDto(alliancePointRepository.save(creatingAlliancePoint));
    }


    @Transactional(readOnly = true)
    public AlliancePointDto getAlliancePointInfo(Long alliancePointId) {
        return alliancePointRepository.findById(alliancePointId)
                .map(alliancePointMapper::mapToAlliancePointDto)
                .orElseThrow(() -> new RuntimeException(String.format("Не удалось найти точку альянса с id: %s", alliancePointId)));
    }


    @Transactional
    public void deleteAlliancePointInfo(Long alliancePointId) {
        alliancePointRepository.deleteById(alliancePointId);
    }


    @Transactional(readOnly = true)
    public PageResponse<AlliancePointDto> getAlliancePointsPage(AlliancePointPageRequest alliancePointPageRequest) {
        Predicate predicate = predicateDataMapper.mapAlliancePointFilterToPredicate(alliancePointPageRequest.getAlliancePointFilter());

        PageRequest pageRequest = PageRequest.of(Optional.ofNullable(alliancePointPageRequest.getPageNumber())
                .orElseThrow(), Optional.ofNullable(alliancePointPageRequest.getPageSize())
                .orElseThrow());
        Page<AlliancePointEntity> alliancePointsPage = alliancePointRepository.findAll(predicate, pageRequest);


        return PageResponse.<AlliancePointDto>builder()
                .data(alliancePointsPage.getContent().stream().map(alliancePointMapper::mapToAlliancePointDto)
                        .toList())
                .pageNumber(alliancePointsPage.getNumber())
                .totalElements(alliancePointsPage.getTotalElements())
                .totalPages(alliancePointsPage.getTotalPages())
                .build();
    }


    //TODO: сделать флаг позволяет ли выгружать компания портфолио о поле для сохранения урла для вызова выгрузки в кафку портфолио. Вызывать эту функцию у компании в случае сли приняли решение ACCEPT
    @Transactional
    public AlliancePointDto addCompanyPointFor(Long alliancePointId, Long companyPointId) {
        AlliancePointEntity alliancePoint = alliancePointRepository.findById(alliancePointId)
                .orElseThrow(() -> new RuntimeException(String.format("Не удалось найти точку альянса с id: %s", alliancePointId)));

        CompanyPointEntity companyEntity = companyPointRepository.findById(companyPointId)
                .orElseThrow(() -> new RuntimeException(String.format("Не удалось найти точку компании с id: %s", alliancePointId)));
        companyPointRepository.save(companyEntity);

        companyEntity.setAlliancePoint(alliancePoint);
        return alliancePointMapper.mapToAlliancePointDto(alliancePoint);
    }


    @Transactional
    public AlliancePointDto deleteCompanyPointFrom(Long alliancePointId, Long companyPointId) {
        CompanyPointEntity companyPointEntity = companyPointRepository.findById(companyPointId)
                .orElseThrow(() -> new RuntimeException(String.format("Не удалось найти точку компании с id: %s", alliancePointId)));

        AlliancePointEntity alliancePoint = companyPointEntity.getAlliancePoint();
        if (alliancePoint == null || !alliancePointId.equals(alliancePoint.getId()))
            throw new RuntimeException(String.format("Точка компании с id: %s не связана с точкой альянса с id: %s",
                    companyPointId, alliancePointId));

        companyPointEntity.setAlliancePoint(null);
        companyPointRepository.save(companyPointEntity);

        return alliancePointMapper.mapToAlliancePointDto(alliancePoint);
    }
}
