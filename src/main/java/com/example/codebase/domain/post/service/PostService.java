package com.example.codebase.domain.post.service;

import com.example.codebase.controller.dto.PageInfo;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.exception.NotFoundMemberException;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.example.codebase.domain.post.dto.*;
import com.example.codebase.domain.post.entity.*;
import com.example.codebase.domain.post.repository.PostCommentRepository;
import com.example.codebase.domain.post.repository.PostLikeMemberRepository;
import com.example.codebase.domain.post.repository.PostRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService {
  private final PostRepository postRepository;
  private final MemberRepository memberRepository;
  private final PostLikeMemberRepository postLikeMemberRepository;
  private final PostCommentRepository postCommentRepository;

  @Autowired
  public PostService(
      PostRepository postRepository,
      MemberRepository memberRepository,
      PostLikeMemberRepository postLikeMemberRepository,
      PostCommentRepository postCommentRepository) {
    this.postRepository = postRepository;
    this.memberRepository = memberRepository;
    this.postLikeMemberRepository = postLikeMemberRepository;
    this.postCommentRepository = postCommentRepository;
  }

  @Transactional
  public PostResponseDTO createPost(PostCreateDTO postCreateDTO, String loginUsername) {
    Member author =
        memberRepository.findByUsername(loginUsername).orElseThrow(NotFoundMemberException::new);

    Post newPost = Post.of(postCreateDTO, author);
    author.addPost(newPost);

    postRepository.save(newPost);
    return PostResponseDTO.from(newPost);
  }

  public PostsResponseDTO getPosts(int page, int size, String sortDirection) {
    Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), "createdTime");
    PageRequest pageRequest = PageRequest.of(page, size, sort);

    Page<Post> posts = postRepository.findAll(pageRequest);
    PageInfo pageInfo = PageInfo.of(page, size, posts.getTotalPages(), posts.getTotalElements());

    List<PostResponseDTO> dtos =
        posts.stream().map(PostResponseDTO::from).collect(Collectors.toList());
    return PostsResponseDTO.of(dtos, pageInfo);
  }

  @Transactional(readOnly = true)
  public PostsResponseDTO getPosts(String loginUsername, int page, int size, String sortDirection) {
    Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), "createdTime");
    PageRequest pageRequest = PageRequest.of(page, size, sort);

    Member member =
        memberRepository.findByUsername(loginUsername).orElseThrow(NotFoundMemberException::new);

    Page<PostWithIsLiked> postPages = postRepository.findAllWithIsLiked(member, pageRequest);
    PageInfo pageInfo =
        PageInfo.of(page, size, postPages.getTotalPages(), postPages.getTotalElements());

    List<PostResponseDTO> dtos =
        postPages.stream().map(PostResponseDTO::from).collect(Collectors.toList());
    return PostsResponseDTO.of(dtos, pageInfo);
  }

  @Transactional
  public PostResponseDTO updatePost(Long postId, PostUpdateDTO postUpdateDTO) {
    // TODO: 동일 작성자 검증 추가
    Post post =
        postRepository.findById(postId).orElseThrow(() -> new RuntimeException("존재하지 않는 게시글입니다."));

    post.update(postUpdateDTO);
    return PostResponseDTO.from(post);
  }

  @Transactional
  public void deletePost(Long postId) {
    // TODO: 동일 작성자 검증 추가
    Post post =
        postRepository.findById(postId).orElseThrow(() -> new RuntimeException("존재하지 않는 게시글입니다."));

    postRepository.delete(post);
  }

  public PostWithLikesResponseDTO getPost(Long postId) {
    Post post =
        postRepository.findById(postId).orElseThrow(() -> new RuntimeException("존재하지 않는 게시글입니다."));

    post.incressView();

    List<PostLikeMemberDTO> postLikeMemberDtos =
        post.getPostLikeMembers().stream()
            .map(
                (PostLikeMember likeMember) ->
                    PostLikeMemberDTO.of(likeMember.getMember(), likeMember.getLikedTime()))
            .collect(Collectors.toList());

    return PostWithLikesResponseDTO.create(post, postLikeMemberDtos);
  }

  // 로그인 유저는 좋아요 여부도 함께 표시
  public PostWithLikesResponseDTO getPost(String loginUsername, Long postId) {
    PostWithLikesResponseDTO post = getPost(postId);

    Member member =
        memberRepository.findByUsername(loginUsername).orElseThrow(NotFoundMemberException::new);

    postLikeMemberRepository
        .findById(PostLikeMemberIds.of(member, post.getId()))
        .ifPresent((PostLikeMember likeMember) -> post.setIsLiked(true));

    return post;
  }

  @Transactional
  public PostResponseDTO likePost(Long postId, String loginUsername) {
    Post post =
        postRepository.findById(postId).orElseThrow(() -> new RuntimeException("존재하지 않는 게시글입니다."));

    Member member =
        memberRepository.findByUsername(loginUsername).orElseThrow(NotFoundMemberException::new);

    Optional<PostLikeMember> likeMember =
        postLikeMemberRepository.findById(PostLikeMemberIds.of(member, post));

    boolean isLiked = likeMember.isPresent();
    if (isLiked) {
      postLikeMemberRepository.delete(likeMember.get());
    } else {
      PostLikeMember newLike = PostLikeMember.of(post, member);
      post.addLikeMember(newLike);
    }

    Integer likeCount = postLikeMemberRepository.countByPostId(post.getId());
    post.setLikes(likeCount);

    return PostResponseDTO.of(post, !isLiked);
  }

  @Transactional
  public PostResponseDTO createComment(
      Long postId, PostCommentCreateDTO commentCreateDTO, String loginUsername) {
    Post post =
        postRepository.findById(postId).orElseThrow(() -> new RuntimeException("존재하지 않는 게시글입니다."));

    Member author =
        memberRepository.findByUsername(loginUsername).orElseThrow(NotFoundMemberException::new);

    PostComment newComment = PostComment.of(post, commentCreateDTO, author);

    Optional<Long> parentCommentId = Optional.ofNullable(commentCreateDTO.getParentCommentId());
    if (parentCommentId.isPresent()) {
      Long perentId = parentCommentId.get();
      PostComment parentComment =
          postCommentRepository
              .findByIdAndPost(perentId, post)
              .orElseThrow(() -> new RuntimeException("존재하지 않는 댓글이거나 해당 게시글에 속해있지 않습니다."));

      // 2차 대댓글 일 시
      if (parentComment.getParent() == null) {
        newComment.setParent(parentComment);
        parentComment.addComment(newComment);
        postCommentRepository.save(newComment);
      } else {
        // 3차 대댓글 일 시
        newComment.setMentionUsername(parentComment.getAuthor().getUsername());
        newComment.setParent(parentComment.getParent());
        parentComment.getParent().addComment(newComment);
        postCommentRepository.save(newComment);
      }
    }
    else {
      post.addComment(newComment);
      postRepository.save(post);
    }

    return PostResponseDTO.from(post);
  }

  @Transactional
  public PostResponseDTO updateComment(
      Long commentId, PostCommentUpdateDTO commentUpdateDTO, String loginUsername) {
    PostComment comment =
        postCommentRepository
            .findById(commentId)
            .orElseThrow(() -> new RuntimeException("존재하지 않는 댓글입니다."));

    Member author =
        memberRepository.findByUsername(loginUsername).orElseThrow(NotFoundMemberException::new);

    if (!comment.getAuthor().equals(author)) {
      throw new RuntimeException("댓글 작성자만 수정할 수 있습니다.");
    }

    comment.update(commentUpdateDTO);

    return PostResponseDTO.from(comment.getPost());
  }

  @Transactional
  public void deleteComment(Long commentId, String loginUsername) {
    PostComment comment =
        postCommentRepository
            .findById(commentId)
            .orElseThrow(() -> new RuntimeException("존재하지 않는 댓글입니다."));

    Member author =
        memberRepository.findByUsername(loginUsername).orElseThrow(NotFoundMemberException::new);

    if (!comment.getAuthor().equals(author)) {
      throw new RuntimeException("댓글 작성자만 삭제할 수 있습니다.");
    }

    postCommentRepository.delete(comment);
  }
}
