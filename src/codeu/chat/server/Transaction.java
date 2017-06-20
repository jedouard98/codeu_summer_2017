package codeu.chat.server;

import codeu.chat.common.User;
import codeu.chat.common.ConversationHeader;
import codeu.chat.common.Message;
import codeu.chat.util.Uuid;
import codeu.chat.util.Tokenizer;
import codeu.chat.util.Time;
import codeu.chat.server.Controller;


import java.util.ArrayList;
import java.util.Scanner;

import java.io.FileWriter;
import java.io.File;

import java.io.IOException;


public final class Transaction {
  private ArrayList<String> transactionsList = new ArrayList<String>();
  private Controller controller;

  public Transaction(Controller controller) {
    this.controller = controller;
    read();
  }

  // takes in information that the server needs to populate itself!
  private void read() {
    File transaction = new File("transaction.txt");
    try {
      Scanner in = new Scanner(transaction);
      while (in.hasNextLine()) {
        String line = in.nextLine();
        System.out.println(line);
        Tokenizer tokens = new Tokenizer(line);
        tokens.next();
        switch (tokens.next()) {
          case "CREATEUSER" : readUser(tokens); break;
          case "CREATEMESSAGE" : readMessage(tokens); break;
          case "CREATECONVO" : readConvo(tokens); break;
        }
      }
    }
    catch (IOException e) {
    }
  }

  private void readUser(Tokenizer tokens) {
    try {
      tokens.next();
      Uuid id = Uuid.parse(tokens.next());
      tokens.next();
      String name = tokens.next();
      tokens.next();
      Time creation = Time.fromMs(new Long(tokens.next()));
      controller.newUser(id, name, creation);
    }
    catch (IOException e) {

    }
  }

  private void readMessage(Tokenizer tokens) {
    try {
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
    catch (IOException e) {
    }
  }

  private void readConvo(Tokenizer tokens) {
    try {
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
    catch (IOException e) {

    }
  }

  // takes all items and flushes it to disc format!
  public void flush() {
    try{
      FileWriter writer = new FileWriter("transaction.txt", true);
      for (String transaction : transactionsList)
        writer.write(transaction + "\n");
      writer.flush();
    } catch (IOException e) {
        //
    }
    this.transactionsList = new ArrayList<String>();
  }

  // takes information in and stores it!
  public void write(User user) {
    String command = "CREATEUSER";

    String uuid = user.id.toString();
    String name = user.name;
    long creation = user.creation.inMs();

    String transaction = String.format("Command: %s Uuid: %s Name: \"%s\" Creation: %d", command, uuid, name, creation);
    transactionsList.add(transaction);
  }

  public void write(ConversationHeader conversation) {
    String command = "CREATECONVO";

    String uuid = conversation.id.toString();
    String owner = conversation.owner.toString();
    long creation = conversation.creation.inMs();
    String title = conversation.title;

    String transaction = String.format("Command: %s Uuid: %s Owner: %s Creation: %d Title: \"%s\"", command, uuid, owner, creation, title);
    transactionsList.add(transaction);
  }

  public void write(Message message, Uuid conversation) {
    String command = "CREATEMESSAGE";

    String uuid = message.id.toString();
    String author = message.author.toString();
    String convo = conversation.toString();
    String body = message.content;
    long creation = message.creation.inMs();

    String transaction = String.format("Command: %s Uuid: %s Author: %s Conversation: %s Body: \"%s\" Creation: %d", command, uuid, author, convo, body, creation);
    transactionsList.add(transaction);
  }

  // collects users and establishes a following
  public void write(User user1, User user2) {
    // TODO: implement thiss
  }

  // collects users and establishes a following
  public void write(User user1, ConversationHeader convo) {
    // TODO: implement this
  }

}
