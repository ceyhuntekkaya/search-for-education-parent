package com.genixo.education.search.repository.content;

import com.genixo.education.search.entity.content.PostComment;
import com.genixo.education.search.enumaration.CommentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

    @Query("SELECT c FROM PostComment c WHERE c.isActive = true AND c.id = :id")
    Optional<PostComment> findByIdAndIsActiveTrue(@Param("id") Long id);

    @Query("SELECT c FROM PostComment c WHERE c.post.id = :postId AND c.parentComment IS NULL " +
            "AND c.status = :status AND c.isActive = true")
    Page<PostComment> findByPostIdAndParentCommentIsNullAndStatusAndIsActiveTrue(
            @Param("postId") Long postId,
            @Param("status") CommentStatus status,
            Pageable pageable);

    @Query("SELECT c FROM PostComment c WHERE c.parentComment.id = :parentId " +
            "AND c.status = :status AND c.isActive = true ORDER BY c.createdAt ASC")
    List<PostComment> findRepliesByParentIdAndStatus(@Param("parentId") Long parentId, @Param("status") CommentStatus status);

    @Query("SELECT c FROM PostComment c WHERE c.post.id = :postId AND c.user.id = :userId " +
            "AND c.isActive = true ORDER BY c.createdAt DESC")
    List<PostComment> findByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);

    @Modifying
    @Query("UPDATE PostComment c SET c.replyCount = c.replyCount + 1 WHERE c.id = :id")
    void incrementReplyCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE PostComment c SET c.replyCount = CASE WHEN c.replyCount > 0 THEN c.replyCount - 1 ELSE 0 END WHERE c.id = :id")
    void decrementReplyCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE PostComment c SET c.likeCount = c.likeCount + 1 WHERE c.id = :id")
    void incrementLikeCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE PostComment c SET c.likeCount = CASE WHEN c.likeCount > 0 THEN c.likeCount - 1 ELSE 0 END WHERE c.id = :id")
    void decrementLikeCount(@Param("id") Long id);

    @Query("SELECT COUNT(c) FROM PostComment c WHERE c.post.id = :postId AND c.status = :status AND c.isActive = true")
    long countByPostIdAndStatus(@Param("postId") Long postId, @Param("status") CommentStatus status);

    @Query("SELECT c FROM PostComment c WHERE c.status = 'PENDING' AND c.isActive = true ORDER BY c.createdAt ASC")
    List<PostComment> findPendingComments();

    @Query("SELECT c FROM PostComment c WHERE c.isFlagged = true AND c.flagCount >= :threshold AND c.isActive = true")
    List<PostComment> findFlaggedComments(@Param("threshold") Integer threshold);

    @Modifying
    @Query("DELETE FROM PostComment c WHERE c.post.school.id = :schoolId")
    void deleteBySchoolId(@Param("schoolId") Long schoolId);
}
