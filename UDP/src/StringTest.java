import java.util.Scanner;

public class StringTest {


    //将16进制的字符串转成字符数组
    public static byte[] getHexBytes(String str){
        byte[] bytes = new byte[str.length() / 2];
        for(int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }
        return bytes;
    }


    public static void main(String[] args) {
        String s= "0001CFE6";
        System.out.println(getHexBytes(s));
    }
}
