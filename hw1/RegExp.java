import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
//ด๚ธี
public class RegExp {

    static public char q1(String str0){
        int length = str0.length();
        if(length % 2 == 1){
            for(int i = 0;i < length / 2;i++){
                if(str0.charAt(i) != str0.charAt(length - 1 - i)) return 'N';
            }
        }else{
            for(int i = 0;i < length / 2;i++){
                if(str0.charAt(i) != str0.charAt(length - i - 1)) return 'N';
            }
        }
        return 'Y';
    }

    static public char q23(String str0,String str1, int num){
        int str0len = str0.length(), str1len = str1.length();
        for(int i = 0; i < str0len - str1len + 1;i++){
            if(str0.charAt(i) == str1.charAt(0)){
                if(str1len == 1) num--;
                int index = i + 1;
                for(int j = 1; j < str1len; j++){
                    if(str0.charAt(index) == str1.charAt(j)) index++;
                    else break;
                    if(j == str1len - 1) num--;
                }
                if(num == 0) return 'Y';
            }
        }
        return 'N';
    }

    static public char q4(String str0){
        int length = str0.length(), searchA = 0;
        for(int i = 0;i < length-1;i++)
        {
            if(str0.charAt(i) == 'A') searchA = 1;
            if(searchA == 1) if(str0.charAt(i) == 'B' && str0.charAt(i+1) == 'B') return 'Y';
        }
        return 'N';
    }

    public static void main(String[] args) {
        String str1 = args[1];
        String str2 = args[2];
        int s2Count = Integer.parseInt(args[3]);

        try {
            BufferedReader reader = new BufferedReader(new FileReader(args[0]));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.toUpperCase();
                System.out.println(q1(line)+","+q23(line,str1.toUpperCase(),1)+","+q23(line,str2.toUpperCase(),s2Count)+","+q4(line));
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
