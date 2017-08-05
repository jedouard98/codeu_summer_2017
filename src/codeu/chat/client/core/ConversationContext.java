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

package com.google.codeu.mathlang.impl;

import java.io.IOException;

import com.google.codeu.mathlang.core.tokens.Token;
import com.google.codeu.mathlang.parsing.TokenReader;

import com.google.codeu.mathlang.core.tokens.NameToken;
import com.google.codeu.mathlang.core.tokens.NumberToken;
import com.google.codeu.mathlang.core.tokens.StringToken;
import com.google.codeu.mathlang.core.tokens.SymbolToken;

import java.util.HashSet;

// MY TOKEN READER
//
// This is YOUR implementation of the token reader interface. To know how
// it should work, read src/com/google/codeu/mathlang/parsing/TokenReader.java.
// You should not need to change any other files to get your token reader to
// work with the test of the system.
public final class MyTokenReader implements TokenReader {
  public String source;

  private int cursorPosition;
  private boolean iteratingThroughNote;
  private boolean noteFlag;
  private StringBuilder sb;

  public MyTokenReader(String source) {
    // Your token reader will only be given a string for input. The string will
    // contain the whole source (0 or more lines).

    this.source = new String(source);
    this.cursorPosition = 0;
    this.sb = new StringBuilder();
  }

  @Override
  public Token next() throws IOException {
    // Most of your work will take place here. For every call to |next| you should
    // return a token until you reach the end. When there are no more tokens, you
    // should return |null| to signal the end of input.

    // If for any reason you detect an error in the input, you may throw an IOException
    // which will stop all execution.
    sb.setLength(0);
    for (int i = cursorPosition; i < source.length(); i++) {
      char c = source.charAt(i);
      if (c == '\"') {
        iteratingThroughNote = true;
      }
      else if (endOfTokenCondition(c)) {
        cursorPosition = i;
        interpret(sb.toString());

      }
      else if (!(Character.isWhitespace(c)))
        sb.append(source.charAt(c));
    }

    return null;
  }

  private Token interpretToken(String token) {
    if (isSymbol(token))
      return SymbolToken(token.charAt(0));
    if (Character.isDigit(token))
      return NumberToken(Double.parseDouble(token));
    if (iteratingThroughNote) {
      iteratingThroughNote = false;
      return StringToken(token);
    }
    else {
      return NameToken(token);
    }
  }
  private boolean isSymbol(char c) {
    return (c == '+' || c == '-');
  }

  private boolean endOfTokenCondition(char c) {
    if (iteratingThroughNote && c != '\"') return false;
    return ((Character.isWhitespace(c) && sb.length() > 0) || (c == ';'));
  }
}
