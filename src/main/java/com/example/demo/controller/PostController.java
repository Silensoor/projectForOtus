package com.example.demo.controller;

import com.example.demo.dto.PostDTO;
import com.example.demo.entity.Post;
import com.example.demo.facade.PostFacade;
import com.example.demo.payload.response.MessageResponse;
import com.example.demo.service.PostService;
import com.example.demo.validations.ResponseErrorValidation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {
    private final PostFacade postFacade;
    private final PostService postService;
    private final ResponseErrorValidation errorValidation;


    @PostMapping("/create")
    public ResponseEntity<Object> createPost(@Valid @RequestBody PostDTO postDTO, BindingResult bindingResult, Principal principal) {
        ResponseEntity<Object> objectResponseEntity = errorValidation.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(objectResponseEntity)) return objectResponseEntity;
        Post post = postService.createPost(postDTO, principal);
        PostDTO createdPost = postFacade.postToPostDTO(post);
        return new ResponseEntity<>(createdPost, HttpStatus.OK);
    }

    @GetMapping("/all/{page}")
    public ResponseEntity<List<PostDTO>> getAllPosts(@PathVariable String page) {
        List<PostDTO> allPosts = postService.getAllPosts(Integer.parseInt(page));
        return new ResponseEntity<>(allPosts, HttpStatus.OK);
    }

    @GetMapping("/user/posts")
    public ResponseEntity<List<PostDTO>> getAllPostsForUser(Principal principal) {
        List<PostDTO> postDTOS = postService.getAllPostForUser(principal)
                .stream()
                .map(postFacade::postToPostDTO)
                .toList();
        return new ResponseEntity<>(postDTOS, HttpStatus.OK);
    }

    @PostMapping("/{postId}/{username}/like")
    public ResponseEntity<PostDTO> likePost(@PathVariable("postId") String postId,
                                            @PathVariable("username") String username) {
        Post post = postService.likePost(Long.parseLong(postId), username);
        PostDTO postDTO = postFacade.postToPostDTO(post);
        return new ResponseEntity<>(postDTO, HttpStatus.OK);
    }

    @PostMapping("/{postId}/delete")
    public ResponseEntity<MessageResponse> deletePost(@PathVariable("postId") String postId, Principal principal) {
        postService.deletePost(Long.parseLong(postId), principal);
        return new ResponseEntity<>(new MessageResponse("Post was deleted"), HttpStatus.OK);
    }

    @GetMapping("/{email}")
    public ResponseEntity<List<PostDTO>> getPostsForUser(@PathVariable String email) {
        List<Post> post = postService.getPostsForUser(email);
        List<PostDTO> postDTO = post.stream().map(postFacade::postToPostDTO).toList();
        return new ResponseEntity<>(postDTO, HttpStatus.OK);
    }


}
