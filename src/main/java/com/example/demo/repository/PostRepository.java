package com.example.demo.repository;

import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByUserOrderByCreatedDateDesc(User user);

    Page<Post> findAllByOrderByCreatedDateDesc(Pageable pageable);

    Optional<Post> findPostByIdAndUser(Long id, User user);

}
