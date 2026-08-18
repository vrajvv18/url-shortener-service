package com.example.urlshortener;

import com.example.urlshortener.config.AppProperties;
import com.example.urlshortener.dto.CreateUrlRequest;
import com.example.urlshortener.repository.ClickEventRepository;
import com.example.urlshortener.repository.UrlRepository;
import com.example.urlshortener.service.UrlService;
import com.example.urlshortener.util.ShortCodeGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.example.urlshortener.domain.Url;

class UrlServiceTest {
    private final UrlRepository urls = mock(UrlRepository.class);
    private final ClickEventRepository clicks = mock(ClickEventRepository.class);
    private final ShortCodeGenerator generator = mock(ShortCodeGenerator.class);
    private final AppProperties props = new AppProperties(new AppProperties.PublicUrl("http://localhost:8080"), new AppProperties.RateLimit(30, 60));
    private final UrlService service = new UrlService(urls, clicks, generator, props);

    @Test
    void rejectsNonHttpUrl() {
        assertThrows(RuntimeException.class, () -> service.create(new CreateUrlRequest("ftp://example.com/file", null)));
        verifyNoInteractions(urls);
    }

    @Test
    void rejectsEmbeddedCredentials() {
        assertThrows(RuntimeException.class, () -> service.create(new CreateUrlRequest("https://user:pass@example.com", null)));
    }

    @Test
    void rejectsExpiredExpiryTime() {
        assertThrows(RuntimeException.class, () -> service.create(new CreateUrlRequest("https://example.com", Instant.now().minusSeconds(1))));
    }

    @Test
    void retriesAfterDatabaseCollision() {
        when(generator.generate()).thenReturn("abc12345", "def67890");
        when(urls.saveAndFlush(any(Url.class)))
                .thenThrow(new org.springframework.dao.DataIntegrityViolationException("duplicate"))
                .thenAnswer(inv -> inv.getArgument(0));
        var response = service.create(new CreateUrlRequest("https://example.com", null));
        assertEquals("def67890", response.shortCode());
        verify(generator, times(2)).generate();
        verify(urls, times(2)).saveAndFlush(any(Url.class));
    }
}
