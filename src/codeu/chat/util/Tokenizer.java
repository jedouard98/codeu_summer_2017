package codeu.chat.util;

import java.util.LinkedList;

// TOKENIZER
//
// This is the class that is used to take in input from the Chat's commandline
// and correctly tokenize input into sections. Main function is to allow
// input that has quoted material be regarded as a single token, and to allow
// nested quotes to be escaped inside the quotation marks if that's what the client
// wishes. It's interface somewhat mimicks the Scanner class's (of the java
// util library) to allow the Chat class's easy transition from that class
// to this one.
public final class Tokenizer {
  private LinkedList<String> tokens = new LinkedList<String>();

  public Tokenizer(String line) {
    StringBuilder token = new StringBuilder();
    boolean inQuotes = false;
    boolean lookingForEscapable = false;

    for (int i = 0; i < line.length(); i++) {
      char c = line.charAt(i);
      if (lookingForEscapable) {
        if (c == '\\' || c == '"') {
          token.append(c);
          lookingForEscapable = false;
        }
        else {
          throw new IllegalArgumentException("Backslash found before unescapable character. '\' can only be found before backslash and quote");
        }
      }
      else {
        if (c == '\\')
          lookingForEscapable = true;
        else if (c == '"') {
          if (token.length == 0) {
            inQuotes = true;
          }
          else {
            if (inQuotes) {
              if ((i + 1) < line.length() && !Character.isWhitespace(line.charAt(i+1)))
                throw new IllegalArgumentException("Character found right after quotation marks. Individual input sections unclear");
              tokens.add(token.toString());
              token.setLength(0);
              inQuotes = false;
            }
          }
        }
        else if (Character.isWhitespace(c) && !inQuotes) {
          if (token.length() != 0) {
            tokens.add(token.toString());
            token.setLength(0);
          }
        }
        else {
          token.append(c);
          lookingForEscapable = false;
        }
      }
    }

    tokens.add(token.toString());
    token = null;
  }

  public boolean hasNext() {
    return !tokens.isEmpty();
  }

  public String next() {
    return tokens.removeFirst();
  }

}
