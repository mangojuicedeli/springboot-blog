package com.mingu.restfulwebapp.user;

import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/jpa")
public class UserJpaController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @GetMapping("/users")
    public List<User> retrieveAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/users/{id}")
    public EntityModel retrieveUser(@PathVariable int id) {
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent()) {
            throw new UserNotFoundException(String.format("ID[%s] not found", id));
        }

        /*
        HATEOAS
         */
        EntityModel entityModel = EntityModel.of(user);
        // 개별 사용자 조회에서 할 수 있는 추가 작업으로 Link 객체에 '전체 사용자 조회' uri를 넣어준다.
        WebMvcLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrieveAllUsers());
        // Link 객체를 만든 후, entityModel에 all-users 라는 이름으로 추가한다.
        entityModel.add(linkTo.withRel("all-users"));
        return entityModel;
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable int id) {
        userRepository.deleteById(id);
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User savedUser = userRepository.save(user);
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

    @GetMapping("/users/{id}/posts")
    public List<Post> retrieveAllPostsByUser(@PathVariable int id) {
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent()) {
            throw new UserNotFoundException(String.format("ID[%s] not found", id));
        }
        return user.get().getPosts();
    }

    @PostMapping("/users/{id}/posts")
    public ResponseEntity<User> createPost(@PathVariable int id, @RequestBody Post post) {
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent()) {
            throw new UserNotFoundException(String.format("ID[%s] not found", id));
        }
        post.setUser(user.get());
        Post savedPost = postRepository.save(post);
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

    @GetMapping("/users/{userId}/posts/{postId}")
    public EntityModel retrievePostByUser(@PathVariable int userId, @PathVariable int postId) {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            throw new UserNotFoundException(String.format("User ID[%s] not found", userId));
        }

        Optional<Post> post = postRepository.findById(postId);
        if (!post.isPresent()) {
            throw new PostNotFoundException(String.format("Post ID[%s] not found", postId));
        }

        EntityModel entityModel = EntityModel.of(post);
        WebMvcLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrieveAllPostsByUser(userId));
        entityModel.add(linkTo.withRel("all-posts-of-user"));
        return entityModel;
    }
}
