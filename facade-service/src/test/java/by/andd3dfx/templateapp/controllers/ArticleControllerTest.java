package by.andd3dfx.templateapp.controllers;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import by.andd3dfx.templateapp.dto.ArticleDto;
import by.andd3dfx.templateapp.dto.ArticleUpdateDto;
import by.andd3dfx.templateapp.dto.PageResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@WebAppConfiguration
class ArticleControllerTest {

    private final String ARTICLES_BASE_ADDRESS = "https://localhost:9082/api/v1/articles";

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private RestTemplate restTemplate;

    @BeforeEach
    public void setup() {
        mockMvc = webAppContextSetup(webApplicationContext)
            .build();
    }

    @Test
    public void createArticle() throws Exception {
        ArticleDto articleDto = ArticleDto.builder()
            .title("Some tittle value")
            .summary("Some summary value")
            .text("Some text")
            .author("Some author")
            .build();
        ArticleDto createdArticleDto = ArticleDto.builder()
            .id(123L)
            .title("Some tittle value")
            .summary("Some summary value")
            .text("Some text")
            .author("Some author")
            .dateCreated(LocalDateTime.now())
            .dateUpdated(LocalDateTime.now())
            .build();
        when(restTemplate.postForObject(ARTICLES_BASE_ADDRESS, articleDto, ArticleDto.class))
            .thenReturn(createdArticleDto);

        mockMvc.perform(post("/api/v1/articles")
                .contentType(APPLICATION_JSON)
                .content(json(articleDto))
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", notNullValue()))
            .andExpect(jsonPath("$.title", is(articleDto.getTitle())))
            .andExpect(jsonPath("$.summary", is(articleDto.getSummary())))
            .andExpect(jsonPath("$.text", is(articleDto.getText())))
            .andExpect(jsonPath("$.author", is(articleDto.getAuthor())))
            .andExpect(jsonPath("$.dateCreated", notNullValue()))
            .andExpect(jsonPath("$.dateUpdated", notNullValue()));
    }

    @Test
    public void createArticleWithIdPopulated() throws Exception {
        ArticleDto articleDto = ArticleDto.builder()
            .id(123L)
            .title("Some tittle value")
            .summary("Some summary value")
            .text("Some text")
            .author("Some author")
            .build();

        final String message = mockMvc.perform(post("/api/v1/articles")
                .contentType(APPLICATION_JSON)
                .content(json(articleDto))
            )
            .andExpect(status().isBadRequest())
            .andReturn().getResolvedException().getMessage();
        assertThat(message, containsString("Article id shouldn't be present"));
    }

    @Test
    public void createArticleWithoutTitle() throws Exception {
        ArticleDto articleDto = new ArticleDto();
        articleDto.setSummary("Some summary value");
        articleDto.setText("Some text");
        articleDto.setAuthor("Some author");

        final String message = mockMvc.perform(post("/api/v1/articles")
                .contentType(APPLICATION_JSON)
                .content(json(articleDto))
            )
            .andExpect(status().isBadRequest())
            .andReturn().getResolvedException().getMessage();
        assertThat(message, containsString("Title should be populated"));
    }

    @Test
    public void createArticleWithEmptyTitle() throws Exception {
        ArticleDto articleDto = new ArticleDto();
        articleDto.setTitle("");
        articleDto.setSummary("Some summary value");
        articleDto.setText("Some text");
        articleDto.setAuthor("Some author");

        final String message = mockMvc.perform(post("/api/v1/articles")
                .contentType(APPLICATION_JSON)
                .content(json(articleDto))
            )
            .andExpect(status().isBadRequest())
            .andReturn().getResolvedException().getMessage();
        assertThat(message, containsString("Title length must be between 1 and 100"));
    }

    @Test
    public void createArticleWithTooLongTitle() throws Exception {
        ArticleDto articleDto = new ArticleDto();
        articleDto.setTitle(createStringWithLength(101));
        articleDto.setSummary("Some summary value");
        articleDto.setText("Some text");
        articleDto.setAuthor("Some author");

        String message = mockMvc.perform(post("/api/v1/articles")
                .contentType(APPLICATION_JSON)
                .content(json(articleDto))
            )
            .andExpect(status().isBadRequest())
            .andReturn().getResolvedException().getMessage();
        assertThat(message, containsString("Title length must be between 1 and 100"));
    }

    @Test
    public void createArticleWithTooLongSummary() throws Exception {
        ArticleDto articleDto = new ArticleDto();
        articleDto.setTitle("Some title");
        articleDto.setSummary(createStringWithLength(260));
        articleDto.setText("Some text");
        articleDto.setAuthor("Some author");

        String message = mockMvc.perform(post("/api/v1/articles")
                .contentType(APPLICATION_JSON)
                .content(json(articleDto))
            )
            .andExpect(status().isBadRequest())
            .andReturn().getResolvedException().getMessage();
        assertThat(message, containsString("Summary length shouldn't be greater than 255"));
    }

    @Test
    public void createArticleWithoutText() throws Exception {
        ArticleDto articleDto = ArticleDto.builder()
            .title("Some title")
            .summary("Some summary value")
            .author("Some author")
            .build();

        String message = mockMvc.perform(post("/api/v1/articles")
                .contentType(APPLICATION_JSON)
                .content(json(articleDto))
            )
            .andExpect(status().isBadRequest())
            .andReturn().getResolvedException().getMessage();
        assertThat(message, containsString("Text should be populated"));
    }

    @Test
    public void createArticleWithEmptyText() throws Exception {
        ArticleDto articleDto = new ArticleDto();
        articleDto.setTitle("Some title");
        articleDto.setSummary("Some summary value");
        articleDto.setText("");
        articleDto.setAuthor("Some author");

        String message = mockMvc.perform(post("/api/v1/articles")
                .contentType(APPLICATION_JSON)
                .content(json(articleDto))
            )
            .andExpect(status().isBadRequest())
            .andReturn().getResolvedException().getMessage();
        assertThat(message, containsString("Text length should be 1 at least"));
    }

    @Test
    public void createArticleWithoutAuthor() throws Exception {
        ArticleDto articleDto = new ArticleDto();
        articleDto.setTitle("Some title");
        articleDto.setSummary("Some summary value");
        articleDto.setText("Some text");

        String message = mockMvc.perform(post("/api/v1/articles")
                .contentType(APPLICATION_JSON)
                .content(json(articleDto))
            )
            .andExpect(status().isBadRequest())
            .andReturn().getResolvedException().getMessage();
        assertThat(message, containsString("Author should be populated"));
    }

    @Test
    public void createArticleWithDateCreatedPopulated() throws Exception {
        ArticleDto articleDto = new ArticleDto();
        articleDto.setTitle("Some tittle value");
        articleDto.setSummary("Some summary value");
        articleDto.setText("Some text");
        articleDto.setAuthor("Some author");
        articleDto.setDateCreated(LocalDateTime.now());

        String message = mockMvc.perform(post("/api/v1/articles")
                .contentType(APPLICATION_JSON)
                .content(json(articleDto))
            )
            .andExpect(status().isBadRequest())
            .andReturn().getResolvedException().getMessage();
        assertThat(message, containsString("DateCreated shouldn't be populated"));
    }

    @Test
    public void createArticleWithDateUpdatedPopulated() throws Exception {
        ArticleDto articleDto = new ArticleDto();
        articleDto.setTitle("Some tittle value");
        articleDto.setSummary("Some summary value");
        articleDto.setText("Some text");
        articleDto.setAuthor("Some author");
        articleDto.setDateUpdated(LocalDateTime.now());

        String message = mockMvc.perform(post("/api/v1/articles")
                .contentType(APPLICATION_JSON)
                .content(json(articleDto))
            )
            .andExpect(status().isBadRequest())
            .andReturn().getResolvedException().getMessage();
        assertThat(message, containsString("DateUpdated shouldn't be populated"));
    }

    @Test
    public void deleteArticle() throws Exception {
        mockMvc.perform(delete("/api/v1/articles/1")
                .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isNoContent());
    }

    @Test
    public void deleteAbsentArticle() throws Exception {
        final Long ARTICLE_ID = 123L;
        var exception = new HttpClientErrorException(HttpStatus.NOT_FOUND);
        Mockito.doThrow(exception)
            .when(restTemplate).delete(ARTICLES_BASE_ADDRESS + "/" + ARTICLE_ID);

        mockMvc.perform(delete("/api/v1/articles/" + ARTICLE_ID)
                .contentType(APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void readArticle() throws Exception {
        final Long ARTICLE_ID = 123L;
        ArticleDto articleDto = ArticleDto.builder()
            .id(ARTICLE_ID)
            .build();
        when(restTemplate.getForObject(ARTICLES_BASE_ADDRESS + "/" + ARTICLE_ID, ArticleDto.class))
            .thenReturn(articleDto);

        mockMvc.perform(get("/api/v1/articles/" + ARTICLE_ID)
                .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(ARTICLE_ID.intValue())));
    }

    @Test
    public void readAbsentArticle() throws Exception {
        final Long ARTICLE_ID = 123L;
        var exception = new HttpClientErrorException(HttpStatus.NOT_FOUND);
        Mockito.when(restTemplate.getForObject(ARTICLES_BASE_ADDRESS + "/" + ARTICLE_ID, ArticleDto.class))
            .thenThrow(exception);

        mockMvc.perform(get("/api/v1/articles/" + ARTICLE_ID)
                .contentType(APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void readArticles() throws Exception {
        var pageNumber = 3;
        var pageSize = 5;
        var pageResult = PageResult.builder()
            .content(List.of(new ArticleDto(), new ArticleDto()))
            .number(pageNumber)
            .size(pageSize)
            .totalPages(1)
            .totalElements(10)
            .build();

        final String url = String.format(ARTICLES_BASE_ADDRESS + "?page=%d&size=%d", pageNumber, pageSize);
        Mockito.when(restTemplate.getForObject(url, Object.class))
            .thenReturn(pageResult);

        mockMvc.perform(get("/api/v1/articles?page=" + pageNumber + "&size=" + pageSize)
                .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(2)))
            .andExpect(jsonPath("$.number", is(pageNumber)))
            .andExpect(jsonPath("$.size", is(pageSize)))
            .andExpect(jsonPath("$.totalPages", is(1)))
            .andExpect(jsonPath("$.totalElements", is(10)));
    }

    @Test
    public void updateArticleTitle() throws Exception {
        ArticleUpdateDto articleUpdateDto = ArticleUpdateDto.builder()
            .title("Some tittle value")
            .build();

        mockMvc.perform(patch("/api/v1/articles/2")
                .contentType(APPLICATION_JSON)
                .content(json(articleUpdateDto))
            )
            .andExpect(status().isOk());
    }

    @Test
    public void updateArticleSummary() throws Exception {
        ArticleUpdateDto articleUpdateDto = ArticleUpdateDto.builder()
            .summary("Some summary value")
            .build();

        mockMvc.perform(patch("/api/v1/articles/2")
                .contentType(APPLICATION_JSON)
                .content(json(articleUpdateDto))
            )
            .andExpect(status().isOk());
    }

    @Test
    public void updateArticleText() throws Exception {
        ArticleUpdateDto articleUpdateDto = new ArticleUpdateDto();
        articleUpdateDto.setText("Some text value");

        mockMvc.perform(patch("/api/v1/articles/2")
                .contentType(APPLICATION_JSON)
                .content(json(articleUpdateDto))
            )
            .andExpect(status().isOk());
    }

    @Test
    public void updateAbsentArticle() throws Exception {
        final Long ARTICLE_ID = 123L;
        ArticleUpdateDto articleUpdateDto = ArticleUpdateDto.builder()
            .title("q")
            .build();
        var exception = new HttpClientErrorException(HttpStatus.NOT_FOUND);
        Mockito.when(
                restTemplate.patchForObject(ARTICLES_BASE_ADDRESS + "/" + ARTICLE_ID, articleUpdateDto,
                    String.class))
            .thenThrow(exception);

        mockMvc.perform(patch("/api/v1/articles/" + ARTICLE_ID)
                .contentType(APPLICATION_JSON)
                .content(json(articleUpdateDto)))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateArticleWithEmptyTitle() throws Exception {
        ArticleUpdateDto articleUpdateDto = ArticleUpdateDto.builder()
            .title("")
            .build();

        String message = mockMvc.perform(patch("/api/v1/articles/2")
                .contentType(APPLICATION_JSON)
                .content(json(articleUpdateDto))
            )
            .andExpect(status().isBadRequest())
            .andReturn().getResolvedException().getMessage();
        assertThat(message, containsString("Title length must be between 1 and 100"));
    }

    @Test
    public void updateArticleWithTooLongTitle() throws Exception {
        ArticleUpdateDto articleUpdateDto = ArticleUpdateDto.builder()
            .title(createStringWithLength(101))
            .build();

        String message = mockMvc.perform(patch("/api/v1/articles/2")
                .contentType(APPLICATION_JSON)
                .content(json(articleUpdateDto))
            )
            .andExpect(status().isBadRequest())
            .andReturn().getResolvedException().getMessage();
        assertThat(message, containsString("Title length must be between 1 and 100"));
    }

    @Test
    public void updateArticleWithTooLongSummary() throws Exception {
        ArticleUpdateDto articleUpdateDto = ArticleUpdateDto.builder()
            .summary(createStringWithLength(260))
            .build();

        String message = mockMvc.perform(patch("/api/v1/articles/2")
                .contentType(APPLICATION_JSON)
                .content(json(articleUpdateDto))
            )
            .andExpect(status().isBadRequest())
            .andReturn().getResolvedException().getMessage();
        assertThat(message, containsString("Summary length shouldn't be greater than 255"));
    }

    @Test
    public void updateArticleWithEmptyText() throws Exception {
        ArticleUpdateDto articleUpdateDto = ArticleUpdateDto.builder()
            .text("")
            .build();

        String message = mockMvc.perform(patch("/api/v1/articles/2")
                .contentType(APPLICATION_JSON)
                .content(json(articleUpdateDto))
            )
            .andExpect(status().isBadRequest())
            .andReturn().getResolvedException().getMessage();
        assertThat(message, containsString("Text length should be 1 at least"));
    }

    private String json(Object o) throws IOException {
        return objectMapper.writeValueAsString(o);
    }

    private String createStringWithLength(int length) {
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < length; index++) {
            builder.append("a");
        }
        return builder.toString();
    }
}
