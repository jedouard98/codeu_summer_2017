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

package codeu.chat.client.core;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread;

import codeu.chat.common.BasicController;
import codeu.chat.common.ConversationHeader;
import codeu.chat.common.Message;
import codeu.chat.common.NetworkCode;
import codeu.chat.common.User;
import codeu.chat.common.CleverBotUser;
import codeu.chat.util.Logger;
import codeu.chat.util.Serializers;
import codeu.chat.util.Uuid;
import codeu.chat.util.connections.Connection;
import codeu.chat.util.connections.ConnectionSource;

final class Controller implements BasicController {

  private final static Logger.Log LOG = Logger.newLog(Controller.class);

  private final ConnectionSource source;

  public Controller(ConnectionSource source) {
    this.source = source;
  }
  
  @Override
  public CleverBotUser newBot(String name, Uuid conversation) {

    CleverBotUser response = null;

    try (final Connection connection = source.connect()) {

      Serializers.INTEGER.write(connection.out(), NetworkCode.NEW_CLEVER_BOT_REQUEST);
      Serializers.STRING.write(connection.out(), name);
      Uuid.SERIALIZER.write(connection.out(), conversation);

      LOG.info("newBot: Request completed.");

      if (Serializers.INTEGER.read(connection.in()) == NetworkCode.NEW_CLEVER_BOT_RESPONSE) {
        response = Serializers.nullable(CleverBotUser.SERIALIZER).read(connection.in());
        LOG.info("newBot: Response completed.");
      } else {
        LOG.error("Response from server failed.");
      }
    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }

    return response;
  }

  @Override
  public int togglePermission(Uuid user, Uuid userToBeChanged, int permission, Uuid conversation) {
    int permissionResponse = -1;
    
    try (final Connection connection = source.connect()) {
      Serializers.INTEGER.write(connection.out(), NetworkCode.NEW_PERMISSION_CHANGE_REQUEST);
      Uuid.SERIALIZER.write(connection.out(), user);
      Uuid.SERIALIZER.write(connection.out(), userToBeChanged);
      Serializers.INTEGER.write(connection.out(), permission);
      Uuid.SERIALIZER.write(connection.out(), conversation);

      if (Serializers.INTEGER.read(connection.in()) == NetworkCode.NEW_PERMISSION_CHANGE_RESPONSE) {
        permissionResponse = Serializers.INTEGER.read(connection.in());
      } else {
        LOG.error("Response from server failed.");
      }
    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }
    return permissionResponse;
}

  @Override
  public String newStatusUpdate(Uuid user) {

    String statusUpdate = null;
    try (final Connection connection = source.connect()) {
      Serializers.INTEGER.write(connection.out(), NetworkCode.NEW_STATUS_UPDATE_REQUEST);
      Uuid.SERIALIZER.write(connection.out(), user);

      if (Serializers.INTEGER.read(connection.in()) == NetworkCode.NEW_STATUS_UPDATE_RESPONSE) {
        statusUpdate = Serializers.STRING.read(connection.in());
      } else {
        LOG.error("Response from server failed.");
      }

    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }
    return statusUpdate;
  }

  @Override
  public void unfollowUser(User userFollowing, User userToBeFollowed) {

    try (final Connection connection = source.connect()) {
      Serializers.INTEGER.write(connection.out(), NetworkCode.NEW_UNFOLLOW_USER_REQUEST);
      User.SERIALIZER.write(connection.out(), userFollowing);
      User.SERIALIZER.write(connection.out(), userToBeFollowed);

      if (!(Serializers.INTEGER.read(connection.in()) == NetworkCode.NEW_UNFOLLOW_CONVERSATION_RESPONSE)) {
        LOG.error("Response from server failed.");
      }
    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }
  }

  @Override
  public void followUser(User userFollowing, User userToBeFollowed) {

    try (final Connection connection = source.connect()) {
      Serializers.INTEGER.write(connection.out(), NetworkCode.NEW_FOLLOW_USER_REQUEST);
      User.SERIALIZER.write(connection.out(), userFollowing);
      User.SERIALIZER.write(connection.out(), userToBeFollowed);

      if (!(Serializers.INTEGER.read(connection.in()) == NetworkCode.NEW_FOLLOW_CONVERSATION_RESPONSE)) {
        LOG.error("Response from server failed.");
      }
    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }
  }

  @Override
  public void unfollowConversation(Uuid user, Uuid conversation) {
    try (final Connection connection = source.connect()) {
      Serializers.INTEGER.write(connection.out(), NetworkCode.NEW_UNFOLLOW_CONVERSATION_REQUEST);
      Uuid.SERIALIZER.write(connection.out(), user);
      Uuid.SERIALIZER.write(connection.out(), conversation);

      if (!(Serializers.INTEGER.read(connection.in()) == NetworkCode.NEW_UNFOLLOW_CONVERSATION_RESPONSE)) {
        LOG.error("Response from server failed.");
      }
    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }
  }

  @Override
  public void followConversation(Uuid user, Uuid conversation) {
    try (final Connection connection = source.connect()) {
      Serializers.INTEGER.write(connection.out(), NetworkCode.NEW_FOLLOW_CONVERSATION_REQUEST);
      Uuid.SERIALIZER.write(connection.out(), user);
      Uuid.SERIALIZER.write(connection.out(), conversation);

      if (!(Serializers.INTEGER.read(connection.in()) == NetworkCode.NEW_FOLLOW_CONVERSATION_RESPONSE)) {
        LOG.error("Response from server failed.");
      }
    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }
  }

  @Override
  public Message newMessage(Uuid author, Uuid conversation, String body) {

    Message response = null;

    try (final Connection connection = source.connect()) {

      Serializers.INTEGER.write(connection.out(), NetworkCode.NEW_MESSAGE_REQUEST);
      Uuid.SERIALIZER.write(connection.out(), author);
      Uuid.SERIALIZER.write(connection.out(), conversation);
      Serializers.STRING.write(connection.out(), body);

      if (Serializers.INTEGER.read(connection.in()) == NetworkCode.NEW_MESSAGE_RESPONSE) {
        response = Serializers.nullable(Message.SERIALIZER).read(connection.in());
      } else {
        LOG.error("Response from server failed.");
      }
    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }

    return response;
  }

  @Override
  public User newUser(String name) {

    User response = null;

    try (final Connection connection = source.connect()) {

      Serializers.INTEGER.write(connection.out(), NetworkCode.NEW_USER_REQUEST);
      Serializers.STRING.write(connection.out(), name);
      LOG.info("newUser: Request completed.");

      if (Serializers.INTEGER.read(connection.in()) == NetworkCode.NEW_USER_RESPONSE) {
        response = Serializers.nullable(User.SERIALIZER).read(connection.in());
        LOG.info("newUser: Response completed.");
      } else {
        LOG.error("Response from server failed.");
      }
    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }

    return response;
  }

  @Override
  public ConversationHeader newConversation(String title, Uuid owner)  {

    ConversationHeader response = null;

    try (final Connection connection = source.connect()) {

      Serializers.INTEGER.write(connection.out(), NetworkCode.NEW_CONVERSATION_REQUEST);
      Serializers.STRING.write(connection.out(), title);
      Uuid.SERIALIZER.write(connection.out(), owner);

      if (Serializers.INTEGER.read(connection.in()) == NetworkCode.NEW_CONVERSATION_RESPONSE) {
        response = Serializers.nullable(ConversationHeader.SERIALIZER).read(connection.in());
      } else {
        LOG.error("Response from server failed.");
      }
    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }

    return response;
  }
}
