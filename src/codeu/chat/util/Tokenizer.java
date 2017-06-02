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
//
public final class Tokenizer {
  private LinkedList<String> tokens = new LinkedList<String>();


  // Constructor does most of the work. It parses through the text and adds tokens
  // to the running tokens list for later access. Currently requires quoted material
  // to be properly formatted escaped using a '\' character before the escaped
  // characters forward slash and a quotation mark for a lack of ambiguity.
  public Tokenizer(String line) {
    StringBuilder token = new StringBuilder();
    boolean inQuotes = false;
    boolean lookingForEscapable = false; //

    for (int i = 0; i < line.length(); i++) {
      char c = line.charAt(i);

      if (lookingForEscapable) {
        if (isEscapableChar(c)) {
          token.append(c);
          lookingForEscapable = false;
        }
        else {
          throw new IllegalArgumentException
          ("Forward slash found before unescapable character.");
        }
      }
      else {
        if (c == '\\' && inQuotes)
          lookingForEscapable = true;
        else if (c == '"') {
          // if at the start of a word, the rest of the material will be in quotes
          if (!inQuotes && emptyToken(token)) {
            inQuotes = true;
          }
          else {
            inQuotes = false;
          }
        }
        else if (Character.isWhitespace(c) && !inQuotes) {
          // white space denotes the ending of tokens, meaning the token should
          // be added to memory
          addToken(token);
          clearToken(token);
        }
        else {
          token.append(c);
        }
      }
    }

    addToken(token);
    token = null;
  }

  private boolean isEscapableChar(char c) {
    return (c == '\\' || c == '"');
  }

  // checks to see if is any content in the token
  private boolean emptyToken(StringBuilder token) {
    return (token.length() == 0);
  }

  // adds token to the tokens list for later access
  private void addToken(StringBuilder token) {
    if (!emptyToken(token))
      tokens.add(token.toString());
  }

  // takes the StringBuilder and empties it for efficient reuse
  private void clearToken(StringBuilder token) {
    token.setLength(0);
  }

  public boolean hasNext() {
    return !tokens.isEmpty();
  }

  public String next() {
    return tokens.removeFirst();
  }
}
