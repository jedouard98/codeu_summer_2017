package codeu.chat.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import codeu.chat.common.ConversationHeader;
import codeu.chat.common.ConversationPayload;
import codeu.chat.common.LinearUuidGenerator;
import codeu.chat.common.Message;
import codeu.chat.common.NetworkCode;
import codeu.chat.common.Relay;
import codeu.chat.common.Secret;
import codeu.chat.common.User;
import codeu.chat.util.Logger;
import codeu.chat.util.Serializers;
import codeu.chat.util.Time;
import codeu.chat.util.Timeline;
import codeu.chat.util.Uuid;
import codeu.chat.util.connections.Connection;
import codeu.chat.server.Server.Command;

public final class Transaction{

  private final Command command;
  private final InputStream in;
  private final OutputStream out;

  public Transaction(Command command, InputStream in, OutputStream out){
    this.command = command;
    this.in = in;
    this.out = out;
  }

  public void execute(){
    try {
      command.onMessage(in,out);
      /**
      * this will not do anything because the Exception will have already been
      * handled in previous excecution of command
      **/
    } catch (Exception ex) { }
  }
}
