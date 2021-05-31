package cn.ecnu.tabusearch.test;

import java.io.*;
import java.util.*;

public class DealData {
    public static void main(String[] args) throws IOException {
        String ini_VF="../../../src/main/resources/results/gql_VF";
        String topgraph="../../../src/main/resources/results/topgraph";
        Map<String,List<Integer>> map_topgraph=read_topgraph_file(topgraph);
        Map<String,List<Integer>> map_ini_VF=read_topgraph_file(ini_VF);
        Map<String,List<Integer>> map1=read_tabu_file("../../../src/main/resources/results/total_tabu_lookahead2");
        Map<String,List<Integer>> map2=read_tabu_file("../../../src/main/resources/results/total_tabu_depth_ini_lookahead2");
        compare_ini_VF(map_ini_VF,map_topgraph,1);
        System.out.println("--------depth-------------num_tabu VS depth_tabu");
        compare_depth_num(map1,map2);
        System.out.println("--------swap number-------------num_tabu VS depth_tabu");
        compare_num_depth(map1,map2);
        System.out.println("---------------------num_tabu VS topgraph");
        compare_tabu_topgraph(map1,map_topgraph,1);
        System.out.println("---------------------depth_tabu VS topgraph");
        compare_tabu_topgraph(map2,map_topgraph,1);
    }

    private static void compare_ini_VF(Map<String, List<Integer>> map1, Map<String, List<Integer>> map2,Integer type) {
        int greater_top_gql=0,greater_gql_top=0,eq_gql_top=0;
        int gate_top=0,gate_gql=0; //门数量
        int gate_gql_all=0;
        int pub_res=0;
        double pro_top_gql=0,pro_gql_top=0;
        for(Map.Entry<String,List<Integer> > set : map1.entrySet()){
            List<Integer> v1list=set.getValue();
            gate_gql_all+=v1list.get(1);
            if (map2.get(set.getKey())==null){
                continue;
            }
            //比较swap个数
            Integer v1=v1list.get(1);
            Integer v2=map2.get(set.getKey()).get(1);

            //比较depth
            if (type==0){
                v1=v1list.get(0);
                v2=map2.get(set.getKey()).get(0);
            }
            pub_res++;
            gate_gql+=v1;
            gate_top+=v2;
//                System.out.print(set.getKey()+"  ");
            if (v1<v2){
                greater_gql_top++;
                pro_gql_top+=(v2-v1+0.0)/v2;
//                    System.out.print((v1-v2+0.0)/v1*100+"% ");
            }else if (v2<v1){
                greater_top_gql++;
                pro_top_gql+=(v1-v2+0.0)/v1;
//                    System.out.print("-"+(v2-v1+0.0)/v2*100+"% ");
            }else{
                eq_gql_top++;
//                    System.out.print(0*100+0.0+"% ");
            }
//                System.out.println();

        }
        if (type==0){
            System.out.println("比较深度：");
        }else{
            System.out.println("比较swap数量：");
        }
        System.out.println("topgraph 结果数量："+map2.size()+" gql 结果数量："+map1.size());
        System.out.println(" gql swaps数量："+gate_gql_all);
        System.out.println("两个都有结果："+pub_res);
        System.out.println("topgraph："+gate_top+" gql："+gate_gql);
        System.out.println("topgraph比gql好： "+ greater_top_gql+" gql比topgraph好： "+greater_gql_top+" 相等： "+eq_gql_top);

        System.out.println("topgraph比gql ："+(gate_gql-gate_top+0.0)/gate_gql*100+"% ");
        System.out.println("gql比topgraph："+(gate_top-gate_gql+0.0)/gate_top*100+"% ");

    }

    private static void compare_tabu_topgraph(Map<String, List<Integer>> map1, Map<String,List<Integer>>  map_topgraph,Integer type) {
        int greater_top_tabu=0,greater_tabu_top=0,eq_tabu_top=0;
        int gate_top=0,gate_tabu=0; //门数量
        int gate_tabu_all=0;
        int pub_res=0;
        double pro_top_tabu=0,pro_tabu_top=0;
        for(Map.Entry<String,List<Integer> > set : map1.entrySet()){
            List<Integer> v1list=set.getValue();
            gate_tabu_all+=v1list.get(4);
            if (map_topgraph.get(set.getKey())==null){
                continue;
            }
            //比较swap个数
            Integer v1=v1list.get(4);
            Integer v2=map_topgraph.get(set.getKey()).get(1);

            //比较depth
            if (type==0){
                v1=v1list.get(3);
                v2=map_topgraph.get(set.getKey()).get(0);
            }
            pub_res++;
            gate_tabu+=v1;
            gate_top+=v2;
//                System.out.print(set.getKey()+"  ");
            if (v1<v2){
                greater_tabu_top++;
                pro_tabu_top+=(v2-v1+0.0)/v2;
//                    System.out.print((v1-v2+0.0)/v1*100+"% ");
            }else if (v2<v1){
                greater_top_tabu++;
                pro_top_tabu+=(v1-v2+0.0)/v1;
//                    System.out.print("-"+(v2-v1+0.0)/v2*100+"% ");
            }else{
                eq_tabu_top++;
//                    System.out.print(0*100+0.0+"% ");
            }
//                System.out.println();

        }
        if (type==0){
            System.out.println("比较深度：");
        }else{
            System.out.println("比较swap数量：");
        }
        System.out.println("topgraph 结果数量："+map_topgraph.size()+" tabu 结果数量："+map1.size());
        System.out.println(" tabu swaps数量："+gate_tabu_all);
        System.out.println("两个都有结果："+pub_res);
        System.out.println("topgraph："+gate_top+" tabu："+gate_tabu);
        System.out.println("topgraph比tabu好： "+ greater_top_tabu+" tabu比topgraph好： "+greater_tabu_top+" 相等： "+eq_tabu_top);

        System.out.println("topgraph比tabu ："+(gate_tabu-gate_top+0.0)/gate_tabu*100+"% ");
        System.out.println("tabu比topgraph："+(gate_top-gate_tabu+0.0)/gate_top*100+"% ");

    }

    private static void compare_num_depth(Map<String, List<Integer>> map1, Map<String, List<Integer>> map2) {
        int greater_num_dep=0,greater_dep_num=0,eq_num_dep=0;
        int pub_res=0;
        int num_dep=0,dep_num=0;
        int num_gates_sum=0,depth_gates_sum=0; //生成电路的2-qubits门数量
        int ini_num_gates_sum=0,ini_depth_gates_sum=0; //初始电路的2-qubits门数量
        double pro_num_dep=0,pro_dep_num=0;
        for(Map.Entry<String,List<Integer> > set : map1.entrySet()){
            List<Integer> v1list=set.getValue();
            List<Integer> v2list=map2.get(set.getKey());
            //比较swap个数
            Integer v1=v1list.get(4); //num
            Integer v2=v2list.get(4); //depth
            num_gates_sum+=v1list.get(2);
            depth_gates_sum+=v2list.get(2);
            ini_num_gates_sum+=v1list.get(1);
            ini_depth_gates_sum+=v2list.get(1);
            //比较深度
//            Integer v1=v1list.get(2);
//            Integer v2=v2list.get(2);


            if (v2!=null){

                pub_res++;
//                System.out.print(set.getKey()+"  ");
                num_dep+=v1;
                dep_num+=v2;
                if (v2<v1){
                    greater_dep_num++;
                    pro_dep_num+=(v1-v2+0.0)/v1;
//                    System.out.print((v1-v2+0.0)/v1*100+"% ");
                }else if (v1<v2){
                    greater_num_dep++;
                    pro_num_dep+=(v2-v1+0.0)/v2;
//                    System.out.print("-"+(v2-v1+0.0)/v2*100+"% ");
                }else{
                    eq_num_dep++;
//                    System.out.print(0*100+0.0+"% ");
                }
//                System.out.println();
            }
        }
        System.out.println("比较swap数量：");
        System.out.println("dep 结果数量："+map2.size()+" num 结果数量："+map1.size());
        System.out.println("两个都有结果："+pub_res);
        System.out.println("添加的swap数量 dep总共有： "+dep_num+" 个，num一共有： "+num_dep);
        System.out.println("dep比num好： "+ greater_dep_num+" num比dep好： "+greater_num_dep+" 相等： "+eq_num_dep);
        System.out.println("初始2-qubits门个数： swap数量优先："+ini_num_gates_sum+" 深度优先："+ini_depth_gates_sum);
        System.out.println("结果2-qubits门个数： swap数量优先："+num_gates_sum+" 深度优先："+depth_gates_sum);
        System.out.println("swap数量优先比深度优先插入swap数量比较："+(dep_num-num_dep+0.0)/dep_num*100+"% ");
        System.out.println("深度优先比swap数量优先插入swap数量比较："+(num_dep-dep_num+0.0)/num_dep*100+"% ");

    }
    private static void compare_depth_num(Map<String, List<Integer>> map1, Map<String, List<Integer>> map2) {
        int greater_num_dep=0,greater_dep_num=0,eq_num_dep=0;
        int pub_res=0;
        int num_dep=0,dep_num=0;
        double pro_num_dep=0,pro_dep_num=0;
        for(Map.Entry<String,List<Integer> > set : map1.entrySet()){
            List<Integer> v1list=set.getValue();
            List<Integer> v2list=map2.get(set.getKey());
            //比较深度
            Integer v1=v1list.get(3);
            Integer v2=v2list.get(3);
            if (v2!=null){
                pub_res++;
//                System.out.print(set.getKey()+"  ");
                num_dep+=v1;
                dep_num+=v2;
                if (v2<v1){
                    greater_dep_num++;
                    pro_dep_num+=(v1-v2+0.0)/v1;
//                    System.out.print((v1-v2+0.0)/v1*100+"% ");
                }else if (v1<v2){
                    greater_num_dep++;
                    pro_num_dep+=(v2-v1+0.0)/v2;
//                    System.out.print("-"+(v2-v1+0.0)/v2*100+"% ");
                }else{
                    eq_num_dep++;
//                    System.out.print(0*100+0.0+"% ");
                }
//                System.out.println();
            }
        }
        System.out.println("比较depth数量：");
        System.out.println("dep 结果数量："+map2.size()+" num 结果数量："+map1.size());
        System.out.println("两个都有结果："+pub_res);
        System.out.println("dep总共有： "+dep_num+" 个，num一共有： "+num_dep);
        System.out.println("dep比num好： "+ greater_dep_num+" num比dep好： "+greater_num_dep+" 相等： "+eq_num_dep);

        System.out.println("swap数量优先比深度优先插入depth数量比较："+(pro_dep_num-pro_num_dep+0.0)/pub_res*100+"% ");
        System.out.println("深度优先比swap数量优先插入depth数量比较："+(pro_num_dep-pro_dep_num+0.0)/pub_res*100+"% ");
        System.out.println("swap数量优先比深度优先插入depth数量比较："+(dep_num-num_dep+0.0)/dep_num*100+"% ");
        System.out.println("深度优先比swap数量优先插入depth数量比较："+(num_dep-dep_num+0.0)/num_dep*100+"% ");

    }

    public static Map<String , List<Integer>> read_topgraph_file(String s) throws IOException {
        Map<String,  List<Integer>> map=new HashMap<>();
        if (s.equals("")){
            System.out.println("文件地址错误");
            return map;
        }
        File f=new File(s);
        FileReader fre=new FileReader(f);
        BufferedReader bre=new BufferedReader(fre);
        String str="";
        while((str=bre.readLine())!=null&&!str.equals("")) //●判断最后一行不存在，为空
        {
            String value=bre.readLine();
            if (value == null) {
                System.out.println(str);
                continue;
            }
            String [] arr=value.split(" ");
            List<Integer> list=new ArrayList<>();
            list.add(Integer.parseInt(arr[0])); //深度不包含单量子门
            list.add(Integer.parseInt(arr[1])); //swap个数
            map.put(str,list);
        }
        bre.close();
        fre.close();
        return map;
    }

    public static Map<String , List<Integer>> read_tabu_file(String s) throws IOException {
        Map<String, List<Integer>> map=new HashMap<>();
        if (s.equals("")){
            System.out.println("文件地址错误");
            return map;
        }
        File f=new File(s);
        FileReader fre=new FileReader(f);
        BufferedReader bre=new BufferedReader(fre);
        String str="";
        while((str=bre.readLine())!=null) //●判断最后一行不存在，为空
        {
            String value=bre.readLine();
            System.out.println(str);

            String[] arr =value.split(" ");
            List<Integer> list=new ArrayList<>();
            if (arr.length==5){
                list.add(Integer.parseInt(arr[0])); //index
                list.add(Integer.parseInt(arr[1]));//初始电路2qubit个数
                list.add(Integer.parseInt(arr[2]));//生成电路的深度包含单量子门
                list.add(Integer.parseInt(arr[3]));//生成电路2qubit个数
                list.add(Integer.parseInt(arr[4]));//添加的swap个数
            }
            map.put(str,list);
        }

        bre.close();
        fre.close();
        return map;
    }
}
