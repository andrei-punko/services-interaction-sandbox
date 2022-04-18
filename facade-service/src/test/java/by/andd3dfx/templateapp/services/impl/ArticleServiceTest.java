package by.andd3dfx.templateapp.services.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import by.andd3dfx.templateapp.dto.ArticleDto;
import by.andd3dfx.templateapp.dto.ArticleUpdateDto;
import by.andd3dfx.templateapp.dto.PageResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    private final String ARTICLES_SERVICE_URL = "http://localhost:8989/api/v1/articles";

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Clock clockMock;
    private Clock fixedClock;

    @InjectMocks
    private ArticleService articleService;

    @BeforeEach
    void before() {
        ReflectionTestUtils.setField(articleService, "articlesServiceUrl", ARTICLES_SERVICE_URL);

        fixedClock = Clock.fixed(Instant.parse("2014-12-22T10:15:30.00Z"), ZoneId.systemDefault());
        // Allow unnecessary stubbing:
        lenient().doReturn(fixedClock.instant()).when(clockMock).instant();
        lenient().doReturn(fixedClock.getZone()).when(clockMock).getZone();
    }

    @Test
    void create() {
        ArticleDto articleDto = ArticleDto.builder().build();
        ArticleDto updatedArticleDto = ArticleDto.builder().build();
        when(restTemplate.postForObject(ARTICLES_SERVICE_URL, articleDto, ArticleDto.class))
            .thenReturn(updatedArticleDto);

        ArticleDto result = articleService.create(articleDto);

        Mockito.verify(restTemplate).postForObject(ARTICLES_SERVICE_URL, articleDto, ArticleDto.class);
        assertThat(result, is(updatedArticleDto));
    }

    @Test
    void get() {
        final Long ARTICLE_ID = 123L;
        ArticleDto articleDto = new ArticleDto();
        Mockito.when(restTemplate.getForObject(ARTICLES_SERVICE_URL + "/" + ARTICLE_ID, ArticleDto.class))
            .thenReturn(articleDto);

        ArticleDto result = articleService.read(ARTICLE_ID);

        Mockito.verify(restTemplate).getForObject(ARTICLES_SERVICE_URL + "/" + ARTICLE_ID, ArticleDto.class);
        assertThat(result, is(articleDto));
    }

    @Test
    public void getAbsentArticle() {
        final Long ARTICLE_ID = 123L;
        var exception = new HttpClientErrorException(HttpStatus.NOT_FOUND);
        Mockito.when(restTemplate.getForObject(ARTICLES_SERVICE_URL + "/" + ARTICLE_ID, ArticleDto.class))
            .thenThrow(exception);

        try {
            articleService.read(ARTICLE_ID);

            fail("Exception should be thrown");
        } catch (HttpClientErrorException ex) {
            Mockito.verify(restTemplate).getForObject(ARTICLES_SERVICE_URL + "/" + ARTICLE_ID, ArticleDto.class);
            assertThat(ex, is(exception));
        }
    }

    @Test
    void update() {
        final Long ARTICLE_ID = 123L;
        ArticleUpdateDto articleUpdateDto = new ArticleUpdateDto();

        articleService.update(ARTICLE_ID, articleUpdateDto);

        Mockito.verify(restTemplate)
            .patchForObject(ARTICLES_SERVICE_URL + "/" + ARTICLE_ID, articleUpdateDto, String.class);
    }

    @Test
    void updateAbsentArticle() {
        final Long ARTICLE_ID = 123L;
        ArticleUpdateDto articleUpdateDto = new ArticleUpdateDto();
        var exception = new HttpClientErrorException(HttpStatus.NOT_FOUND);
        Mockito.when(
                restTemplate.patchForObject(ARTICLES_SERVICE_URL + "/" + ARTICLE_ID, articleUpdateDto, String.class))
            .thenThrow(exception);

        try {
            articleService.update(ARTICLE_ID, articleUpdateDto);

            fail("Exception should be thrown");
        } catch (HttpClientErrorException ex) {
            Mockito.verify(restTemplate)
                .patchForObject(ARTICLES_SERVICE_URL + "/" + ARTICLE_ID, articleUpdateDto, String.class);
            assertThat(ex, is(exception));
        }
    }

    @Test
    void delete() {
        final Long ARTICLE_ID = 123L;

        articleService.delete(ARTICLE_ID);

        Mockito.verify(restTemplate).delete(ARTICLES_SERVICE_URL + "/" + ARTICLE_ID);
    }

    @Test
    void deleteAbsentArticle() {
        final Long ARTICLE_ID = 123L;
        var exception = new HttpClientErrorException(HttpStatus.NOT_FOUND);
        Mockito.doThrow(exception)
            .when(restTemplate).delete(ARTICLES_SERVICE_URL + "/" + ARTICLE_ID);

        try {
            articleService.delete(ARTICLE_ID);

            fail("Exception should be thrown");
        } catch (HttpClientErrorException ex) {
            Mockito.verify(restTemplate).delete(ARTICLES_SERVICE_URL + "/" + ARTICLE_ID);
            assertThat(ex, is(exception));
        }
    }

    @Test
    void readArticles() {
        var articleDto = new ArticleDto();
        var pageResult = PageResult.builder()
            .content(List.of(articleDto))
            .build();

        var pageSize = 20;
        var pageNumber = 3;
        var sortBy = "title,ASC";

        final String url = String.format("%s?page=%d&size=%d", ARTICLES_SERVICE_URL, pageNumber, pageSize);
        Object object = new Object();
        Mockito.when(restTemplate.getForObject(url, Object.class))
            .thenReturn(object);
        when(objectMapper.convertValue(object, PageResult.class)).thenReturn(pageResult);

        PageResult result = articleService.readArticles(pageNumber, pageSize, sortBy);

        Mockito.verify(restTemplate).getForObject(url, Object.class);
        Mockito.verify(objectMapper).convertValue(object, PageResult.class);
        assertThat(result, is(pageResult));
    }
}
