package codeu.chat.server;

import codeu.chat.common.User;
import codeu.chat.common.ConversationHeader;
import codeu.chat.common.Message;
import codeu.chat.util.Uuid;
import codeu.chat.util.Tokenizer;
import codeu.chat.util.Time;
import codeu.chat.server.Controller;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.File;

import java.io.IOException;
import java.lang.InterruptedException;

public final class TransactionLog {

  private final BlockingQueue<String> transactionsList = new LinkedBlockingQueue<>();
  private Controller controller;
  private String fileName;

  private final Model model;

  public TransactionLog(Controller controller, String fileName, Model model) {
    this.controller = controller;
    this.fileName = fileName;
    this.model = model;
  }

  // takes in information that the server needs to populate itself!
  public void read() throws IOException {
    File file = new File(fileName);
    if (file.exists()) {
      FileReader transactions = new FileReader(file);
      String line = null;
      BufferedReader in = new BufferedReader(transactions);
      while ((line = in.readLine()) != null) {
        Tokenizer tokens = new Tokenizer(line);
        tokens.next();
        switch (tokens.next()) {
          case "CREATEUSER" : readUser(tokens); break;
          case "CREATEMESSAGE" : readMessage(tokens); break;
          case "CREATECONVO" : readConvo(tokens); break;
          case "FOLLOWCONVO" : readFollowConvo(tokens); break;
          case "FOLLOWUSER" : readFollowUser(tokens); break;
          case "UNFOLLOWCONVO" : readUnfollowConvo(tokens); break;
          case "UNFOLLOWUSER" : readUnfollowUser(tokens); break;
          case "CREATESTATUSUPDATE" : readStatusUpdate(tokens); break;
        }
      }
    }
  }

  private void readStatusUpdate(Tokenizer tokens) throws IOException {
    tokens.next();
    Uuid userUuid = Uuid.parse(tokens.next());
    User user = model.userById().first(userUuid);

    controller.newStatusUpdate(userUuid);
    System.out.println("I did a thing!!!");
  }

  private void readUnfollowUser(Tokenizer tokens) throws IOException {
    tokens.next();
    Uuid userUnfollowingUuid = Uuid.parse(tokens.next());
    User userUnfollowing = model.userById().first(userUnfollowingUuid);
    tokens.next();
    Uuid userToBeUnfollowedUuid = Uuid.parse(tokens.next());
    User userToBeUnfollowed = model.userById().first(userToBeUnfollowedUuid);

    controller.unfollowUser(userUnfollowing, userToBeUnfollowed);
  }

  private void readFollowUser(Tokenizer tokens) throws IOException {
    tokens.next();
    Uuid userFollowingUuid = Uuid.parse(tokens.next());
    User userFollowing = model.userById().first(userFollowingUuid);
    tokens.next();
    Uuid userToBeFollowedUuid = Uuid.parse(tokens.next());
    User userToBeFollowed = model.userById().first(userToBeFollowedUuid);

    controller.followUser(userFollowing, userToBeFollowed);
  }

  private void readUnfollowConvo(Tokenizer tokens) throws IOException {
    tokens.next();
    Uuid userUuid = Uuid.parse(tokens.next());
    tokens.next();
    Uuid convoUuid = Uuid.parse(tokens.next());

    controller.unfollowConversation(userUuid, convoUuid);
  }

  private void readFollowConvo(Tokenizer tokens) throws IOException {
    tokens.next();
    Uuid userUuid = Uuid.parse(tokens.next());
    tokens.next();
    Uuid convoUuid = Uuid.parse(tokens.next());

    controller.followConversation(userUuid, convoUuid);
  }

  private void readUser(Tokenizer tokens) throws IOException {
    // TODO: fix tokenized input to avoid excessive .next() calls
    tokens.next();
    Uuid id = Uuid.parse(tokens.next());
    tokens.next();
    String name = tokens.next();
    tokens.next();
    Time creation = Time.fromMs(new Long(tokens.next()));
    controller.newUser(id, name, creation);
  }

  private void readMessage(Tokenizer tokens) throws IOException {
    tokens.next();
    Uuid id = Uuid.parse(tokens.next());
    tokens.next();
    Uuid author = Uuid.parse(tokens.next());
    tokens.next();
    Uuid convo = Uuid.parse(tokens.next());
    tokens.next();
    String body = tokens.next();
    tokens.next();
    Time creation = Time.fromMs(new Long(tokens.next()));
    controller.newMessage(id, author, convo, body, creation);
  }

  private void readConvo(Tokenizer tokens) throws IOException {
    tokens.next();
    Uuid id = Uuid.parse(tokens.next());
    tokens.next();
    Uuid owner = Uuid.parse(tokens.next());
    tokens.next();
    Time creation = Time.fromMs(new Long(tokens.next()));
    tokens.next();
    String title = tokens.next();
    controller.newConversation(id, title, owner, creation);
  }

  // takes all items and flushes it to disc format!
  public void flush() throws IOException {
    FileWriter writer = new FileWriter("transactions.txt", true);
    for (String transaction : transactionsList)
      writer.write(transaction + "\n");
    writer.flush();
    this.transactionsList.clear();
  }

  public void writeStatusUpdate(Uuid user) throws InterruptedException {
    String command = "CREATESTATUSUPDATE";
    String uuid = user.toString();
    String transaction = String.format("Command: %s Uuid: %s", command, uuid);

    transactionsList.put(transaction);
    System.out.println("I did a thing!!!");
  }

  public void writeCreateUser(User user) throws InterruptedException {
    String command = "CREATEUSER";

    String uuid = user.id.toString();
    String name = user.name;
    long creation = user.creation.inMs();

    String transaction = String.format("Command: %s Uuid: %s Name: \"%s\" Creation: %d", command, uuid, name, creation);

    transactionsList.put(transaction);
  }

  public void writeCreateConversation(ConversationHeader conversation) throws InterruptedException {
    String command = "CREATECONVO";

    String uuid = conversation.id.toString();
    String owner = conversation.owner.toString();
    long creation = conversation.creation.inMs();
    String title = conversation.title;

    String transaction = String.format("Command: %s Uuid: %s Owner: %s Creation: %d Title: \"%s\"", command, uuid, owner, creation, title);

    transactionsList.put(transaction);
  }

  public void writeCreateMessage(Message message, Uuid conversation) throws InterruptedException {
    String command = "CREATEMESSAGE";

    String uuid = message.id.toString();
    String author = message.author.toString();
    String convo = conversation.toString();
    String body = message.content;
    long creation = message.creation.inMs();

    String transaction = String.format("Command: %s Uuid: %s Author: %s Conversation: %s Body: \"%s\" Creation: %d", command, uuid, author, convo, body, creation);

    transactionsList.put(transaction);
  }

  // collects users and establishes a following
  public void writeFollowUser(User userFollowing, User userToBeFollowed) throws InterruptedException {
    String command = "FOLLOWUSER";

    String userFollowingUuid = userFollowing.id.toString();
    String userToBeFollowedUuid = userToBeFollowed.id.toString();

    String transaction = String.format("Command: %s UserFollowingUuid: %s userToBeFollowedUuid: %s", command, userFollowingUuid, userToBeFollowedUuid);

    transactionsList.put(transaction);
  }

  // collects users and establishes an unfollowing
  public void writeUnfollowUser(User userUnfollowing, User userToBeUnfollowed) throws InterruptedException {
    String command = "UNFOLLOWUSER";

    String userUnfollowingUuid = userUnfollowing.id.toString();
    String userToBeUnfollowedUuid = userToBeUnfollowed.id.toString();

    String transaction = String.format("Command: %s UserUnfollowingUuid: %s userToBeUnfollowedUuid: %s", command, userUnfollowingUuid, userToBeUnfollowedUuid);

    transactionsList.put(transaction);
  }

  // collects users and conversations and establishes a following
  public void writeFollowConvo(Uuid user, Uuid convo) throws InterruptedException {
    String command = "FOLLOWCONVO";

    String userUuid = user.toString();
    String conversationUuid = convo.toString();

    String transaction = String.format("Command: %s User: %s Conversation: %s", command, userUuid, conversationUuid);

    transactionsList.put(transaction);
  }

    // collects users and conversations and establishes an unfollowing
  public void writeUnfollowConvo(Uuid user, Uuid convo) throws InterruptedException {
    String command = "UNFOLLOWCONVO";

    String userUuid = user.toString();
    String conversationUuid = convo.toString();

    String transaction = String.format("Command: %s User: %s Conversation: %s", command, userUuid, conversationUuid);

    transactionsList.put(transaction);
  }
}
