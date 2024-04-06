package object_orienters.techspot.comment;

import object_orienters.techspot.content.ReactableContent;
import object_orienters.techspot.model.Privacy;
import object_orienters.techspot.post.Post;
import object_orienters.techspot.profile.Profile;
import object_orienters.techspot.security.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
// @WebMvcTest(value = CommentController.class, excludeAutoConfiguration =
// SecurityAutoConfiguration.class)

public class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CommentModelAssembler assembler;
    @MockBean
    @Autowired
    private ImpleCommentService commentService;
    private User user = createUser("husam_ramoni", "husam@example.com", "securepassword123");
    private User user2 = createUser("rawan", "rawan@example.com", "securepassword123");
    private Profile profile = createProfile(user);
    private Post post = createPost(profile);
    // MockMultipartFile mockFile = new MockMultipartFile("file", "filename.txt",
    // "text/plain", "Some content".getBytes());
    private Comment comment = createComment(profile, post, "Test comment");
    private Comment repliedComment = createComment(profile, comment, "Replied comment");
    private Profile profile2 = createProfile(user2);

    public static User createUser(String username, String email, String password) {

        return new User(username, email, password);
    }

    public static Profile createProfile(User user) {
        return new Profile(user, "Husam Ramoni", "Software Engineer", "husam@example.com",
                null, Profile.Gender.MALE, "1985-04-12");
    }

    // @TestConfiguration
    // static class SecurityPermitAllConfig {
    // @Bean
    // public SecurityFilterChain securityFilterChain(HttpSecurity http) throws
    // Exception {
    // http.authorizeRequests()
    // .anyRequest().permitAll();
    // return http.build();
    // }
    // }

    public Post createPost(Profile author) {

        return new Post("This is a test post", Privacy.PUBLIC, author);
    }

    public Comment createComment(Profile commentor, ReactableContent commentedOn, String commentContent) {

        return new Comment(commentor, commentedOn, commentContent);
    }

    public void generator() {
        post.setContentID(1L);
        comment.setContentID(2L);
        repliedComment.setContentID(3L);
    }

    @BeforeEach
    void setup(WebApplicationContext wac) {
        generator();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void testGetCommentsOnPost() throws Exception {
        System.out.println(post.getContentID());
        System.out.println(profile.getPublishedPosts());
        System.out.println(post.getComments());
        System.out.println(profile.getUsername());
        // System.out.println(user.getUsername());
        // System.out.println(profile.getUsername());
        // System.out.println(profile.getPublishedPosts());
        // System.out.println(post.getComments());
        EntityModel<Comment> entityModel = EntityModel.of(comment); // Wrap in HATEOAS entity model.
        given(commentService.getComments(post.getContentID())).willReturn(List.of(comment));
        given(assembler.toModel(comment)).willReturn(entityModel);
        mockMvc.perform(get("/profiles/{username}/content/{contentID}/comments", profile.getUsername(), 1)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()); // Here we expect the HTTP status to be 200 OK.

    }

    @Test
    public void testGetCommentOnPost() throws Exception {

        EntityModel<Comment> entityModel = EntityModel.of(comment,
                linkTo(methodOn(CommentController.class).getComment(comment.getContentID(), post.getContentID(),
                        user.getUsername())).withSelfRel());

        given(commentService.getComment(comment.getContentID())).willReturn(comment);
        given(assembler.toModel(comment)).willReturn(entityModel);

        // Act & Assert
        mockMvc.perform(get("/profiles/{username}/content/{contentID}/comments/{commentID}", "husam_ramoni", 1, 2)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Verify some fields in the response.
                .andExpect(jsonPath("$.contentID").value(2));
        System.out.println(comment.getCommentedOn().getContentID());
        // Verify interaction with mock objects
        verify(commentService).getComment(comment.getContentID());
        verify(assembler).toModel(comment);
    }

    @Test
    public void testGetCommentsOnComment() throws Exception {
        System.out.println(comment.getComments());
        System.out.println(repliedComment.getTextData());
        System.out.println(repliedComment.getCommentedOn());
        EntityModel<Comment> entityModel = EntityModel.of(repliedComment); // Wrap in HATEOAS entity model.
        given(commentService.getComments(comment.getContentID())).willReturn(List.of(repliedComment));
        given(assembler.toModel(repliedComment)).willReturn(entityModel);
        mockMvc.perform(get("/profiles/{username}/content/{contentID}/comments", "husam_ramoni", 2))
                .andExpect(status().isOk()); // Here we expect the HTTP status to be 200 OK.

    }

    @Test
    public void testGetCommentOnComment() throws Exception {

        EntityModel<Comment> entityModel = EntityModel.of(repliedComment,
                linkTo(methodOn(CommentController.class).getComment(repliedComment.getContentID(),
                        comment.getContentID(), user.getUsername())).withSelfRel());

        given(commentService.getComment(repliedComment.getContentID())).willReturn(repliedComment);
        given(assembler.toModel(repliedComment)).willReturn(entityModel);

        // Act & Assert
        mockMvc.perform(get("/profiles/{username}/content/{contentID}/comments/{commentID}", "husam_ramoni", 2, 3)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Verify some fields in the response.
                .andExpect(jsonPath("$.contentID").value(3));
        // Verify interaction with mock objects
        verify(commentService).getComment(repliedComment.getContentID());
        verify(assembler).toModel(repliedComment);
    }

    @Test
    @WithMockUser(username = "husam_ramoni")
    public void testAddTextCommentOnPost() throws Exception {

        given(commentService.addComment(1L, "husam_ramoni", null, "Test comment")).willReturn(comment);

        mockMvc.perform(MockMvcRequestBuilders
                .multipart("/profiles/{username}/content/{contentID}/comments", "husam_ramoni", 1L)
                .param("text", "Test comment")
                .param("contentID", "1")
                .param("username", "husam_ramoni")
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());
        System.out.println("comment.getTextData(): " + comment.getTextData());
        System.out.println("comment.getCommentedOn(): " + comment.getCommentedOn());
        System.out.println("comment.getCommentedOn(): " + comment.getCommentedOn());
        System.out.println("post.getContentID(): " + post.getContentID());
        System.out.println("post.getTextData(): " + post.getTextData());
    }

    @Test
    @WithMockUser(username = "husam_ramoni")
    public void testDeleteComment() throws Exception {
        doNothing().when(commentService).deleteComment(post.getContentID(), comment.getContentID());

        // Act & Assert
        mockMvc.perform(delete("/profiles/{username}/content/{contentID}/comments/{commentID}", "husam_ramoni", 1, 2))
                .andExpect(status().isNoContent());
    }

    // @Test
    // @WithMockUser(username = "husam_ramoni")
    // public void testUpdateComment() throws Exception {
    // // Arrange
    // String updatedText = "Updated comment text";
    // Map<String, String> updateRequest = Map.of("comment", updatedText);
    // //comment.setComment(updatedText);
    // System.out.println(commentService.updateComment(post.getContentID(),
    // comment.getContentID(), updatedText));
    // EntityModel<Comment> entityModel = EntityModel.of(comment);
    // given(commentService.updateComment(post.getContentID(),
    // comment.getContentID(), updatedText)).willReturn(comment);
    // given(assembler.toModel(comment)).willReturn(entityModel);
    // // Act & Assert
    // mockMvc.perform(put("/profiles/{username}/content/{contentID}/comments/{commentID}",
    // "husam_ramoni", 1, 2)
    // .contentType(MediaType.APPLICATION_JSON)
    // .content("{\"comment\":\"" + updatedText + "\"}"))
    // .andExpect(status().isOk())
    // .andExpect(jsonPath("$.contentID").value(2))
    // .andExpect(jsonPath("$.comment").value("Updated comment text"));

    // // Verify service interaction
    // verify(commentService).updateComment(1L, 2L, updatedText);
    // verify(assembler).toModel(comment);
    // }

    @Test
    @WithMockUser(username = "husam_ramoni")
    public void testAddMediaCommentOnPost() throws Exception {
        ClassPathResource imageResource = new ClassPathResource("p1.png");
        System.out.println(imageResource.getFile().getAbsolutePath());
        if (!imageResource.exists()) {
            throw new AssertionError("Test file not found");
        }
        byte[] content = Files.readAllBytes(imageResource.getFile().toPath());

        generator();
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "p1.png", // Filename
                "image/png", // Content type
                content // Correct file content
        );

        // Configure mock service to return a predefined comment object
        given(commentService.addComment(1L, "husam_ramoni", mockFile, "Test comment")).willReturn(comment);

        // Perform the mock MVC request
        mockMvc.perform(MockMvcRequestBuilders
                .multipart("/profiles/{username}/content/{contentID}/comments", "husam_ramoni", 1L) // Make sure the
                                                                                                    // path matches your
                                                                                                    // controller's
                                                                                                    // endpoint
                .file(mockFile) // Adds the file to the request
                .param("text", "Test comment")
                .param("contentID", "1")
                .param("username", "husam_ramoni")
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated()); // Asserts the expected result
    }

}
