package org.example.expert.domain.todo.service;

import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    @Mock
    private WeatherClient weatherClient;

    @Test
    @DisplayName("Todo 가 정상적으로 등록된다")
    void saveTodo_successfully() {
        // given
        AuthUser authUser = new AuthUser(1L, "test@test.com", UserRole.USER);
        String weather = "Sunny";
        TodoSaveRequest todoSaveRequest = new TodoSaveRequest("title", "contents");

        Todo newTodo = new Todo(todoSaveRequest.getTitle(), todoSaveRequest.getContents(), weather, User.fromAuthUser(authUser));

        when(weatherClient.getTodayWeather()).thenReturn(weather);
        when(todoRepository.save(any(Todo.class))).thenReturn(newTodo);

        // when
        TodoSaveResponse todoSaveResponse = todoService.saveTodo(authUser, todoSaveRequest);

        // then
        assertNotNull(todoSaveResponse);
        assertEquals(weather, todoSaveResponse.getWeather());
        assertEquals(authUser.getId(), todoSaveResponse.getUser().getId());
    }

    @Test
    @DisplayName("Todo 의 정상적으로 페이징 조회된다")
    void getTodos_successfully() {
        // given
        int page = 1, size = 5;

        User user = new User("test@test.com", "Pwd", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);
        Todo todo1 = new Todo("title1", "content", "Rainy", user);
        ReflectionTestUtils.setField(todo1, "id", 1L);
        Todo todo2 = new Todo("title2", "content", "Sunny", user);
        ReflectionTestUtils.setField(todo2, "id", 1L);
        List<Todo> todoList = List.of(todo1, todo2);

        Page<Todo> pageTodo = new PageImpl<>(
                todoList,
                PageRequest.of(page - 1, size),
                todoList.size());
        when(todoRepository.findAllByOrderByModifiedAtDesc(any())).thenReturn(pageTodo);

        // when
        Page<TodoResponse> result = todoService.getTodos(page, size);

        // then
        assertEquals(2, result.getContent().size());
        assertEquals("title1", result.getContent().get(0).getTitle());
        assertEquals("title2", result.getContent().get(1).getTitle());
        assertEquals("Rainy", result.getContent().get(0).getWeather());
        assertEquals("Sunny", result.getContent().get(1).getWeather());
    }

    @Test
    @DisplayName("Todo 가 정상적으로 단일 조회 된다")
    void getTodo_successfully() {
        // given
        User user = new User("test@test.com", "Pwd", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);
        Todo todo1 = new Todo("title1", "content", "Rainy", user);
        ReflectionTestUtils.setField(todo1, "id", 1L);

        when(todoRepository.findByIdWithUser(any())).thenReturn(Optional.of(todo1));

        // when
        TodoResponse result = todoService.getTodo(1L);

        // then
        assertEquals("title1", result.getTitle());
        assertEquals("Rainy", result.getWeather());
        assertEquals("test@test.com", result.getUser().getEmail());
    }
}