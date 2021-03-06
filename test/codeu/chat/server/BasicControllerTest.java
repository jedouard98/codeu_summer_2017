// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package codeu.chat.server;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

import codeu.chat.client.core.Context;
import codeu.chat.common.BasicController;
import codeu.chat.common.ConversationHeader;
import codeu.chat.common.Message;
import codeu.chat.common.User;
import codeu.chat.util.Tokenizer;
import codeu.chat.util.Uuid;

public final class BasicControllerTest {

  private Model model;
  private BasicController controller;

  @Before
  public void doBefore() {
    model = new Model();
    controller = new Controller(Uuid.NULL, model);
  }

  @Test
  public void testAddUser() {

    final User user = controller.newUser("user");

    assertFalse("Check that user has a valid reference", user == null);
  }

  @Test
  public void testAddConversation() {

    final User user = controller.newUser("user");

    assertFalse("Check that user has a valid reference", user == null);

    final ConversationHeader conversation = controller.newConversation("conversation", user.id);

    assertFalse("Check that conversation has a valid reference", conversation == null);
  }

  @Test
  public void testAddMessage() {

    final User user = controller.newUser("user");

    assertFalse("Check that user has a valid reference", user == null);

    final ConversationHeader conversation = controller.newConversation("conversation", user.id);

    assertFalse("Check that conversation has a valid reference", conversation == null);

    final Message message = controller.newMessage(user.id, conversation.id, "Hello World");

    assertFalse("Check that the message has a valid reference", message == null);
  }

  /**
   * adds test for version function
   */
  @Test
  public void testGetVersion() {

    assertFalse("ERROR: Exception during call on server. Check log for details.", model == null);
    String str = model.version();
    assertNotNull(str);
  }

  /**
   * adds test for Uptime
   */
  @Test
  public void testUpTime() {

    model.uptime();
    final long l = 0;

    assertFalse("Response from server failed.", model == null);
    assertTrue(model.uptime() >= l);

  }

  /**
   * adds test for Tokenizer
   */
  @Test
  public void testTokenizer() {

    String str = "hi how are you";

    Tokenizer t = new Tokenizer(str);

    assertEquals("hi", t.next());
    assertEquals("how", t.next());
    assertEquals("are", t.next());
    assertEquals("you", t.next());
    assertFalse(t.hasNext());

    String str1 = "\"hello how\" are \"you\"";
    Tokenizer t1 = new Tokenizer(str1);

    assertEquals("hello how", t1.next());
    assertEquals("are", t1.next());
    assertEquals("you", t1.next());
    assertFalse(t1.hasNext());

    Tokenizer t2 = new Tokenizer("hi \"how are you");
    assertEquals("hi", t2.next());
    assertEquals("how are you", t2.next());
    assertFalse(t2.hasNext());
  }

  public void testFollowUser() {
    final User user = controller.newUser("user");
    final User user1 = controller.newUser("user");

    User.follow(user, user1);
    assertEquals(1, user.followees.size());
  }

  public void testFollowConvo() {

    final User user = controller.newUser("user");
    final ConversationHeader convo1 = controller.newConversation("convo", user.id);

    controller.followConversation(user.id, convo1.id);
    assertEquals(1, user.followees.size());
  }

  public void testPermissionsToggle() {
    final User user = controller.newUser("user");
    final ConversationHeader convo1 = controller.newConversation("convo", user.id);

    assertFalse(ConversationHeader.isMember(convo1.getPermission(user.id)));

    convo1.togglePermission(user.id, ConversationHeader.MEMBER_PERM);

    assertTrue(ConversationHeader.isMember(convo1.getPermission(user.id)));
    assertFalse(ConversationHeader.isAdmin(convo1.getPermission(user.id)));
    assertFalse(ConversationHeader.isOwner(convo1.getPermission(user.id)));
  }
}
