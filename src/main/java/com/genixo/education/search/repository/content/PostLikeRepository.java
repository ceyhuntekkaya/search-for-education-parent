package com.genixo.education.search.repository.content;

import com.genixo.education.search.entity.content.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    @Query("SELECT pl FROM PostLike pl WHERE pl.post.id = :postId AND pl.user.id = :userId")
    Optional<PostLike> findByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);

    @Query("SELECT pl FROM PostLike pl WHERE pl.post.id = :postId ORDER BY pl.likedAt DESC")
    List<PostLike> findByPostIdOrderByLikedAtDesc(@Param("postId") Long postId);

    @Query("SELECT COUNT(pl) FROM PostLike pl WHERE pl.post.id = :postId")
    long countByPostId(@Param("postId") Long postId);

    @Query("SELECT COUNT(pl) FROM PostLike pl WHERE pl.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    @Query("SELECT pl FROM PostLike pl WHERE pl.user.id = :userId ORDER BY pl.likedAt DESC")
    List<PostLike> findByUserIdOrderByLikedAtDesc(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM PostLike pl WHERE pl.post.school.id = :schoolId")
    void deleteBySchoolId(@Param("schoolId") Long schoolId);
}
