package cn.ecnu.tabusearch.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {
    public static void main(String[] args) throws IOException {
        String startStr="../../../src/main/resources/compare/initial_information";
        Map<String , List<List<Integer>>> start_result=readStart(startStr);
        String optmStr="../../../src/main/resources/compare/total_A_ini_connect";
        Map<String , List<List<Integer>>> optmStr_result=readOptm(optmStr);
        String wghgrStr="../../../src/main/resources/compare/wgtgraph";
        Map<String , List<List<Integer>>> wghgr_result=readWghgr(wghgrStr);
        String tabuStr="../../../src/main/resources/compare/total_tabu_lookahead2";
        Map<String , List<List<Integer>>> num_tabu_result=readTabu(tabuStr);
        String depthtabuStr="../../../src/main/resources/compare/total_tabu_depth_ini_lookahead2";
        Map<String , List<List<Integer>>> depth_tabu_result=readTabu(depthtabuStr);
        print_num(start_result,optmStr_result,wghgr_result,num_tabu_result,depth_tabu_result);
//        print_non_num(start_result,optmStr_result,wghgr_result,num_tabu_result,depth_tabu_result);
//        print_depth(start_result,optmStr_result,wghgr_result,num_tabu_result,depth_tabu_result);
//        print_non_dep(start_result,optmStr_result,wghgr_result,num_tabu_result,depth_tabu_result);


        System.out.println();

    }
    private static void print_non_dep(Map<String, List<List<Integer>>> start_result, Map<String, List<List<Integer>>> optmStr_result,
                                      Map<String, List<List<Integer>>> wghgr_result, Map<String, List<List<Integer>>> num_tabu_result,
                                      Map<String, List<List<Integer>>> depth_tabu_result) {
        int sum0=0,sum1=0,sum2=0,sum3=0,sum4=0,sum5=0;
        for(Map.Entry<String,List<List<Integer>>> set: start_result.entrySet()){
            if (num_tabu_result.get(set.getKey())!=null&&depth_tabu_result.get(set.getKey())!=null&&
                    optmStr_result.get(set.getKey())!=null){

                List<List<Integer>> num_tabu_value=num_tabu_result.get(set.getKey());
                List<List<Integer>> dep_tabu_value=depth_tabu_result.get(set.getKey());
                List<List<Integer>> optm_value=optmStr_result.get(set.getKey());
                if (num_tabu_value.get(0).get(2)!=9999999&&dep_tabu_value.get(0).get(2)!=9999999
                        &&optm_value.get(1).get(2)!=9999999){
                    List<List<Integer>> value=set.getValue();
                    System.out.print(set.getKey()+" & "+value.get(0).get(0)+" & "+value.get(0).get(2)+" & "+value.get(0).get(1)+" & ");
                    System.out.print(num_tabu_value.get(0).get(2)+" & ");
                    System.out.print(dep_tabu_value.get(0).get(2)+" & ");
                    System.out.print(optm_value.get(1).get(2)+" \\\\ ");
                    sum0+=value.get(0).get(0);//qubit num
                    sum1+=value.get(0).get(1); // ini depth
                    sum5+=value.get(0).get(2); // 2-qubitgate
                    sum2+=num_tabu_value.get(0).get(2);
                    sum3+=dep_tabu_value.get(0).get(2);
                    sum4+=optm_value.get(1).get(2);
                    System.out.println();
                }
            }
        }
        System.out.println("---------------------------------------------------");
        for(Map.Entry<String,List<List<Integer>>> set: start_result.entrySet()){
            if ((num_tabu_result.get(set.getKey())!=null&&depth_tabu_result.get(set.getKey())!=null&&
                    optmStr_result.get(set.getKey())!=null)){
                List<List<Integer>> num_tabu_value=num_tabu_result.get(set.getKey());
                List<List<Integer>> dep_tabu_value=depth_tabu_result.get(set.getKey());
                List<List<Integer>> optm_value=optmStr_result.get(set.getKey());
                if (!(num_tabu_value.get(0).get(2)!=9999999&&dep_tabu_value.get(0).get(2)!=9999999
                        &&optm_value.get(1).get(2)!=9999999)){
                    List<List<Integer>> value=set.getValue();
                    System.out.print(set.getKey()+" & "+value.get(0).get(0)+" & "+value.get(0).get(2)+" & "+value.get(0).get(1)+" & ");
                    System.out.print(num_tabu_value.get(0).get(2)+" & ");
                    System.out.print(dep_tabu_value.get(0).get(2)+" & ");
                    System.out.print(optm_value.get(1).get(2)+" \\\\ ");
                    System.out.println();
                }
        }
        }
        System.out.println(sum0+" & "+sum5+" & "+sum1+" & "+sum2+" & "+sum3+" & "+sum4+" & "+" \\\\ ");
    }

    private static void print_depth(Map<String, List<List<Integer>>> start_result, Map<String, List<List<Integer>>> optmStr_result, Map<String, List<List<Integer>>> wghgr_result, Map<String, List<List<Integer>>> num_tabu_result, Map<String, List<List<Integer>>> depth_tabu_result) {
        for(Map.Entry<String,List<List<Integer>>> set: start_result.entrySet()){
            List<List<Integer>> value=set.getValue();
            System.out.print(set.getKey()+" & "+value.get(0).get(0)+" & "+value.get(0).get(1)+" & ");
            if (num_tabu_result.get(set.getKey())!=null){
                List<List<Integer>> tabu_value=num_tabu_result.get(set.getKey());
                System.out.print(tabu_value.get(0).get(2)+" & ");
            }else{
                System.out.print("- & ");
            }
            if (depth_tabu_result.get(set.getKey())!=null){
                List<List<Integer>> tabu_value=depth_tabu_result.get(set.getKey());
                System.out.print(tabu_value.get(0).get(2)+" & ");

            }else{
                System.out.print("- & ");
            }
            if (optmStr_result.get(set.getKey())!=null){
                List<List<Integer>> optm_value=optmStr_result.get(set.getKey());
                System.out.print(optm_value.get(1).get(2)+" \\\\ ");
            }else{
                System.out.print("-  \\\\ ");
            }
        }
    }

    private static void print_num(Map<String, List<List<Integer>>> start_result, Map<String, List<List<Integer>>> optmStr_result,
                                  Map<String, List<List<Integer>>> wghgr_result, Map<String, List<List<Integer>>> num_tabu_result,
                                  Map<String, List<List<Integer>>> depth_tabu_result) {
        for(Map.Entry<String,List<List<Integer>>> set: start_result.entrySet()){
            List<List<Integer>> value=set.getValue();
            System.out.print(set.getKey()+" & "+value.get(0).get(0)+" & "+value.get(0).get(2)+" & ");
            if (num_tabu_result.get(set.getKey())!=null){
                List<List<Integer>> tabu_value=num_tabu_result.get(set.getKey());
                System.out.print(tabu_value.get(0).get(4)+" & ");
            }else{
                System.out.print("- & ");
            }
            if (depth_tabu_result.get(set.getKey())!=null){
                List<List<Integer>> tabu_value=depth_tabu_result.get(set.getKey());
                System.out.print(tabu_value.get(0).get(4)+" & ");

            }else{
                System.out.print("- & ");
            }
            if (optmStr_result.get(set.getKey())!=null){
                List<List<Integer>> optm_value=optmStr_result.get(set.getKey());
                System.out.print(optm_value.get(1).get(3)+" & ");
            }else{
                System.out.print("- & ");
            }
            if (wghgr_result.get(set.getKey())!=null){
                List<List<Integer>> wghtgr_value=wghgr_result.get(set.getKey());
                System.out.print(wghtgr_value.get(0).get(1)+" \\\\ ");
            }else{
                System.out.print("- \\\\ ");
            }
            System.out.println();
        }
    }
    private static void print_non_num(Map<String, List<List<Integer>>> start_result, Map<String, List<List<Integer>>> optmStr_result,
                                  Map<String, List<List<Integer>>> wghgr_result, Map<String, List<List<Integer>>> num_tabu_result,
                                  Map<String, List<List<Integer>>> depth_tabu_result) {
        int sum0=0,sum1=0,sum2=0,sum3=0,sum4=0,sum5=0,sum6=0,sum7=0,sum8=0,sum9=0;
        for(Map.Entry<String,List<List<Integer>>> set: start_result.entrySet()){
            if (num_tabu_result.get(set.getKey())!=null&&depth_tabu_result.get(set.getKey())!=null&&
                    optmStr_result.get(set.getKey())!=null&&wghgr_result.get(set.getKey())!=null

            ){

                List<List<Integer>> num_tabu_value=num_tabu_result.get(set.getKey());
                List<List<Integer>> dep_tabu_value=depth_tabu_result.get(set.getKey());
                List<List<Integer>> optm_value=optmStr_result.get(set.getKey());
                List<List<Integer>> wghtgr_value=wghgr_result.get(set.getKey());
                if (num_tabu_value.get(0).get(4)!=9999999&&dep_tabu_value.get(0).get(4)!=9999999
                        &&optm_value.get(1).get(3)!=9999999&&wghtgr_value.get(0).get(1)!=9999999){
                    List<List<Integer>> value=set.getValue();
                    System.out.print(set.getKey()+" & "+value.get(0).get(0)+" & "+value.get(0).get(2)+" & ");
                    System.out.print(num_tabu_value.get(0).get(4)+" & ");
                    System.out.print(dep_tabu_value.get(0).get(4)+" & ");
                    System.out.print(optm_value.get(1).get(3)+" & ");
                    System.out.print(wghtgr_value.get(0).get(1)+" \\\\ ");
                    sum0+=value.get(0).get(0);
                    sum1+=value.get(0).get(2);
                    sum2+=num_tabu_value.get(0).get(4);
                    sum3+=dep_tabu_value.get(0).get(4);
                    sum4+=optm_value.get(1).get(3);
                    sum5+=wghtgr_value.get(0).get(1);

                    System.out.println();
                }
            }
        }
        System.out.println("---------------------------------------------------");
        for(Map.Entry<String,List<List<Integer>>> set: start_result.entrySet()) {
            if (!(num_tabu_result.get(set.getKey()) != null && depth_tabu_result.get(set.getKey()) != null &&
                    optmStr_result.get(set.getKey()) != null && wghgr_result.get(set.getKey()) != null)) {
                List<List<Integer>> value = set.getValue();
                System.out.print(set.getKey() + " & " + value.get(0).get(0) + " & " + value.get(0).get(2) + " & ");
                if (num_tabu_result.get(set.getKey()) != null) {
                    List<List<Integer>> tabu_value = num_tabu_result.get(set.getKey());
                    System.out.print(tabu_value.get(0).get(4) + " & ");
                } else {
                    System.out.print("- & ");
                }
                if (depth_tabu_result.get(set.getKey()) != null) {
                    List<List<Integer>> tabu_value = depth_tabu_result.get(set.getKey());
                    System.out.print(tabu_value.get(0).get(4) + " & ");

                } else {
                    System.out.print("- & ");
                }
                if (optmStr_result.get(set.getKey()) != null) {
                    List<List<Integer>> optm_value = optmStr_result.get(set.getKey());
                    System.out.print(optm_value.get(1).get(3) + " & ");
                } else {
                    System.out.print("- & ");
                }
                if (wghgr_result.get(set.getKey()) != null) {
                    List<List<Integer>> wghtgr_value = wghgr_result.get(set.getKey());
                    System.out.print(wghtgr_value.get(0).get(1) + " \\\\ ");
                } else {
                    System.out.print("- \\\\ ");
                }
                System.out.println();
            }
        }
        System.out.println(sum0+" & "+sum1+" & "+sum2+" & "+sum3+" & "+sum4+" & "+sum5+" \\\\ ");
    }

    public static Map<String , List<List<Integer>>> readStart(String s) throws IOException {
        Map<String, List<List<Integer>>> map=new HashMap<>();
        if (s.equals("")){
            System.out.println("文件地址错误");
            return map;
        }
        File f=new File(s);
        FileReader fre=new FileReader(f);
        BufferedReader bre=new BufferedReader(fre);
        String str="";
        List<List<Integer>> res=new ArrayList<>();
        String key="";
        while((str=bre.readLine())!=null) //●判断最后一行不存在，为空
        {
            String[] arr =str.split(" ");
            List<Integer> list=new ArrayList<>();
            if (arr.length==3){
                list.add(Integer.parseInt(arr[0])); //qubits
                list.add(Integer.parseInt(arr[1]));//初始电路深度 含单量子门
                list.add(Integer.parseInt(arr[2]));//初始电路2qubit门个数
                res.add(list);
            }else{
                if (res!=null&&res.size()>0){
                    map.put(key,res);
                    res=new ArrayList<>();
                }
                key=str;
            }
        }
        bre.close();
        fre.close();
        return map;
    }
    public static Map<String , List<List<Integer>>> readOptm(String s) throws IOException {
        Map<String, List<List<Integer>>> map=new HashMap<>();
        if (s.equals("")){
            System.out.println("文件地址错误");
            return map;
        }
        File f=new File(s);
        FileReader fre=new FileReader(f);
        BufferedReader bre=new BufferedReader(fre);
        String str="";
        List<List<Integer>> res=new ArrayList<>();
        String key="";
        while((str=bre.readLine())!=null) //●判断最后一行不存在，为空
        {
            

            String[] arr =str.trim().split(" ");
            List<Integer> list=new ArrayList<>();
            if (arr.length==4){
                list.add(Integer.parseInt(arr[0])); //index
                list.add(Integer.parseInt(arr[1]));//初始电路门个数
                list.add(Integer.parseInt(arr[2])); //depth
                list.add(Integer.parseInt(arr[3]));//初始电路add swap个数
                res.add(list);
            }else{
                if (res!=null&&res.size()>0){
                    map.put(key,res);
                    res=new ArrayList<>();
                }
                key=str;
            }
        }
        bre.close();
        fre.close();
        return map;
    }
    public static Map<String , List<List<Integer>>> readWghgr(String s) throws IOException {
        Map<String, List<List<Integer>>> map=new HashMap<>();
        if (s.equals("")){
            System.out.println("文件地址错误");
            return map;
        }
        File f=new File(s);
        FileReader fre=new FileReader(f);
        BufferedReader bre=new BufferedReader(fre);
        String str="";
        List<List<Integer>> res=new ArrayList<>();
        String key="";
        while((str=bre.readLine())!=null) //●判断最后一行不存在，为空
        {
            

            String[] arr =str.split(" ");
            List<Integer> list=new ArrayList<>();
            if (arr.length==2){
                list.add(Integer.parseInt(arr[0])); //depth 不加单量子门
                list.add(Integer.parseInt(arr[1]));//add swaps
                res.add(list);
            }else{
                if (res!=null&&res.size()>0){
                    map.put(key,res);
                    res=new ArrayList<>();
                }
                key=str;
            }
        }
        bre.close();
        fre.close();
        return map;
    }
    public static Map<String , List<List<Integer>>> readTabu(String s) throws IOException {
        Map<String, List<List<Integer>>> map=new HashMap<>();
        if (s.equals("")){
            System.out.println("文件地址错误");
            return map;
        }
        File f=new File(s);
        FileReader fre=new FileReader(f);
        BufferedReader bre=new BufferedReader(fre);
        String str="";
        List<List<Integer>> res=new ArrayList<>();
        String key="";
        while((str=bre.readLine())!=null) //●判断最后一行不存在，为空
        {
            

            String[] arr =str.split(" ");
            List<Integer> list=new ArrayList<>();
            if (arr.length==5){
                list.add(Integer.parseInt(arr[0])); //index
                list.add(Integer.parseInt(arr[1]));//初始电路2qubit门个数
                list.add(Integer.parseInt(arr[2]));//生成电路的深度包含单量子门
                list.add(Integer.parseInt(arr[3]));//生成电路2qubit门个数
                list.add(Integer.parseInt(arr[4]));//添加的swap个数
                res.add(list);
            }else{
                if (res!=null&&res.size()>0){
                    map.put(key,res);
                    res=new ArrayList<>();
                }
                key=str;
            }
        }
        bre.close();
        fre.close();
        return map;
    }
}
