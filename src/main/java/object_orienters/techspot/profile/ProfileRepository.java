package object_orienters.techspot.profile;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, String> {

    @Query("SELECT f FROM Profile u JOIN u.followers f WHERE u.username = :userName")
    List<Profile> findFollowersByUserId(String userName);

    @Query("SELECT f FROM Profile u JOIN u.followers f WHERE f.username = :username AND u.username = :accountUsername")
    Profile findFollowerByUsername(String accountUsername, String username);

    @Query("SELECT f FROM Profile u JOIN u.following f WHERE u.username = :userName")
    List<Profile> findFollowingByUserId(String userName);

    @Query("SELECT f FROM Profile u JOIN u.following f WHERE f.username = :username AND u.username = :accountUsername")
    Profile findFollowingByUsername(String accountUsername, String username);

    Profile findByEmail(String email);

}
