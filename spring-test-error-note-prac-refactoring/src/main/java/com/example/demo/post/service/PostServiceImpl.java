package com.example.demo.post.service;

import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.common.service.port.ClockHolder;
import com.example.demo.post.controller.port.PostService;
import com.example.demo.post.domain.Post;
import com.example.demo.post.domain.PostCreate;
import com.example.demo.post.domain.PostUpdate;
import com.example.demo.post.service.port.PostRepository;
import com.example.demo.user.domain.User;
import com.example.demo.user.service.port.UserRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Builder
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
//    private final UserService userService;   // 테스트 편의 위해 userService 이용하던 것을 userRepository를 직접 이용하는 것으로 바꿈. 의존성을 줄인 결과가 됨.
    private final UserRepository userRepository;
    private final ClockHolder clockHolder;

    public Post getById(long id) {
        return postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Posts", id));
    }

    public Post create(PostCreate postCreate) {
        User user = userRepository.getById(postCreate.getWriterId());
        Post post = Post.from(user, postCreate, clockHolder);
        return postRepository.save(post);
    }

    public Post update(long id, PostUpdate postUpdate) {
        Post post = getById(id);
        post = post.update(postUpdate, clockHolder);
        return postRepository.save(post);
    }
}