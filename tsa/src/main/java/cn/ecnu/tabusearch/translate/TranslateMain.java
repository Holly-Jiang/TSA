package cn.ecnu.tabusearch.translate;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TranslateMain {
        public static final Integer N=999999;
        public static List<String> getFileList(String path) {
            List<String> list = new ArrayList();
            try {
                System.out.println(path);
                File file = new File(path);
                String[] filelist = file.list();
                for (int i = 0; i < filelist.length; i++) {
                    list.add(path+filelist[i]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return list;
        }
        public static void translate(){
            /**
             * /home/test/qubitmapping/
             * E:\github\Tabu_win\tabu\src\main\resources\data\
             */
            String inPath = "../../../src/main/resources/data/";//遍历文件夹下的所有.jpg文件
            List<String> files=  getFileList(inPath);
            for (int i=0;i<files.size();i++){
                System.out.println(i+" : "+files.get(i));
            }
            System.out.println("------------------------------------------");
            for (int i=0;i<files.size();i++){
                if (files.get(i)==" "){
                    return;
                }
//            File file = new File("E:\\github\\Tabu_win\\tabu\\src\\main\\resources\\resources/data\\0example.qasm");
                File file = new File(files.get(i));
                BufferedReader reader = null;
                Level[] tower=new Level[N];
                for (int k=0;k<N;k++){
                    tower[k]=new Level(k);
                }
                try {
                    reader = new BufferedReader(new FileReader(file));
                    Translate trans=new Translate();
                    System.out.println(i+" : "+files.get(i));
                    int total_level = trans.translate(tower,reader);
                    System.out.println("\n重新打印代码:\n");
                    StringBuffer sb=new StringBuffer();
                    sb.append("/home/jh/github/TSA/tsa/src/main/resources/examples_result/");
                    String []splits=files.get(i).split("/");

                    sb.append(splits[splits.length-1]);

                    FileWriter fw = new FileWriter(sb.toString(), false);
                    PrintWriter pw = new PrintWriter(fw);
                    System.out.println(total_level);
                    for (int j = 1, line = 1; j <= total_level; j++) {
                        Instruction p = tower[j].head.getNext();
                        while (p!=null) {
                            // System.out.println(line+" : ");
                            line++;
                            p.print(pw);
                            //System.out.println("\n");
                            p = p.getNext();
                        }
                    }
                    pw.close();
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
            return ;
        }
        public static void main(String[] args) {
          translate();
    }
}

