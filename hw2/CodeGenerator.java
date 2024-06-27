import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class CodeGenerator {
    //delete line is used 
    public static int cutLine(String mermaidCode){
        int len = mermaidCode.length(), findN = 0;
        for(int i = 0;i < len;i++){
            if(mermaidCode.charAt(i) != ' ' && findN == 1) return i;
            if(mermaidCode.charAt(i) == '\n') findN = 1;
        }
        return len;
    }

    //make sure whether first 5 word is "class"
    public static boolean isClass(String mermaidCode, String ensure){
        int len = mermaidCode.length(),index = 0;
        for(int i = 0;i < len;i++)
        {
            if(mermaidCode.charAt(i) == ' ') continue;
            if(mermaidCode.charAt(i) != ensure.charAt(index)) return false;
            if(index == 4) return true;
            index++;
        }
        return false;
    }
    
    //look for class name
    public static String isClassName(String mermaidString){
        String classname ="";
        int len = mermaidString.length();
        int i = 0;
        //erase the ' ' of front of "class"
        mermaidString.trim();

        //because first 5 char are "class", so we need ignore first 5 char
        i +=5;
        for(;i < len;i++){ 
            if(mermaidString.charAt(i) == ' ') continue;
            if(mermaidString.charAt(i) < 'A') break;
            classname += mermaidString.charAt(i);
        }
        return classname;
    }
    
    //creat a java
    public static void creatJava(String output,String content){
        try {
            File file = new File(output);
            if (!file.exists()) {
                file.createNewFile();
            }
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                bw.write(content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static int findPN(type className,String lineCode){
        if(lineCode.indexOf('+') != -1){
            className.stringStore("public ");
            return lineCode.indexOf('+')+1;
        }else if(lineCode.indexOf('-') != -1){
            className.stringStore("private ");
            return lineCode.indexOf('-')+1;
        }
        return -1;
    }
    
    public static boolean isFunction(String lineCode,type className){
        if(lineCode.indexOf(')') != -1) return true;

        int len = lineCode.length(),index = 0;
        for(;index < len;index++){
            if(lineCode.charAt(index) < ' ') break; 
        }
        className.stringStore(lineCode.substring(0,index)+";\n");
        return false;
    }
    
    public static String FunctionType(String lineCode,type className){
        int pos = lineCode.indexOf(')');
        pos++;
        lineCode = lineCode.substring(pos);
        lineCode = lineCode.trim();
        className.stringStore(lineCode+" ");

        //return type
        return lineCode;
    }
    
    public static void getFunction(String lineCode,type className,space classSpace){
        //next line
        className.stringStore("\n");
        
        //search position of '('
        int pos = lineCode.indexOf('(');
        
        //search return name
        lineCode = lineCode.substring(3,pos);
        lineCode = lineCode.toLowerCase();
        
        //output "return lineCode;"
        className.stringStore(classSpace.outputSpace()+"return "+lineCode+";\n");
        classSpace.storeSpace('}');
        className.stringStore(classSpace.outputSpace()+"}\n");
    }

    public static void setFunction(String lineCode,type className,space classSpace){
        //next line
        className.stringStore("\n");
        
        //search position of '('
        int pos = lineCode.indexOf('(');
        int len = lineCode.length();

        //thisline = this.thisline
        //search return name
        String thisline = lineCode.substring(3,pos);
        thisline = thisline.toLowerCase();

        //variableName -> this.thislint = variableName
        String variableName = "";
        for(int i = pos;i <len;i++){
            if(lineCode.charAt(i) == ' '){
                pos = lineCode.indexOf(')');
                variableName = lineCode.substring(i+1, pos);
                break;
            }
        }

        className.stringStore(classSpace.outputSpace()+"this."+thisline+" = "+variableName+";\n");
        classSpace.storeSpace('}');
        className.stringStore(classSpace.outputSpace()+"}\n");
    }
    
    public static void voidFunction(type className,space classSpace,String functionType){
        if(functionType.equals("String")) className.stringStore("return \"\";}\n");
        else if(functionType.equals("int")) className.stringStore("return 0;}\n");
        else if(functionType.equals("boolean")) className.stringStore("return false;}\n");
        else className.stringStore(";}\n");
        classSpace.storeSpace('}');
    }
    
    public static void crearFunction(String lineCode,type className,space classSpace){
        //output Function type
        String functionType = FunctionType(lineCode,className);

        //output Fuction name
        int pos = lineCode.indexOf(')'); //search position of ')'
        className.stringStore(lineCode.substring(0,pos+1)+" {");
        classSpace.storeSpace('{');
        
        //determin whether the first 3 word are get or the first 3 words are set
        String get = "get",set = "set";
        if(get.equals(lineCode.substring(0,3))) getFunction(lineCode, className, classSpace);
        else if(set.equals(lineCode.substring(0,3)))setFunction(lineCode, className, classSpace);
        else voidFunction(className,classSpace,functionType);
    }
    public static void main(String[] args) {    
		    // 讀取文件
        if (args.length == 0) {
            System.err.println("請輸入檔案名稱");
            return;
        }
        String fileName = args[0];
        String mermaidCode = "";
        try {
            mermaidCode = Files.readString(Paths.get(fileName));
        }
        catch (IOException e) {
            System.err.println("無法讀取文件 " + fileName);
            e.printStackTrace();
            return;
        }
        
        //main
        int classNum = 0;
        type className = new type();
        space classSpace = new space();
        String lineCode = "";
        //ignore "classDiagram"
        while((mermaidCode = mermaidCode.substring(cutLine(mermaidCode))) !=""){
            
            lineCode = mermaidCode.substring(0,cutLine(mermaidCode));
            //if this line first String is class, creat a class type to record
            if(isClass(lineCode, "class")){ 
                if(classNum != 0){  
                    (className.name).trim();
                    className.stringStore("}");
                    creatJava(className.name+".java", className.re);
                }
                className = new type();
                classSpace = new space();
                classNum++;
                //output "Public class className.name { \n"
                className.nameType(isClassName(lineCode));
                className.stringStore("public class "+className.name+" {"+'\n');
                classSpace.storeSpace('{');
                //next line
                mermaidCode = mermaidCode.substring(cutLine(mermaidCode));
                lineCode = mermaidCode.substring(0,cutLine(mermaidCode));
            }

            //if lineCode is space, ignore this lineCode
            lineCode = lineCode.trim();
            if(lineCode == "") continue;

            //we have searched the class name 
            //indent
            className.stringStore(classSpace.outputSpace());

            int PNpos = findPN(className, lineCode);
            if(PNpos != -1) lineCode = lineCode.substring(PNpos);// find public or private and remove words of front of '+' or '-'
            //Use ')' to determine whether the lineCode is a function
            if(isFunction(lineCode,className)) crearFunction(lineCode,className,classSpace);

        }
        //file has been readed
        className.stringStore("}");
        creatJava(className.name+".java", className.re);
        
    }
}


class type{
    String name ="";
    String re = "";

    void nameType(String name){
        this.name += name;
    }
    
    void stringStore(String re){
        this.re += re;
    }
    
}

class space{
    String spaceNum="";
    void storeSpace(char bracket){
        if(bracket == '{') spaceNum += "    ";
        if(bracket == '}') spaceNum = spaceNum.substring(4);
    }

    String outputSpace(){
        return spaceNum;
    }
}