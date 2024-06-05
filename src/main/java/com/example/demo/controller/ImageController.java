package com.example.demo.controller;

import com.example.demo.dto.ImageDTO;
import com.example.demo.entity.ImageModel;
import com.example.demo.payload.response.MessageResponse;
import com.example.demo.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Base64;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/image")
public class ImageController {
    private final ImageService imageService;

    @GetMapping("/{username}")
    public ResponseEntity<ImageModel> getImageForPostUser(@PathVariable String username) {
        ImageModel imageForPostUser = imageService.getImageForPostUser(username);
        return new ResponseEntity<>(imageForPostUser, HttpStatus.OK);
    }
    @GetMapping("/email/{email}")
    public ResponseEntity<ImageModel> getImageForPostUserEmail(@PathVariable String email) {
        ImageModel imageForPostUser = imageService.getImageForPostUser(email);
        return new ResponseEntity<>(imageForPostUser, HttpStatus.OK);
    }

    @PostMapping("/upload")
    public ResponseEntity<MessageResponse> uploadImageToUser(@RequestParam("file") MultipartFile file,
                                                             Principal principal) throws IOException {
        imageService.uploadImageToUser(file, principal);
        return ResponseEntity.ok(new MessageResponse("Image upload successfully"));
    }

    @PostMapping("/{postId}/upload")
    public ResponseEntity<MessageResponse> uploadImageToPost(@PathVariable("postId") String postId,
                                                             @RequestParam("file") MultipartFile file,
                                                             Principal principal) throws IOException {

        imageService.uploadImageToPost(file, principal, Long.parseLong(postId));
        return ResponseEntity.ok(new MessageResponse("Image upload successfully"));
    }

    @GetMapping("/profileImage")
    public ResponseEntity<ImageModel> getImageForUser(Principal principal) {
        ImageModel userImage = imageService.getImageToUser(principal);
        return new ResponseEntity<>(userImage, HttpStatus.OK);
    }

    @GetMapping("/{postId}/image")
    public ResponseEntity<ImageModel> getImageToPost(@PathVariable("postId") String postId) {
        ImageModel postImage = imageService.getImageToPost(Long.parseLong(postId));
        return new ResponseEntity<>(postImage, HttpStatus.OK);
    }

    @GetMapping("/{postId}/image/profiles")
    public ResponseEntity<ImageDTO> getImageProfileByPost(@PathVariable("postId") String postId) {
        ImageDTO profileImage = imageService.getImageProfileByPost(Long.parseLong(postId));
        return new ResponseEntity<>(profileImage, HttpStatus.OK);
    }
    @GetMapping("/userId/{userId}")
    public ResponseEntity<String> getImageByUserId(@PathVariable String userId) {
        ImageModel imageByUserId = imageService.getImageByUserId(Long.parseLong(userId));
        String encodedString = Base64.getEncoder().encodeToString(imageByUserId.getImageBytes());
        return new ResponseEntity<>(encodedString, HttpStatus.OK);
    }


}
