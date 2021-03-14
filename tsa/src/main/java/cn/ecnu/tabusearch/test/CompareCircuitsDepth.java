package cn.ecnu.tabusearch.test;

import cn.ecnu.tabusearch.utils.FileResult;
import cn.ecnu.tabusearch.utils.FileUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class CompareCircuitsDepth {
    public static void main(String[] args) throws IOException {
        read_filenames("large");
//        generate_VF_formalization(); 66 49 44
    }

    //生成VF格式的文件
    private static void generate_VF_formalization() throws IOException {
        String filePath = "../../../src/main/resources/examples_result/";

        List<String> files = FileUtil.getFiles(filePath);
        for (int k = 0; k < files.size(); k++) {
            StringBuilder str = new StringBuilder(files.get(k));
            str.delete(0, str.lastIndexOf("/") + 1);
            String ss = str.substring(0, str.lastIndexOf("."));
            System.out.println("处理文件： " + ss);
            FileUtil.generate_VF_formalization(Paths.get(files.get(k)), ss);
        }
    }

    //获取文件夹下的所有文件
    private static void read_filenames(String argv) throws FileNotFoundException {
        String filePath = "../../../src/main/resources/examples_result/";

        List<String> files = FileUtil.getFiles(filePath);
        //初始电路门数量
        int ini_gate_num = 0;
        Integer small = 0,
                large = 0,
                medium = 0;
        for (int k = 0; k < files.size(); k++) {
            StringBuilder str = new StringBuilder(files.get(k));
            str.delete(0, str.lastIndexOf("/") + 1);
            String ss = str.substring(0, str.lastIndexOf("."));
            String argv1 = files.get(k);
            String argv2 = ss;
            FileResult fileResult = FileUtil.read_qasm_to_compute_depth(Paths.get(files.get(k)));
            ini_gate_num += fileResult.getNgates();
            System.out.println(fileResult.getLayers().size());
            if (argv.equals("small")){
                if (fileResult.getN2gates()>=100){
                    continue;
                }else{
                    small++;
                    System.out.println(k+" "+ ss);
                }
            }else  if (argv.equals("medium")){
                if (fileResult.getN2gates()>=100&&fileResult.getN2gates()<=1000){
                    medium++;
                    System.out.println(k+" "+ ss);
                }else{
                    continue;
                }
            }else if (argv.equals("large")){
                if (fileResult.getN2gates()>1000){
                    large++;
                    System.out.println(k+" "+ ss);
                }else{
                    continue;
                }
            }
    }
            System.out.println(small + " " + medium + " " + large);

//            System.out.println(ss+"： " + fileResult.getNgates());
        }
//        System.out.println("2_qubits门总数： " + ini_gate_num);
//    }
}
