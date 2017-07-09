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

  public TransactionLog(Controller controller, String fileName) {
    this.controller = controller;
    this.fileName = fileName;
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
        }
      }
    }
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

  public void writeCreateMessage(Message message, Uuid conversation) throws InterruptedException{
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
  public void writeFollowUser(User user1, User user2) throws InterruptedException{
    // TODO: implement thiss
  }

  // collects users and establishes a following
  public void writeFollowConvo(User user1, ConversationHeader convo) throws InterruptedException{
    // TODO: implement this
  }

}
