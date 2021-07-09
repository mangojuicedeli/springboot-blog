package com.mingu.restfulwebapp.user.controller;

import com.mingu.restfulwebapp.post.Post;
import com.mingu.restfulwebapp.exception.PostNotFoundException;
import com.mingu.restfulwebapp.post.PostRepository;
import com.mingu.restfulwebapp.post.PostService;
import com.mingu.restfulwebapp.user.User;
import com.mingu.restfulwebapp.exception.UserNotFoundException;
import com.mingu.restfulwebapp.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final PostService postService;

    @GetMapping("/all")
    public List<User> retrieveAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public EntityModel retrieveUser(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        if (!user.isPresent()) {
            throw new UserNotFoundException(String.format("ID[%s] not found", id));
        }

        /* HATEOAS */
        EntityModel entityModel = EntityModel.of(user);
        // 개별 사용자 조회에서 할 수 있는 추가 작업으로 Link 객체에 '전체 사용자 조회' uri를 넣어준다.
        WebMvcLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrieveAllUsers());
        // Link 객체를 만든 후, entityModel에 all-users 라는 이름으로 추가한다.
        entityModel.add(linkTo.withRel("all-users"));
        return entityModel;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.removeUserById(id);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User savedUser = userService.create(user);
        /*
        생성 완료된 Resource를 조회할 수 있는 URI를 location 이라는 이름으로 헤더값에 실어서 보낸다.
        ServletUriComponentBuilder는 uri를 생성해서 보낼 수 있는 클래스이다.
         */
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}/posts")
    public List<Post> retrieveAllPostsByUser(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        if (!user.isPresent()) {
            throw new UserNotFoundException(String.format("ID[%s] not found", id));
        }
        return user.get().getPosts();
    }

    @PostMapping("/{id}/posts")
    public ResponseEntity<User> createPost(@PathVariable Long id, @RequestBody Post post) {
        Optional<User> user = userService.getUserById(id);
        if (!user.isPresent()) {
            throw new UserNotFoundException(String.format("ID[%s] not found", id));
        }
        post.setUser(user.get());
        Post savedPost = postService.create(post);

        /*
        생성 완료된 Resource를 조회할 수 있는 URI를 location 이라는 이름으로 헤더값에 실어서 보낸다.
        ServletUriComponentBuilder는 uri를 생성해서 보낼 수 있는 클래스이다.
         */
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedPost.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{userId}/posts/{postId}")
    public EntityModel retrievePostByUser(@PathVariable Long userId, @PathVariable Long postId) {
        Optional<User> user = userService.getUserById(userId);
        if (!user.isPresent()) {
            throw new UserNotFoundException(String.format("User ID[%s] not found", userId));
        }

        Optional<Post> post = postService.getPostById(postId);
        if (!post.isPresent()) {
            throw new PostNotFoundException(String.format("Post ID[%s] not found", postId));
        }

        EntityModel entityModel = EntityModel.of(post);
        WebMvcLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrieveAllPostsByUser(userId));
        entityModel.add(linkTo.withRel("all-posts-of-user"));
        return entityModel;
    }

    @DeleteMapping("/posts/{postId}")
    public void deletePost(@PathVariable Long postId) {
        Optional<Post> post = postService.getPostById(postId);
        if (!post.isPresent()) {
            throw new PostNotFoundException(String.format("Post ID[%s] not found", postId));
        }
        postService.removePostById(postId);
    }
}
