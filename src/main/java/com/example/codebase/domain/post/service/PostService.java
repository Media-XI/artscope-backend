package com.example.codebase.domain.post.service;


import com.example.codebase.controller.dto.PageInfo;
import com.example.codebase.domain.post.dto.PostCreateDTO;
import com.example.codebase.domain.post.dto.PostResponseDTO;
import com.example.codebase.domain.post.dto.PostUpdateDTO;
import com.example.codebase.domain.post.dto.PostsResponseDTO;
import com.example.codebase.domain.post.entity.Post;
import com.example.codebase.domain.post.repository.PostRepository;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.exception.NotFoundMemberException;
import com.example.codebase.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;

    private final MemberRepository memberRepository;
    @Autowired
    public PostService(PostRepository postRepository, MemberRepository memberRepository) {
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }


    public PostResponseDTO createPost(PostCreateDTO postCreateDTO, String loginUsername) {
        Member author = memberRepository.findByUsername(loginUsername).orElseThrow(() -> new NotFoundMemberException());

        Post newPost = Post.of(postCreateDTO, author);
        postRepository.save(newPost);
        return PostResponseDTO.of(newPost);
    }

    public PostsResponseDTO getPosts(int page, int size, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), "createdTime");
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<Post> posts = postRepository.findAll(pageRequest);
        PageInfo pageInfo = PageInfo.of(page, size, posts.getTotalPages(), posts.getTotalElements());

        List<PostResponseDTO> dtos = posts.stream()
                .map(PostResponseDTO::from)
                .collect(Collectors.toList());
        return PostsResponseDTO.of(dtos, pageInfo);
    }


    @Transactional
    public PostResponseDTO updatePost(Long postId, PostUpdateDTO postUpdateDTO) {
        // TODO: 동일 작성자 검증 추가
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("존재하지 않는 게시글입니다."));

        post.update(postUpdateDTO);
        return PostResponseDTO.of(post);
    }

    @Transactional
    public void deletePost(Long postId) {
        // TODO: 동일 작성자 검증 추가
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("존재하지 않는 게시글입니다."));

        postRepository.delete(post);
    }

    public PostResponseDTO getPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("존재하지 않는 게시글입니다."));

        post.incressView();

        return PostResponseDTO.of(post);
    }
}
