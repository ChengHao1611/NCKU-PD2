import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Stack;

public class testCodeGenerator {
    //record the class in front of left brace
    public static Stack<String> nowClass = new Stack<String>();
    public static type className1 = new type();
    public static space classSpace1 = new space();
    public static type className2 = new type();
    public static space classSpace2 = new space();
    public static type className3 = new type();
    public static space classSpace3 = new space();

    public static void outputPublisgClass(String lineCode,type className,space classSpace){
        //output "Public class className.name { \n"
        className.stringStore("public class "+className.name+" {"+'\n');
        classSpace.storeSpace('{');

    }

    public static type outputClassName(String ClassName,String lineCode){
        if(className1.name == ""){
            className1.name = ClassName;
            outputPublisgClass(lineCode,className1,classSpace1);
            return className1;
        }
        else if(className2.name == ""){
            className2.name = ClassName;
            outputPublisgClass(lineCode,className2,classSpace2);
            return className2;
        } 
        else if(className3.name == ""){
            className3.name = ClassName;
            outputPublisgClass(lineCode,className3,classSpace3);
            return className3;
        }
        else return findClassName(ClassName);
    }

    public static space outputSpace(String ClassName){
        if(className1.name.equals(ClassName)) return classSpace1;
        else if(className2.name.equals(ClassName)) return classSpace2;
        else return classSpace3;
    }
        
    public static type findClassName(String className){
        if(className1.name.equals(className)) return className1;
        else if(className2.name.equals(className)) return className2;
        else return className3; 
    }

    public static space findClassSpace(String className){
        if(className1.name.equals(className)) return classSpace1;
        else if(className2.name.equals(className)) return classSpace2;
        else return classSpace3; 
    }
    
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
    public static boolean isClass(String lineCode, String ensure){
        int len = lineCode.length(),index = 0;
        for(int i = 0;i < len;i++)
        {
            if(lineCode.charAt(i) == ' ') continue;
            if(lineCode.charAt(i) != ensure.charAt(index)) return false;
            if(index == 4) return true;
            index++;
        }
        return false;
    }
    
    //look for class name
    public static String isClassName(String lineCode){
        String classname ="";
        int len = lineCode.length();
        int i = 0;
        //erase the ' ' of front of "class"
        lineCode.trim();

        //because first 5 char are "class", so we need ignore first 5 char
        i +=5;
        for(;i < len;i++){ 
            if(lineCode.charAt(i) == ' ') continue;
            if(lineCode.charAt(i) <'A' || lineCode.charAt(i) > 'z') break;
            classname += lineCode.charAt(i);
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
    
    public static String findClass(String lineCode){
        int len = lineCode.length();
        String re = "";
        lineCode = lineCode.trim();
        for(int i = 0;i < len;i++){
            if(lineCode.charAt(i) <65 || lineCode.charAt(i) > 122) break;
            re += lineCode.charAt(i);
        }
        return re;
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
    
    public static String splitString(String lineCode){
        String[] splitSpace = lineCode.split("\\s+");
        String reClassName = "";
        for(String x : splitSpace){
            reClassName += " "+x;
        }
        reClassName = reClassName.trim();
        return reClassName;
    }

    public static boolean isFunction(String lineCode,type className){
        if(lineCode.indexOf(')') != -1) return true;

        className.stringStore(splitString(lineCode)+";\n");
        return false;
    }
    
    public static String FunctionType(String lineCode,type className){
        int pos = lineCode.indexOf(')');
        pos++;
        lineCode = lineCode.substring(pos);
        lineCode = lineCode.trim();
        if(lineCode == "") lineCode = "void";
        className.stringStore(lineCode+" ");

        //return type
        return lineCode;
    }
    
    public static String eraseFunctionSpace(String lineCode){
        int len = lineCode.length(),index = 0;
        String re = "";
        for(;index < len;index++){
            if(lineCode.charAt(index) == ' ' || lineCode.charAt(index) == '('){
                re = lineCode.substring(0, index);
                break;
            }
        }
        lineCode = lineCode.substring(lineCode.indexOf('(')+1, lineCode.indexOf(')'));
        return re+"("+splitString(lineCode)+")";
    }
    
    public static void getFunction(String lineCode,type className,space classSpace){
        //next line
        className.stringStore("\n");
        
        //search position of '('
        int pos = lineCode.indexOf('(');
        
        //search return name
        lineCode = lineCode.substring(3,pos);
        String notChangeString = lineCode.substring(1);
        lineCode = lineCode.substring(0, 1);
        lineCode = lineCode.toLowerCase();
        lineCode += notChangeString;
        
        //output "return lineCode;"
        className.stringStore(classSpace.outputSpace()+"return "+lineCode+";\n");
        classSpace.storeSpace('}');
        className.stringStore(classSpace.outputSpace()+"}\n");
    }

    public static void setFunction(String lineCode,type className,space classSpace){
        //next line
        className.stringStore("\n");
        
        //search position of '('
        int posBrace = lineCode.indexOf('(');
        int posSpace = lineCode.indexOf(' ');
        int len = lineCode.length();

        //min(plsSpace, posBrace) ex. setName()  or setName  ()
        //thisline = this.thisline
        //search return name and make first word of name to be lower
        String thisline = lineCode.substring(3,Math.min(posBrace,posSpace));
        String notChangeString = thisline.substring(1);
        thisline = thisline.substring(0, 1);
        thisline = thisline.toLowerCase();
        thisline += notChangeString;

        //variableName -> this.thislint = variableName
        lineCode = lineCode.substring(lineCode.indexOf('(')+1);
        String variableName = "";
        String[] lineCodeArgument = lineCode.split("\\s+");
        int time = 0;
        for(String x : lineCodeArgument){
            if(x.equals("")) continue;
            else{
                time++;
                if(time == 2){
                    variableName = x;
                    if(variableName.contains(")")){
                        variableName = variableName.substring(0,variableName.indexOf(')'));
                    }
                    break;
                }
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
    
    public static void creatFunction(String lineCode,type className,space classSpace){
        //output Function type
        String functionType = FunctionType(lineCode,className);

        //output Fuction name
        className.stringStore(eraseFunctionSpace(lineCode)+" {");
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
        type className = null;
        space classSpace = null;
        String lineCode = "";
        //ignore "classDiagram"
        while((mermaidCode = mermaidCode.substring(cutLine(mermaidCode))) !=""){
            
            lineCode = mermaidCode.substring(0,cutLine(mermaidCode));
            //if this line first String is class, creat a class type to record
            if(isClass(lineCode, "class")){ 
                String ClassName = isClassName(lineCode);

                //output ClassName to type.name
                className = outputClassName(ClassName,lineCode);
                classSpace = outputSpace(ClassName);
                if(lineCode.indexOf('{') != -1) nowClass.push(className.name);
                continue;
            }

            //if lineCode is space, ignore this lineCode
            lineCode = lineCode.trim();
            if(lineCode == "") continue;
                       
            if(lineCode.charAt(0) == '}'){
                nowClass.pop();
                continue;
            }

            if(nowClass.empty()){
                String classtype = findClass(lineCode);
                className = findClassName(classtype);
                classSpace = findClassSpace(classtype);
            }
            else{
                className = findClassName(nowClass.peek());
                classSpace = findClassSpace(nowClass.peek());
            }

            //we have searched the class name 
            //indent
            className.stringStore(classSpace.outputSpace());
            int PNpos = findPN(className, lineCode);
            if(PNpos != -1) lineCode = lineCode.substring(PNpos);// find public or private and remove words of front of '+' or '-'
            //Use ')' to determine whether the lineCode is a function
            
            //erase space in back of - or +;
            lineCode = lineCode.trim();
            if(isFunction(lineCode,className)) creatFunction(lineCode,className,classSpace);

        }
        //file has been readed
        if(className1.name != "")
        {
            className1.stringStore("}");
            creatJava(className1.name+".java", className1.re);
        }
        if(className2.name != "")
        {
            className2.stringStore("}");
            creatJava(className2.name+".java", className2.re);
        }
        if(className3.name != "")
        {
            className3.stringStore("}");
            creatJava(className3.name+".java", className3.re);
        }
        
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