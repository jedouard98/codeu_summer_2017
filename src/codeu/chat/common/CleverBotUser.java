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

import com.michaelwflaherty.cleverbotapi.CleverBotQuery;
import com.sethsutopia.utopiai.cleverbot.Cleverbot;
import com.sethsutopia.utopiai.cleverbot.CleverResponse;

import codeu.chat.util.Uuid;
import codeu.chat.util.Time;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import codeu.chat.util.Serializer;
import codeu.chat.util.Serializers;



public final class CleverBotUser extends User {
  public static final String API_KEY = "CC3kqdDZtESaa2JoIvSVhBIELxw";

  public static final Serializer<CleverBotUser> SERIALIZER = new Serializer<CleverBotUser>() {

    @Override
    public void write(OutputStream out, CleverBotUser value) throws IOException {

      Uuid.SERIALIZER.write(out, value.id);
      Serializers.STRING.write(out, value.name);
      Time.SERIALIZER.write(out, value.creation);

    }

    @Override
    public CleverBotUser read(InputStream in) throws IOException {

      return new CleverBotUser(
          Uuid.SERIALIZER.read(in),
          Serializers.STRING.read(in),
          Time.SERIALIZER.read(in)
      );

    }
  };

  public CleverBotUser(Uuid id, String name, Time creation) {
    super(id, name, creation);
  }

  public String response(String input) {
    String userResponse = (input == null) ? "" : input;

    Cleverbot test = new Cleverbot(API_KEY);

    String response;
    try {
      CleverResponse cr = test.say("example", userResponse);
      response = cr.getOutput();
    }
    catch (Exception e) {
      response = "I didn't understand that!";
    }
    return response;
  }
}
