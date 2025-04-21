package org.example.expert.client;

import org.example.expert.client.dto.WeatherDto;
import org.example.expert.domain.common.exception.ServerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherClientTest {

    @Mock
    private RestTemplate restTemplate;

    private WeatherClient weatherClient;

    // 기본 생성자 만들기
    @BeforeEach
    void setUp() {
        RestTemplateBuilder builder = Mockito.mock(RestTemplateBuilder.class);
        when(builder.build()).thenReturn(restTemplate);
        weatherClient = new WeatherClient(builder);
    }

    @Test
    @DisplayName("정상적으로 오늘의 날씨 정보가 조회된다.")
    void getTodayWeather_successfully() {
        // given
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("MM-dd"));

        WeatherDto[] mockResponse = {
                new WeatherDto(today, "Stormy"),
                new WeatherDto("01-01", "Rainy")
        };

        ResponseEntity<WeatherDto[]> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        when(restTemplate.getForEntity(any(URI.class), eq(WeatherDto[].class))).thenReturn(responseEntity);

        // when
        String result = weatherClient.getTodayWeather();

        // then
        assertEquals("Stormy", result);
    }

    @Test
    @DisplayName("응답에 오늘 날짜에 해당하는 데이터가 없는 경우 서버 에러를 던진다")
    void getTodayWeather_WhenTodayWeatherNotFound_ThrowsServerException() {
        WeatherDto[] mockResponse = {
                new WeatherDto("01-01", "Rainy")
        };

        ResponseEntity<WeatherDto[]> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        when(restTemplate.getForEntity(any(URI.class), eq(WeatherDto[].class))).thenReturn(responseEntity);


        // then
        assertThrows(ServerException.class, () -> {
            weatherClient.getTodayWeather();
        });
    }
}