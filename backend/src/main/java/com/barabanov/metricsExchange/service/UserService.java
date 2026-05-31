package com.barabanov.metricsExchange.service;

import com.barabanov.metricsExchange.entity.CompanyEntity;
import com.barabanov.metricsExchange.entity.UserEntity;
import com.barabanov.metricsExchange.entity.UserRole;
import com.barabanov.metricsExchange.interfaces.rest.dto.*;
import com.barabanov.metricsExchange.mapper.PredicateDataMapper;
import com.barabanov.metricsExchange.mapper.SortDataMapper;
import com.barabanov.metricsExchange.mapper.UserMapper;
import com.barabanov.metricsExchange.repository.CompanyRepository;
import com.barabanov.metricsExchange.repository.UserRepository;
import com.barabanov.metricsExchange.utils.QPredicates;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.UUID;

import static com.barabanov.metricsExchange.entity.QUserEntity.userEntity;
import static com.barabanov.metricsExchange.utils.DataExtractionUtils.getUserRole;


@Slf4j
@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final UserMapper userMapper;
    private final PredicateDataMapper predicateDataMapper;
    private final SortDataMapper sortDataMapper;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public UserDto createClient(UserRegisterDto userRegisterDto) {
        UserEntity creatingUser = userMapper.mapToEntity(userRegisterDto);
        creatingUser.setRole(UserRole.CLIENT);
        creatingUser.setPasswordHash(Optional.ofNullable(userRegisterDto.getPassword())
                .filter(StringUtils::hasText)
                .map(passwordEncoder::encode)
                .orElseThrow(() -> new IllegalArgumentException("Пароль не может быть пустым при создании пользователя")));

        return userMapper.toUserDto(userRepository.save(creatingUser));
    }


    @Transactional
    public UserCreatedDto createUser(CreateUserDto createUserDto, UserDetails creatorUserDetails) {

        CompanyEntity linkedCompanyEntity;
        if (getUserRole(creatorUserDetails) == UserRole.COMPANY_AGENT) {
            Optional<String> creatorEmailOptional = Optional.ofNullable(creatorUserDetails)
                    .map(UserDetails::getUsername);

            linkedCompanyEntity = creatorEmailOptional.flatMap(userRepository::findByEmail)
                    .map(UserEntity::getLinkedCompany)
                    .orElseThrow(() -> new RuntimeException(String.format("Не удалось найти компанию связанную с email: %s",
                            creatorEmailOptional.orElse(null))));
        } else
            linkedCompanyEntity = Optional.ofNullable(createUserDto.getLinkedCompanyId())
                    .flatMap(companyRepository::findById)
                    .orElse(null);
        String createdPass = UUID.randomUUID().toString().substring(0, 8);

        UserEntity creatingUser = userMapper.mapToEntity(createUserDto);
        creatingUser.setLinkedCompany(linkedCompanyEntity);
        creatingUser.setPasswordHash(passwordEncoder.encode(createdPass));

        return userMapper.toUserCreatedDto(userRepository.save(creatingUser), createdPass);
    }

    @Transactional(readOnly = true)
    public UserDto getUserInfoByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail)
                .map(userMapper::toUserDto)
                .orElseThrow(() -> new RuntimeException(String.format("Не удалось найти пользователя с email: %s", userEmail)));
    }


    @Transactional(readOnly = true)
    public PageResponse<UserDto> getUserPage(UserPageRequest userPageRequest, UserDetails userDetails) {
        QPredicates predicateBuilder = predicateDataMapper.mapUserFilterToPredicate(userPageRequest.getUserFilter());

        if (getUserRole(userDetails) == UserRole.COMPANY_AGENT) {
            Optional<String> creatorEmailOptional = Optional.ofNullable(userDetails)
                    .map(UserDetails::getUsername);

            Long agentCompanyId = creatorEmailOptional.flatMap(userRepository::findByEmail)
                    .map(UserEntity::getLinkedCompany)
                    .map(CompanyEntity::getId)
                    .orElseThrow(() -> new RuntimeException(String.format("Не удалось найти компанию связанную с email: %s",
                            creatorEmailOptional.orElse(null))));
            predicateBuilder.add(agentCompanyId, userEntity.linkedCompany.id::eq);
        }


        Sort userPageSort = sortDataMapper.mapUserSortSpecifiersToSpringSort(userPageRequest.getSortOrderSpecifiers());
        PageRequest pageRequest = PageRequest.of(
                Optional.ofNullable(userPageRequest.getPageNumber()).orElseThrow(),
                Optional.ofNullable(userPageRequest.getPageSize()).orElseThrow(),
                userPageSort);
        Page<UserEntity> usersPage = userRepository.findAll(predicateBuilder.build(), pageRequest);

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


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return userRepository.findByEmail(username)
                .map(userMapper::toSpringSecUser)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Не удалось найти пользователя с email: %s", username)));
    }
}
