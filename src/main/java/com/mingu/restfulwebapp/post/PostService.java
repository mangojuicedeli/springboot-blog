package com.mingu.restfulwebapp.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public Post create(Post post) {
        return null;
    }

    public Optional<Post> getPostById(Long postId) {
        return null;
    }

    public void removePostById(Long postId) {
    }
}
