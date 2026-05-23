package com.barabanov.metricsExchange.interfaces.rest;

import com.barabanov.metricsExchange.interfaces.rest.dto.CreateUserDto;
import com.barabanov.metricsExchange.interfaces.rest.dto.PageResponse;
import com.barabanov.metricsExchange.interfaces.rest.dto.UserPageRequest;
import com.barabanov.metricsExchange.service.UserService;
import com.barabanov.metricsExchange.interfaces.rest.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user-exchange-metrics")
@RestController
public class UserController {

    private final UserService userService;


    // Рест на создание администратора компании
    // Рест на создание аккаунта представителя компании в системе
    // Рест на создание администратора
    @PostMapping("/user/create")
    public UserDto createUser(CreateUserDto createUserDto) {

        log.info("Получен запрос на создание пользователя");
        return userService.createUser(createUserDto);
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
    public PageResponse<UserDto> getUsers(@RequestBody UserPageRequest userPageRequest) {

        log.info("Получен запрос на получение набора пользователей");
        return userService.getUserPage(userPageRequest);
    }

}
