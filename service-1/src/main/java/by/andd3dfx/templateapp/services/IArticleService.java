package by.andd3dfx.templateapp.services;

import by.andd3dfx.templateapp.dto.ArticleDto;
import by.andd3dfx.templateapp.dto.ArticleUpdateDto;
import by.andd3dfx.templateapp.dto.PageResult;

public interface IArticleService {

    ArticleDto create(ArticleDto articleDto);

    ArticleDto read(Long id);

    void update(Long id, ArticleUpdateDto articleUpdateDto);

    void delete(Long id);

    PageResult readArticles(Integer pageNo, Integer pageSize, String sortBy);
}
