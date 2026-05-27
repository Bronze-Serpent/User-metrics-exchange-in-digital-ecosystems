package com.barabanov.metricsExchange.service;

import com.barabanov.metricsExchange.entity.AllianceEntity;
import com.barabanov.metricsExchange.entity.AlliancePointEntity;
import com.barabanov.metricsExchange.entity.CompanyPointEntity;
import com.barabanov.metricsExchange.entity.PointStatus;
import com.barabanov.metricsExchange.external.UserMetricsWebClient;
import com.barabanov.metricsExchange.interfaces.rest.dto.AlliancePointCreateDto;
import com.barabanov.metricsExchange.interfaces.rest.dto.AlliancePointDto;
import com.barabanov.metricsExchange.interfaces.rest.dto.AlliancePointUpdateDto;
import com.barabanov.metricsExchange.mapper.AlliancePointMapper;
import com.barabanov.metricsExchange.mapper.PredicateDataMapper;
import com.barabanov.metricsExchange.repository.AlliancePointRepository;
import com.barabanov.metricsExchange.repository.AllianceRepository;
import com.barabanov.metricsExchange.repository.CompanyPointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class AlliancePointService {

    private final ExecutorService userMetricsRequestsExecutorService;
    @Value("${user-metrics-request.passed-parameters:usrLogin, usrEmail}")
    private final Set<String> userMetricsRqPassedParameters;
    private final UserMetricsWebClient userMetricsWebClient;
    private final AlliancePointMapper alliancePointMapper;
    private final PredicateDataMapper predicateDataMapper;
    private final AlliancePointRepository alliancePointRepository;
    private final AllianceRepository allianceRepository;
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
                    -> userMetricsWebClient.getUserMetricsFrom(companyPoint.getUrl(), parametersForUserMetricsRq)));

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
        creatingAlliancePoint.setStatus(PointStatus.NEW);
        AllianceEntity alliance = allianceRepository.findById(alliancePointCreateDto.getAllianceId())
                .orElseThrow(() -> new RuntimeException(String.format("Не удалось найти альянс с id: %s", alliancePointCreateDto.getAllianceId())));
        creatingAlliancePoint.setAlliance(alliance);

        return alliancePointMapper.mapToAlliancePointDto(alliancePointRepository.save(creatingAlliancePoint));
    }


    @Transactional(readOnly = true)
    public AlliancePointDto getAlliancePointInfo(Long alliancePointId) {
        return alliancePointRepository.findById(alliancePointId)
                .map(alliancePointMapper::mapToAlliancePointDto)
                .orElseThrow(() -> new RuntimeException(String.format("Не удалось найти точку альянса с id: %s", alliancePointId)));
    }


    @Transactional
    public AlliancePointDto updateAlliancePointDto(Long alliancePointId, AlliancePointUpdateDto alliancePointUpdateDto) {
        AlliancePointEntity updatingAlliancePoint = alliancePointRepository.findById(alliancePointId)
                .orElseThrow(() -> new RuntimeException(String.format("Не удалось найти точку альянса с id: %s", alliancePointId)));
        alliancePointMapper.mergeUpdateToEntity(alliancePointUpdateDto, updatingAlliancePoint);

        return alliancePointMapper.mapToAlliancePointDto(alliancePointRepository.save(updatingAlliancePoint));
    }


    @Transactional
    public void deleteAlliancePointInfo(Long alliancePointId) {
        alliancePointRepository.deleteById(alliancePointId);
    }


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
