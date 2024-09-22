package aboba1337.blogproject.article;

import aboba1337.blogproject.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleRepository articleRepository;

    public ArticleController(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @GetMapping
    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticleById(@PathVariable Long id) {
        return articleRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Article with id " + id + " not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Article createArticle(@RequestBody Article article) {
        return articleRepository.save(article);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable Long id, @RequestBody Article article) {
        return articleRepository.findById(id)
                .map(existingArticle -> {
                    existingArticle.setTitle(article.getTitle());
                    existingArticle.setContent(article.getContent());
                    return ResponseEntity.ok(articleRepository.save(existingArticle));
                })
                .orElseThrow(() -> new NotFoundException("Article with id " + id + " not found"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteArticle(@PathVariable Long id) {
        return articleRepository.findById(id)
                .map(article -> {
                    articleRepository.delete(article);
                    return ResponseEntity.ok().build();
                })
                .orElseThrow(() -> new NotFoundException("Article with id " + id + " not found"));
    }

}
