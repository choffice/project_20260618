package com.example.project.service;

import java.util.List;

import com.example.project.dto.FriendDTO;
import com.example.project.dto.ResponseUserDTO;
import com.example.project.entity.Friend;

public interface FriendService {

  List<FriendDTO> getFriendList(Long uid);
  
  List<FriendDTO> searchFriend(Long uid, String keyword);
  
  List<FriendDTO> getFriendRequestList(Long uid);

  List<FriendDTO> searchFriendRequestList(Long uid, String keyword);

  Long addFriend(Long requesterUid, Long receiverUid);

  Long removeFriend(Long ffid);

  Long acceptRequest(Long requesterUid, Long receiverUid);

  Long declineRequest(Long requesterUid, Long receiverUid);
}
