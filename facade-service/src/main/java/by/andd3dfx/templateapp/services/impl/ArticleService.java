package by.andd3dfx.templateapp.services.impl;

import by.andd3dfx.templateapp.dto.ArticleDto;
import by.andd3dfx.templateapp.dto.ArticleUpdateDto;
import by.andd3dfx.templateapp.dto.PageResult;
import by.andd3dfx.templateapp.services.IArticleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ArticleService implements IArticleService {

    @Value("${service2.articles-url}")
    private String articlesServiceUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public ArticleDto create(ArticleDto articleDto) {
        return restTemplate.postForObject(articlesServiceUrl, articleDto, ArticleDto.class);
    }

    @Override
    public ArticleDto read(Long id) {
        return restTemplate.getForObject(articlesServiceUrl + "/" + id, ArticleDto.class);
    }

    @Override
    public void update(Long id, ArticleUpdateDto articleUpdateDto) {
        restTemplate.patchForObject(articlesServiceUrl + "/" + id, articleUpdateDto, String.class);
    }

    @Override
    public void delete(Long id) {
        restTemplate.delete(articlesServiceUrl + "/" + id);
    }

    @Override
    public PageResult readArticles(Integer pageNo, Integer pageSize, String sortBy) {
        var url = String.format("%s?page=%d&size=%d", articlesServiceUrl, pageNo, pageSize);
        var result = restTemplate.getForObject(url, Object.class);
        return objectMapper.convertValue(result, PageResult.class);
    }
}
