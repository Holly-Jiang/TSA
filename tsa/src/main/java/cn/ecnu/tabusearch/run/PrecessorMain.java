package cn.ecnu.tabusearch.run;

import cn.ecnu.tabusearch.Edge;
import cn.ecnu.tabusearch.ShortPath;
import cn.ecnu.tabusearch.translate.Instruction;
import cn.ecnu.tabusearch.translate.Level;
import cn.ecnu.tabusearch.translate.Translate;
import cn.ecnu.tabusearch.utils.FileResult;
import cn.ecnu.tabusearch.utils.FileUtil;
import cn.ecnu.tabusearch.utils.PathResult;
import cn.ecnu.tabusearch.utils.PathUtil;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import static cn.ecnu.tabusearch.translate.TranslateMain.getFileList;

/**
 * 预处理文件
 *
 */
public class PrecessorMain {

    public static final Integer N=999999;
    public static void translate(int start){
        /**
         * /home/test/qubitmapping/
         * E:\github\Tabu_win\tabu\src\main\resources\example\
         */
        String inPath = "../../../src/main/resources/example/";//遍历文件夹下的所有.jpg文件
        List<String> files=  getFileList(inPath);
        System.out.println("------------------------------------------");
        for (int i=start;i<files.size();i++){
            if (files.get(i)==" "){
                return;
            }
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
                sb.append("../../../src/main/resources/examples_result/");
                String []splits=files.get(i).split("/");
                sb.append(splits[splits.length-1]);

                FileWriter fw = new FileWriter(sb.toString(), false);
                PrintWriter pw = new PrintWriter(fw);
                for (int j = 1; j <= total_level; j++) {
                    Instruction p = tower[j].head.getNext();
                    while (p!=null) {
                        p.print(pw);
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

    public static void precess(int start){
        String filePath = "../../../src/main/resources/example/";
        List<String> files = FileUtil.getFiles(filePath);
        //Adjust the life cycle of qubits
        translate(start);
        //
        /**
         * /home/test/qubitmapping
         *\\src\\main\\resources
         */
        for (int k = start; k < files.size(); k++) {
            StringBuilder str = new StringBuilder(files.get(k));
            str.delete(0, str.lastIndexOf("/") + 1);
            String ss = str.substring(0, str.lastIndexOf("."));
            System.out.println(" 处理第 "+k + " 个文件： " + ss);

            String argv1 = files.get(k);
            String argv2 = ss;
            Path qasm = Paths.get(argv1);
            try {
                FileUtil.precessReadQasm(qasm, argv2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
        int start=0;
        if(args.length>0){
            start=Integer.parseInt(args[0]);
            System.out.print(start);
        }
        precess(start);
        }
    }
