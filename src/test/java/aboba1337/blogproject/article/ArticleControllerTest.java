package aboba1337.blogproject.article;

import aboba1337.blogproject.DockerContainerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ArticleControllerTest extends DockerContainerConfig {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ArticleController articleController;

    @Autowired
    private ArticleRepository articleRepository;

    private Article article;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        article = new Article();
        article.setId(1L);
        article.setTitle("Test Article");
        article.setContent("Test Content");
    }

    @Test
    public void testGetAllArticles() throws Exception {
        // Arrange
        Article article1 = new Article();
        article1.setTitle("Test Article 1");
        article1.setContent("Test Content 1");
        articleRepository.save(article1);

        Article article2 = new Article();
        article2.setTitle("Test Article 2");
        article2.setContent("Test Content 2");
        articleRepository.save(article2);

        // Act
        mockMvc.perform(MockMvcRequestBuilders.get("/api/articles"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("Test Article 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].title").value("Test Article 2"));
    }

    @Test
    public void testGetArticleById() throws Exception {
        // Arrange
        Article article = new Article();
        article.setId(1L);
        article.setTitle("Test Article");
        article.setContent("Test Content");
        articleRepository.save(article);

        // Act
        mockMvc.perform(MockMvcRequestBuilders.get("/api/articles/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Test Article"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value("Test Content"));
    }

    @Test
    public void testGetArticleByIdNotFound() throws Exception {
        // Arrange
        // when(articleRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        mockMvc.perform(MockMvcRequestBuilders.get("/api/articles/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateArticle() throws Exception {
        // Arrange
        Article article = new Article();
        article.setTitle("Test Article");
        article.setContent("Test Content");

        // Act
        mockMvc.perform(MockMvcRequestBuilders.post("/api/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(article)))
                .andExpect(status().isCreated());

        // Assert
        Optional<Article> createdArticle = articleRepository.findById(1L);
        assertTrue(createdArticle.isPresent());
        assertEquals("Test Article", createdArticle.get().getTitle());
        assertEquals("Test Content", createdArticle.get().getContent());
    }

    @Test
    public void testUpdateArticle() throws Exception {
        // Arrange
        Article updatedArticle = new Article();
        updatedArticle.setId(1L);
        updatedArticle.setTitle("Updated Test Article");
        updatedArticle.setContent("Updated Test Content");
        articleRepository.save(article);

        // Act
        mockMvc.perform(MockMvcRequestBuilders.put("/api/articles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedArticle)))
                .andExpect(status().isOk());

        // Assert
        Optional<Article> result = articleRepository.findById(1L);
        assertTrue(result.isPresent());
        assertEquals("Updated Test Article", result.get().getTitle());
        assertEquals("Updated Test Content", result.get().getContent());
    }

    @Test
    public void testUpdateArticleNotFound() throws Exception {
        // Act
        mockMvc.perform(MockMvcRequestBuilders.put("/api/articles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(article)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteArticle() throws Exception {
        // Arrange
        articleRepository.save(article);

        // Act
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/articles/1"))
                .andExpect(status().isOk());

        // Assert
        assertFalse(articleRepository.findById(1L).isPresent());
    }

    @Test
    public void testDeleteArticleNotFound() throws Exception {
        // Act
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/articles/1"))
                .andExpect(status().isNotFound());
    }
}