package cn.ecnu.tabusearch.test;


import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

class TreeNode {
      int val;
      TreeNode left;
      TreeNode right;
      TreeNode() {}
      TreeNode(int val) { this.val = val; }
      TreeNode(int val, TreeNode left, TreeNode right) {
          this.val = val;
 this.left = left;
         this.right = right;
     }
 }
public class FileTest {

    public static void main(String[] args) throws IOException {
//        method1("E:\\github\\quantum_compiler_optim\\compare\\total","fff");
//        FileWriter of2 = new FileWriter("E:\\github\\quantum_compiler_optim\\compare\\total",true);
//        of2.write("PPPP");
//        of2.append("\n");
//        of2.append("999999999");
//        of2.append("999999999");
//        of2.append("999999999");
//        of2.append("999999999");
//        of2.append("999999999");
//        of2.write("pppp");
//        of2.flush();
//        of2.close();
//        BigInteger bigInteger1=new BigInteger("2");
//        BigInteger bigInteger2=new BigInteger("3");
//        bigInteger1.multiply(bigInteger2);
//        System.out.println(bigInteger1.multiply(bigInteger2));
        System.out.println(integerBreak(8));
    }
    public static int integerBreak(int n) {
        int muilt=0,temp=n,a=0,b=0,c=n,last=1;
        List<Integer>list=new ArrayList<>();
        list.add(n);
        while(muilt<last){
            int min=0;
            muilt=last;
            for(int i=0;i<list.size();i++){
                if(list.get(i)>list.get(min)){
                    min=i;
                }
            }
            c=list.get(min);
            if(c<=1){
                return last;
            }
            a=c/2+1;
            b=(c-a);
            temp=a*b*(temp/c);
            if (temp>last){
                last=temp;
            }else{
                return last;
            }
            list.remove(min);
            list.add(a);
            list.add(b);
        }
        return last;
    }

        /**
         * 追加文件：使用FileOutputStream，在构造FileOutputStream时，把第二个参数设为true
         *
         * @param fileName
         * @param content
         */
        public   static   void  method1(String file, String conent) {
            BufferedWriter out = null ;
            try  {
                StringBuilder s=new StringBuilder();
                out = new  BufferedWriter( new  OutputStreamWriter(
                        new  FileOutputStream(file,  true )));
                out.write(conent);
            } catch  (Exception e) {
                e.printStackTrace();
            } finally  {
                try  {
                    out.close();
                } catch  (IOException e) {
                    e.printStackTrace();
                }
            }
        }

}
