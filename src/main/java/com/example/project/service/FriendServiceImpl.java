package com.example.project.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.project.dto.FriendDTO;
import com.example.project.dto.ResponseUserDTO;
import com.example.project.entity.Friend;
import com.example.project.entity.FriendStatus;
import com.example.project.entity.Notification;
import com.example.project.entity.NotificationType;
import com.example.project.entity.User;
import com.example.project.repository.FriendRepository;
import com.example.project.repository.NotificationRepository;
import com.example.project.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

  private final FriendRepository friendRepository;
  private final UserRepository userRepository;
  private final NotificationRepository notificationRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserService userService;
  
  @Override
  public List<FriendDTO> getFriendList(Long uid) {

    List<Friend> friends = friendRepository.findFriends(uid);

    return friends.stream().map(f -> {
             User user = f.getRequester().getUid().equals(uid) ? f.getReceiver() : f.getRequester();

             return FriendDTO.builder()
                 .ffid(f.getFfid())
                 .uid(user.getUid())
                 .name(user.getName())
                 .email(user.getEmail())
                 .profileImg(user.getProfileImg())
                 .status(FriendStatus.ACCEPTED)
                 .build();
             }).toList();
  }

  @Override
  public List<FriendDTO> searchFriend(Long uid, String keyword) {

    List<Friend> friends = friendRepository.findByNameContaining(uid, keyword);

    return friends.stream().map(f -> {
             User user = f.getRequester().getUid().equals(uid) ? f.getReceiver() : f.getRequester();

             return FriendDTO.builder()
                 .ffid(f.getFfid())
                 .uid(user.getUid())
                 .name(user.getName())
                 .email(user.getEmail())
                 .profileImg(user.getProfileImg())
                 .status(FriendStatus.ACCEPTED)
                 .build();
             }).toList();
  }

  @Override
  public List<FriendDTO> getFriendRequestList(Long uid) {
    List<Friend> friends = friendRepository.findFriendRequests(uid);

    return friends.stream().map(f -> {
             User user = f.getRequester().getUid().equals(uid) ? f.getReceiver() : f.getRequester();

             return FriendDTO.builder()
                 .ffid(f.getFfid())
                 .uid(user.getUid())
                 .name(user.getName())
                 .email(user.getEmail())
                 .profileImg(user.getProfileImg())
                 .status(FriendStatus.PENDING)
                 .build();
             }).toList();
  }

  @Override
  public List<FriendDTO> searchFriendRequestList(Long uid, String keyword) {
    List<Friend> friends = friendRepository.findFriendRequestsByNameContaining(uid, keyword);

    return friends.stream().map(f -> {
             User user = f.getRequester().getUid().equals(uid) ? f.getReceiver() : f.getRequester();

             return FriendDTO.builder()
                 .ffid(f.getFfid())
                 .uid(user.getUid())
                 .name(user.getName())
                 .email(user.getEmail())
                 .profileImg(user.getProfileImg())
                 .status(FriendStatus.PENDING)
                 .build();
             }).toList();
  }

  @Override
  public Long addFriend(Long requesterUid, Long receiverUid) {
    if (requesterUid.equals(receiverUid)) {
      return -1L;
    }
    Optional<User> requester = userRepository.findById(requesterUid);
    Optional<User> receiver = userRepository.findById(receiverUid);

    if (requester.isPresent() && receiver.isPresent()) {
      if (friendRepository.existsByRequesterAndReceiver(requester.get().getUid(), receiver.get().getUid())) {
        return -2L;
      }
      Friend friend = Friend.builder()
                        .requester(requester.get())
                        .receiver(receiver.get())
                        .status(FriendStatus.PENDING)
                        .build();
      friendRepository.save(friend);

      Notification notification = Notification.builder()
                                    .user(receiver.get())
                                    .title(requester.get().getName() + " 님으로부터 친구 요청이 왔습니다.")
                                    .url("/friends?tab=request")
                                    .type(NotificationType.ACTIVITY)
                                    .build();
      notificationRepository.save(notification);
      return 1L;
    }
    return 0L;
  }

  @Override
  public Long acceptRequest(Long requesterUid, Long receiverUid) {
    Friend friend = friendRepository.findFriend(requesterUid, receiverUid);

    if (friend == null) {
      return 0L;
    }
    
    friend.changeStatus(FriendStatus.ACCEPTED);
    friendRepository.save(friend);
    return 1L;
  }

  @Override
  public Long declineRequest(Long requesterUid, Long receiverUid) {
    Friend friend = friendRepository.findFriend(requesterUid, receiverUid);

    if (friend == null) {
      return 0L;
    }
    
    friendRepository.delete(friend);
    return 1L;
  }

  @Override
  public Long removeFriend(Long ffid) {
    Optional<Friend> friend = friendRepository.findById(ffid);

    if (friend.isPresent()) {
      friendRepository.delete(friend.get());
      return 1L;
    }
    return 0L;
  }
}
