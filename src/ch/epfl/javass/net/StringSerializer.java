package ch.epfl.javass.net;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Class which contains methods to serialize information represented as Strings
 * @author remi
 */
public final class StringSerializer {

    //Constant representing base 16
    private static final int HEX_BASE = 16;

    /**
     * Private constructor of the class
     */
    private StringSerializer() {}

    /**
     * Method to serialize an integer into a string
     * @param value (int): the integer to serialize
     * @return (String): the integer serialized into a string
     */
    public static String serializeInt(int value) {
        return Integer.toUnsignedString(value, HEX_BASE);
    }

    /**
     * Method to deserialize a string to get back its integer value
     * @param s (String): the string we want to deserialize
     * @return (int): the integer value
     */
    public static int deserializeInt(String s) {
        return Integer.parseUnsignedInt(s, HEX_BASE);
    }

    /**
     * Method to serialize a long into a string
     * @param value (long): the long to serialize
     * @return (String): the long serialized into a string
     */
    public static String serializeLong(long value) {
        return Long.toUnsignedString(value, HEX_BASE);
    }

    /**
     * Method to deserialize a string to get back its long value
     * @param s (String): the string we want to deserialize
     * @return (long): the long value
     */
    public static long deserializeLong(String s) {
        return Long.parseUnsignedLong(s, HEX_BASE);
    }

    /**
     * Method to serialize a string from its UTF-8 encoding to Base 64
     * @param s (String): the string to serialize
     * @return (String): the string serialized in UTF-8 encoding
     */
    public static String serializeString(String s) {

        byte[] byteArray = s.getBytes(StandardCharsets.UTF_8);
        String serializedString = Base64.getEncoder().encodeToString(byteArray);
        return serializedString;
    }

    /**
     * Method to deserialize a string from Base 64 to UTF-8 encoding
     * @param s (String):  the string we want to deserialize
     * @return (String): the deserialized string
     */
    public static String deserializeString(String s) {
        byte[] byteArray = Base64.getDecoder().decode(s);
        String deserializedString = new String(byteArray, StandardCharsets.UTF_8);
        return deserializedString;
    }

    /**
     * Combines multiple strings separated by a delimiter into a single string
     * @param delimiter (String) : the delimiter of the strings
     * @param strings (String...): the strings we want to combine
     * @return (String) : the string resulting from the combination
     */
    public static String combine(String delimiter, String...strings) {
        for(String s : strings) {
            if(s.contains(delimiter)) { throw new IllegalArgumentException(); }
        }
        String fullString = String.join(delimiter, strings);
        return fullString;
    }

    /**
     * Splits a string into multiple strings separated by a delimiter
     * @param string (String): the string to split
     * @param delimiter (String): the delimiter
     * @return (String[]): the array containing all the split strings
     */
    public static String[] split(String string, String delimiter) {
        String[] stringArray = string.split(delimiter);
        return stringArray;
    }
}
