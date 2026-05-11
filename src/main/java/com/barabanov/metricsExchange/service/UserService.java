package com.barabanov.metricsExchange.service;

import com.barabanov.metricsExchange.entity.UserEntity;
import com.barabanov.metricsExchange.interfaces.rest.dto.CreateUserDto;
import com.barabanov.metricsExchange.interfaces.rest.dto.PageResponse;
import com.barabanov.metricsExchange.interfaces.rest.dto.UserPageRequest;
import com.barabanov.metricsExchange.mapper.PredicateDataMapper;
import com.barabanov.metricsExchange.mapper.UserMapper;
import com.barabanov.metricsExchange.repository.CompanyRepository;
import com.barabanov.metricsExchange.repository.UserRepository;
import com.barabanov.metricsExchange.interfaces.rest.dto.UserDto;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final UserMapper userMapper;
    private final PredicateDataMapper predicateDataMapper;


    @Transactional
    public UserDto createUser(CreateUserDto createUserDto) {
        UserEntity creatingUser = userMapper.mapToEntity(createUserDto);
        creatingUser.setLinkedCompany(Optional.ofNullable(createUserDto.getLinkedCompanyId())
                .flatMap(companyRepository::findById)
                .orElse(null));

        return userMapper.toUserDto(userRepository.save(creatingUser));
    }


    @Transactional(readOnly = true)
    public PageResponse<UserDto> getUserPage(UserPageRequest userPageRequest) {
        Predicate predicate = predicateDataMapper.mapUserFilterToPredicate(userPageRequest.getUserFilter());

        PageRequest pageRequest = PageRequest.of(Optional.ofNullable(userPageRequest.getPageNumber())
                .orElseThrow(), Optional.ofNullable(userPageRequest.getPageSize())
                .orElseThrow());
        Page<UserEntity> usersPage = userRepository.findAll(predicate, pageRequest);

        return PageResponse.<UserDto>builder()
                .data(usersPage.getContent().stream().map(userMapper::toUserDto)
                        .toList())
                .pageNumber(usersPage.getNumber())
                .totalElements(usersPage.getTotalElements())
                .totalPages(usersPage.getTotalPages())
                .build();
    }


    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
