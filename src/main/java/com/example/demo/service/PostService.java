package com.example.demo.service;

import com.example.demo.dto.PostDTO;
import com.example.demo.entity.ImageModel;
import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.exceptions.PostNotFoundException;
import com.example.demo.facade.PostFacade;
import com.example.demo.repository.ImageRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final PostFacade postFacade;

    public Post createPost(PostDTO postDTO, Principal principal) {
        User user = getUserByPrincipal(principal);
        Post post = new Post();
        post.setUser(user);
        post.setCaption(postDTO.getCaption());
        post.setLocation(postDTO.getLocation());
        post.setTitle(postDTO.getTitle());
        post.setLikes(0);
        user.getPosts().add(post);
        log.info("Saving Post for User: {} ", user.getEmail());
        return postRepository.saveAndFlush(post);
    }

    public List<PostDTO> getAllPosts(Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<Post> posts = postRepository.findAllByOrderByCreatedDateDesc(pageable);

        List<PostDTO> postDTOS = new ArrayList<>();
        for (Post post : posts.getContent()) {
            PostDTO postDTO = postFacade.postToPostDTO(post);

            Optional<ImageModel> byUserId = imageRepository.findByUserId(post.getUser().getId());
            byUserId.ifPresent(imageModel -> postDTO.setImagePerson(decompressBytes(imageModel.getImageBytes())));
            postDTOS.add(postDTO);
        }
        return postDTOS;
    }


    public List<Post> getAllPostForUser(Principal principal) {
        User user = getUserByPrincipal(principal);
        return postRepository.findAllByUserOrderByCreatedDateDesc(user);
    }

    public Post likePost(Long id, String userName) {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("Post cannot be found"));
        Optional<String> userLiked = post.getLikedUsers().stream().filter(u -> u.equals(userName)).findAny();
        if (userLiked.isPresent()) {
            post.setLikes(post.getLikes() - 1);
            post.getLikedUsers().remove(userName);
        } else {
            post.setLikes(post.getLikes() + 1);
            post.getLikedUsers().add(userName);
        }
        return postRepository.saveAndFlush(post);
    }
    public List<Post> getPostsForUser(String email){
        User user = userRepository.findUserByEmail(email)
                .orElseGet(() -> userRepository.findUserByUserName(email)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + email)));
       return postRepository.findAllByUserOrderByCreatedDateDesc(user);

    }
    public void deletePost(Long postId, Principal principal) {
        Optional<User> user = userRepository.findUserByEmail(principal.getName());
        if (user.isPresent()) {
            Post post = getPostById(postId, principal);
            Optional<ImageModel> imageModel = imageRepository.findByPostId(post.getId());
            if (imageModel.isPresent()) {
                List<Post> posts = user.get().getPosts();
                posts.remove(post);
                user.get().setPosts(posts);
                userRepository.saveAndFlush(user.get());
                postRepository.delete(post);
                imageRepository.delete(imageModel.get());
            }
        }
        log.info("Post deleted");
    }


    public Post getPostById(Long postId, Principal principal) {
        User user = getUserByPrincipal(principal);
        return postRepository.findPostByIdAndUser(postId, user).
                orElseThrow(() -> new PostNotFoundException("Post cannot be found for userName: " + user.getEmail()));
    }

    private User getUserByPrincipal(Principal principal) {
        String userName = principal.getName();
        return userRepository.findUserByEmail(userName).orElseThrow(() -> new UsernameNotFoundException("UserName not found " + userName));
    }

    private static byte[] decompressBytes(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            outputStream.close();
        } catch (IOException | DataFormatException e) {
            log.error("Cannot decompress Bytes");
        }
        return outputStream.toByteArray();
    }

}
