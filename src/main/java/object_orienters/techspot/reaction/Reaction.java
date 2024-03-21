package object_orienters.techspot.reaction;



import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import object_orienters.techspot.content.Content;
import object_orienters.techspot.profile.Profile;

@Entity
@Data
@NoArgsConstructor
@Table(name = "reaction")
public class Reaction {
    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
    private String reactionId;
    @OneToOne
    private Profile reactor;
    ReactionType type;

    @ManyToOne
    @JoinColumn(name = "content_id")
    @JsonBackReference
    private Content content;

    public Reaction( Profile reactor, ReactionType type,String reactionId) {
        this.reactionId = reactionId;
        this.reactor = reactor;
        this.type = type;
    }

    public enum ReactionType {
        LIKE, DISLIKE, LOVE, SUPPORT, HAHA;
    }

}