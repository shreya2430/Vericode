package com.vericode.repository;

import com.vericode.model.Language;
import com.vericode.model.PRStatus;
import com.vericode.model.PullRequest;
import com.vericode.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PullRequestRepository extends JpaRepository<PullRequest, Long> {

    List<PullRequest> findByAuthor(User author);

    List<PullRequest> findByAuthorId(Long authorId);

    List<PullRequest> findByStatus(PRStatus status);

    List<PullRequest> findByLanguage(Language language);
}