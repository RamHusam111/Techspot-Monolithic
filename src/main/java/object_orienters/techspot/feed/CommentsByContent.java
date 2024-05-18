package object_orienters.techspot.feed;

import object_orienters.techspot.comment.Comment;
import object_orienters.techspot.comment.CommentRepository;
import object_orienters.techspot.content.ContentNotFoundException;
import object_orienters.techspot.content.ReactableContent;
import object_orienters.techspot.content.ReactableContentRepository;
import object_orienters.techspot.model.Privacy;
import object_orienters.techspot.post.ContentIsPrivateException;
import object_orienters.techspot.post.Post;
import object_orienters.techspot.post.PostNotFoundException;
import object_orienters.techspot.post.PostRepository;
import object_orienters.techspot.security.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentsByContent extends Strategy<Comment, Long> {

    private final PermissionService permissionService;
    @Autowired
    public CommentsByContent(ReactableContentRepository reactableContentRepository, CommentRepository commentRepository, PermissionService permissionService) {
        this.reactableContentRepository = reactableContentRepository;
        this.commentRepository = commentRepository;
        this.permissionService = permissionService;
    }

    public Page<Comment> operate(Long  contentID, int pageNumber, int pageSize) {
        String currentUserPrincipal = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            if (!permissionService.canAccessPost(contentID, currentUserPrincipal))
                return Page.empty();
        } catch (PostNotFoundException e) {
            throw new ContentNotFoundException(contentID);
        }
        return commentRepository.findByCommentedOn(reactableContentRepository.findByContentID(contentID).orElseThrow(() -> new ContentNotFoundException(contentID )), PageRequest.of(pageNumber, pageSize, Sort.by("timestamp").descending()));
    }

}