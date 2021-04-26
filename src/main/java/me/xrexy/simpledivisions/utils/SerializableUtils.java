package me.xrexy.simpledivisions.utils;

import java.io.*;
import java.util.Base64;

public class SerializableUtils {

    /**
     * Serializes object to Base64 string
     *
     * @param o Object to serialize
     * @return Object as Base64 string
     * @throws IOException If an I/O error occurs while reading stream header
     */
    public static String toString(Serializable o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    /**
     * Deserializes object from Byte64 string
     *
     * @param s String to deserialize from
     * @return Deserialized object from string
     * @throws IOException            If an I/O error occurs while reading stream header
     * @throws ClassNotFoundException Class of a serialized object cannot be found.
     */
    public static Object fromString(String s) throws ClassNotFoundException, IOException {
        byte[] data = Base64.getDecoder().decode(s);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        Object o = ois.readObject();
        ois.close();
        return o;
    }
    // i hate my life
}
