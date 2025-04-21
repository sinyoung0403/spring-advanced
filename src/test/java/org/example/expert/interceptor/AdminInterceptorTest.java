package org.example.expert.interceptor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertTrue;


class AdminInterceptorTest {

    @Test
    @DisplayName("정상적으로 인터셉터가 작동 시 로거가 출력된다.")
    void AdminInterceptor_successfully() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("userRole", "ADMIN");
        MockHttpServletResponse response = new MockHttpServletResponse();

        AdminInterceptor interceptor = new AdminInterceptor();
        Logger mockLogger = mock(Logger.class);
        interceptor.setLogger(mockLogger);

        // when
        boolean result = interceptor.preHandle(request, response, new Object());

        // then
        assertTrue(result);
        verify(mockLogger).info(anyString(), any(), any());
    }
}