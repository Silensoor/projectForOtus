package com.example.demo.service;

import com.example.demo.dto.DialogDTO;
import com.example.demo.dto.ImageDTO;
import com.example.demo.entity.Dialog;
import com.example.demo.entity.ImageModel;
import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.exceptions.ImageNotFoundException;
import com.example.demo.repository.DialogRepository;
import com.example.demo.repository.ImageRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final DialogRepository dialogRepository;


    public List<DialogDTO> setImageByDialogs(List<DialogDTO> dialogDTOS, Principal principal) {
        User user = getUserByPrincipal(principal);
        return dialogDTOS.stream().peek(t -> {
            if (user.getId().equals(t.getFirstPersonId())) {
                Optional<ImageModel> byUserId = imageRepository.findByUserId(t.getSecondPersonId());
                byUserId.ifPresent(imageModel -> t.setImage(decompressBytes(imageModel.getImageBytes())));
            } else {
                Optional<ImageModel> byUserId = imageRepository.findByUserId(t.getFirstPersonId());
                byUserId.ifPresent(imageModel -> t.setImage(decompressBytes(imageModel.getImageBytes())));
            }

        }).toList();
    }

    private byte[] compressBytes(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            log.error("Cannot compress Bytes");
        }

        log.info("Compressed Image Byte Size - " + outputStream.toByteArray().length);
        return outputStream.toByteArray();
    }

    public byte[] getImageByteByDialogId(Long id) {
        Optional<Dialog> dialog = dialogRepository.findById(id);
        Optional<ImageModel> byUserId = null;
        if (dialog.isPresent()) {
            byUserId = imageRepository.findByUserId(dialog.get().getSecondPersonId());
        }

        return Objects.requireNonNull(byUserId).map(ImageModel::getImageBytes).orElse(null);


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

    public ImageModel getImageForPostUser(String email) {
        User user = userRepository.findUserByEmail(email)
                .orElseGet(() -> userRepository.findUserByUserName(email)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + email)));


        ImageModel imageModel = imageRepository.findByUserId(user.getId()).orElse(null);
        if (imageModel != null) {
            imageModel.setImageBytes(decompressBytes(imageModel.getImageBytes()));
        }
        return imageModel;
    }

    public ImageModel getImageByUserId(Long userId) {
        ImageModel imageModel = imageRepository.findByUserId(userId).orElse(null);
        if (imageModel != null) {
            imageModel.setImageBytes(decompressBytes(imageModel.getImageBytes()));
        }
        return imageModel;
    }

    public void uploadImageToUser(MultipartFile file, Principal principal) throws IOException {
        User user = getUserByPrincipal(principal);
        log.info("Uploading image profile to User {}", user.getUsername());

        ImageModel userProfileImage = imageRepository.findByUserId(user.getId()).orElse(null);
        if (!ObjectUtils.isEmpty(userProfileImage)) {
            imageRepository.delete(userProfileImage);
        }
        ImageModel imageModel = new ImageModel();
        imageModel.setUserId(user.getId());
        imageModel.setImageBytes(compressBytes(file.getBytes()));
        imageModel.setName(file.getOriginalFilename());
        imageRepository.saveAndFlush(imageModel);

    }

    public void uploadImageToPost(MultipartFile file, Principal principal, Long postId) throws IOException {
        User user = getUserByPrincipal(principal);
        List<Post> posts = user.getPosts()
                .stream().filter(p -> p.getId().equals(postId)).toList();

        ImageModel imageModel = new ImageModel();
        imageModel.setPostId(posts.isEmpty() ? null : posts.get(0).getId());
        imageModel.setImageBytes(compressBytes(file.getBytes()));
        imageModel.setName(file.getOriginalFilename());
        log.info("Uploading image to Post {}", posts.isEmpty() ? null : posts.get(0).getId());
        imageRepository.saveAndFlush(imageModel);
    }

    public ImageModel getImageToUser(Principal principal) {
        User user = getUserByPrincipal(principal);
        ImageModel imageModel = imageRepository.findByUserId(user.getId()).orElse(null);
        if (!ObjectUtils.isEmpty(imageModel)) {
            imageModel.setImageBytes(decompressBytes(imageModel.getImageBytes()));
        }
        return imageModel;
    }

    public ImageModel getImageToPost(Long postId) {
        ImageModel imageModel = imageRepository.findByPostId(postId).
                orElseThrow(() -> new ImageNotFoundException("Cannot find image to Post: " + postId));
        if (!ObjectUtils.isEmpty(imageModel)) {
            imageModel.setImageBytes(decompressBytes(imageModel.getImageBytes()));
        }
        return imageModel;
    }


    private User getUserByPrincipal(Principal principal) {
        String userName = principal.getName();
        return userRepository.findUserByEmail(userName).orElseThrow(() -> new UsernameNotFoundException("UserName not found " + userName));
    }

    public ImageDTO getImageProfileByPost(Long id) {
        Optional<Post> byId = postRepository.findById(id);
        ImageDTO imageDTO = new ImageDTO();
        if (byId.isPresent()) {
            Optional<ImageModel> byUserId = imageRepository.findByUserId(byId.get().getUser().getId());
            imageDTO.setImageBytes(decompressBytes(byUserId.get().getImageBytes()));
            imageDTO.setId(id);
        }
        return imageDTO;

    }
}
