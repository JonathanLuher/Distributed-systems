import java.io.*;
import java.util.Arrays;

class Demo implements java.io.Serializable
{
	public int a;
	public String b;

	// Default constructor
	public Demo(int a, String b)
	{
		this.a = a;
		this.b = b;
	}
}

class Test
{
	public static void main(String[] args)
	{
		Demo object = new Demo(2024, "Prueba serializacion y deserializacion");
		Demo objeto = null;
		
		byte[] serializado = SerializationUtils.serialize(object);
		System.out.println("Objeto serializado byte por byte en formato Hexadecimal:");
     	System.out.println(bytesToHex(serializado));
     	System.out.println("\nObjeto deserializado:");
		objeto = (Demo)SerializationUtils.deserialize(serializado);
		System.out.println("a = " + objeto.a);
		System.out.println("b = " + objeto.b);
	}

	private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }
}
