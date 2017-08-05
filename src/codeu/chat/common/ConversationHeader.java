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

import java.util.HashMap;

import codeu.chat.util.Serializer;
import codeu.chat.util.Serializers;
import codeu.chat.util.Time;
import codeu.chat.util.Uuid;

public final class ConversationHeader {

  public static final Serializer<ConversationHeader> SERIALIZER = new Serializer<ConversationHeader>() {

    @Override
    public void write(OutputStream out, ConversationHeader value) throws IOException {

      Uuid.SERIALIZER.write(out, value.id);
      Uuid.SERIALIZER.write(out, value.owner);
      Time.SERIALIZER.write(out, value.creation);
      Serializers.STRING.write(out, value.title);

    }

    @Override
    public ConversationHeader read(InputStream in) throws IOException {

      return new ConversationHeader(
          Uuid.SERIALIZER.read(in),
          Uuid.SERIALIZER.read(in),
          Time.SERIALIZER.read(in),
          Serializers.STRING.read(in)
      );
    }
  };

  // bits representing different permissions each has
  public static final byte OWNER_PERM  = 0b0100;
  public static final byte ADMIN_PERM  = 0b0010;
  public static final byte MEMBER_PERM = 0b0001;

  public final Uuid id;
  public final Uuid owner;
  public final Time creation;
  public final String title;

  public int size;
  public HashMap<Uuid, Byte> permissions;

  public ConversationHeader(Uuid id, Uuid owner, Time creation, String title) {

    this.id = id;
    this.owner = owner;
    this.creation = creation;
    this.title = title;
    this.size = 0;
    this.permissions = new HashMap<Uuid, Byte>();
    permissions.put(owner, OWNER_PERM);
  }

  public String formatPermission(int permission) {
    String ownerStatus = "";
    String adminStatus = "";
    String memberStatus = "";

    if (isOwner(permission)) {
      ownerStatus = "Owner";
    }
    if (isAdmin(permission)) {
      adminStatus = "Admin";
    }
    if (isMember(permission)) {
      memberStatus = "Member";
    }
    return String.format("%s %s %s", ownerStatus, adminStatus, memberStatus);
  }

  public static boolean isOwner(int permission) {
    return ((permission & OWNER_PERM) > 0);
  }

  public static boolean isAdmin(int permission) {
    return ((permission & ADMIN_PERM) > 0);
  }

  public static boolean isMember(int permission) {
    return ((permission & MEMBER_PERM) > 0);
  }

  public byte getPermission(Uuid user){
    if (permissions.containsKey(user))
      return permissions.get(user);
    return -1;
  }

  public void togglePermission(Uuid user, byte permission) {
    if (permissions.containsKey(user)) {
      byte newPermission = (byte) (permissions.get(user) ^ permission);
      if (newPermission == 0) {
        permissions.remove(user);
      }
      else {
        permissions.put(user, newPermission);
      }
      return;
    }
    permissions.put(user, permission);
  }
}
