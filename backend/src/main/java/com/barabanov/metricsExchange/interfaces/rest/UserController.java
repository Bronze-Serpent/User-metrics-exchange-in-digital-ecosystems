package com.barabanov.metricsExchange.interfaces.rest;

import com.barabanov.metricsExchange.interfaces.rest.dto.*;
import com.barabanov.metricsExchange.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user-exchange-metrics")
@RestController
public class UserController {

    private final UserService userService;


    @GetMapping("/me")
    public UserDto userInfo(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Получен запрос на получение информации об авторизованном пользователе");
        return userService.getUserInfoByEmail(userDetails.getUsername());
    }

    @PostMapping("/user/register-client")
    public UserDto registerUser(@RequestBody UserRegisterDto userRegisterDto) {

        log.info("Получен запрос на создание регистрацию пользователя с email: {}", userRegisterDto.getEmail());
        return userService.createClient(userRegisterDto);
    }


    // Рест на создание администратора компании
    // Рест на создание аккаунта представителя компании в системе
    // Рест на создание администратора
    @PostMapping("/user/create")
    public UserCreatedDto createUser(@RequestBody CreateUserDto createUserDto,
                                     @AuthenticationPrincipal UserDetails userDetails) {

        log.info("Получен запрос на создание пользователя с email: {} от пользователя с email: {}",
                createUserDto.getEmail(),
                userDetails.getUsername());
        return userService.createUser(createUserDto, userDetails);
    }


    // Рест на удаление администратора компании
    // Рест на удаление администратора
    @DeleteMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {

        log.info("Получен запрос на удаление пользователя");
        userService.deleteUser(userId);
    }


    // Рест на просмотр всех администраторов компании с пагинацией
    // Рест на получение списка всех акков с ролью админа в приложении
    @PostMapping("/users")
    public PageResponse<UserDto> getUsers(@RequestBody UserPageRequest userPageRequest,
                                          @AuthenticationPrincipal UserDetails userDetails) {

        log.info("Получен запрос на получение набора пользователей");
        return userService.getUserPage(userPageRequest, userDetails);
    }

}
