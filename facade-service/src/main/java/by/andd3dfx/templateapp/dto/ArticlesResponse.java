package by.andd3dfx.templateapp.dto;

import by.andd3dfx.templateapp.dto.ArticleDto;
import java.util.List;
import lombok.Data;

@Data
public class ArticlesResponse {

    private List<ArticleDto> content;
}
