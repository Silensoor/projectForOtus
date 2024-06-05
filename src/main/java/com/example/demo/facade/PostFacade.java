package com.example.demo.facade;

import com.example.demo.dto.PostDTO;
import com.example.demo.entity.Post;
import com.example.demo.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class PostFacade {

    public PostDTO postToPostDTO(Post post) {
        PostDTO postDTO = new PostDTO();
        postDTO.setId(post.getId());
        postDTO.setUsername(post.getUser().getUsersName());
        postDTO.setTitle(post.getTitle());
        postDTO.setLikes(post.getLikes());
        postDTO.setCaption(post.getCaption());
        postDTO.setLocation(post.getLocation());
        postDTO.setUsersLiked(post.getLikedUsers());
        postDTO.setEmail(post.getUser().getEmail());
        return postDTO;
    }
}
