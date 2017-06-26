// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package codeu.chat.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import codeu.chat.util.Serializer;
import codeu.chat.util.Serializers;
import codeu.chat.util.Time;
import codeu.chat.util.Uuid;

import java.util.HashMap;

public final class User {

  public static final Serializer<User> SERIALIZER = new Serializer<User>() {

    @Override
    public void write(OutputStream out, User value) throws IOException {

      Uuid.SERIALIZER.write(out, value.id);
      Serializers.STRING.write(out, value.name);
      Time.SERIALIZER.write(out, value.creation);

    }

    @Override
    public User read(InputStream in) throws IOException {

      return new User(
          Uuid.SERIALIZER.read(in),
          Serializers.STRING.read(in),
          Time.SERIALIZER.read(in)
      );

    }
  };

  public final Uuid id;
  public final String name;
  public final Time creation;
  public HashMap<Uuid, UserFollowing> followees;
  public HashMap<Uuid, UserFollowing> followers;

  public User(Uuid id, String name, Time creation) {
    this.id = id;
    this.name = name;
    this.creation = creation;
    this.followees = new HashMap<Uuid, UserFollowing>();
    this.followers = new HashMap<Uuid, UserFollowing>();
  }

  public String statusUpdate() {
    StringBuilder status = new StringBuilder();
    for (Uuid user : following.keySet())
      status.append(following.get(user).statusUpdate());
    return status.toString();
  }

  // User A wants to follow User B
  public static void follow(User userA, User userB) {
    UserFollowing connection = new UserFollowing(userA, userB);
    userA.following.put(userB.id, connection);
    userB.followers.put(userA.id, connection);
  }

  // User A wants to unfollow User B
  public static void unfollow(User userA, User userB) {
    userA.following.remove(userB.id);
    userB.followers.remove(userA.id);
  }

  public void addCreatedConversation(ConversationHeader conversation) {
    for (Uuid user : followers.keySet())
      followers.get(user).addCreatedConversation(conversation);
  }

  public void addJoinedConversation(ConversationHeader conversation) {
    for (Uuid user : followers.keySet())
      followers.get(user).addJoinedConversation(conversation);
  }
}
