package com.example.core.sequence;

import com.example.core.tokens.TokenRegistry;

import java.util.ArrayList;
import java.util.List;

public class DSLParser {
    public static List<SequenceElement> parse(String input) throws ParseException {
        return parseSequence(input);
    }
    private static List<String> errors;

    private static List<SequenceElement> parseSequence(String s) throws ParseException {
        // Clear out any previous errors
        errors = new ArrayList<>();
        List<SequenceElement> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        int depth = 0;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            // escape using \, primarily intended for built-in DSL symbols
            if (c == '\\' && i + 1 < s.length()) {
                cur.append(s.charAt(++i));
            }
            // track grouping depth
            else if (c == '[' || c == '{' || c == '(') {
                depth++;
                cur.append(c);
            } else if (c == ']' || c == '}' || c == ')') {
                cur.append(c);
                depth = Math.max(0, depth - 1);
            }
            // '>' at top level as alias
            else if (c == '>' && depth == 0) {
                flushPart(cur, out);
                out.add(new PauseKeyInterval(0, 0));
            }
            // whitespace at top level splits tokens
            else if (Character.isWhitespace(c) && depth == 0) {
                flushPart(cur, out);
            }
            else {
                cur.append(c);
            }
        }
        flushPart(cur, out);
        if(!errors.isEmpty()) {
            throw new ParseException(errors);
        }
        return out;
    }

    private static void flushPart(StringBuilder cur, List<SequenceElement> out) {
        String part = cur.toString().trim();
        cur.setLength(0);
        if (part.isEmpty()) return;
        out.add(parseElement(part));
    }

    private static SequenceElement parseElement(String part) {
        if (part.startsWith("[") && part.endsWith("]")) {
            return parsePauseTimeInterval(part);
        } else if (part.startsWith("{") && part.endsWith("}")) {
            return parsePauseKeyInterval(part);
        } else {
            return parseKeyAction(part);
        }
    }

    private static PauseTimeInterval parsePauseTimeInterval(String token) {
        String inner = token.substring(1, token.length() - 1).trim();
        String[] nums = inner.split(",", 2);
        int min = nums.length == 1 ? 0 : Integer.parseInt(nums[0].trim());
        int max = Integer.parseInt(nums[nums.length - 1].trim());
        if(max < min || min < 0) {
            errors.add("Invalid time interval: " + min + ", " + max);
        }
        return new PauseTimeInterval(min, max);
    }

    private static PauseKeyInterval parsePauseKeyInterval(String token) {
        String inner = token.substring(1, token.length() - 1).trim();
        String[] nums = inner.split(",", 2);
        int min = nums.length == 1 ? 0 : Integer.parseInt(nums[0].trim());
        int max = Integer.parseInt(nums[nums.length - 1].trim());
        if(max < min || min < 0) {
            errors.add("Invalid key interval: " + min + ", " + max);
        }
        return new PauseKeyInterval(min, max);
    }

    private static SequenceElement parseKeyAction(String token) {
        List<String> parts = splitUnescaped(token, '+');
        List<String> mods = parts.subList(0, parts.size() - 1);

        int m = 0;
        for(String mod : mods) {
            Integer x = TokenRegistry.MODIFIER_MAP.get(mod);
            if(x == null) {
                errors.add("Modifier " + mod + "doesn't exist");
            } else {
                m |= x;
            }
        }
        String key = parts.getLast();
        Integer keyCode = TokenRegistry.KEY_MAP.get(key);
        if(keyCode == null) {
            errors.add("Token " + key + " doesn't exist");
        }
        if(!errors.isEmpty()) {
            return null;
        }
        char c = key.charAt(key.length()-1);
        if(c == '*') {
            return new KeyPressAction(m, keyCode);
        } else if(c == '^') {
            return new KeyReleaseAction(m, keyCode);
        } else {
            // No need to pass in the key code here
            return new KeyTypedAction(c);
        }
    }

    private static List<String> splitUnescaped(String s, char sep) {
        List<String> parts = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                cur.append(s.charAt(++i));
            } else if (c == sep) {
                parts.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        parts.add(cur.toString());
        return parts;
    }
}
