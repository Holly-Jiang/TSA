package cn.ecnu.vf2.runner;

import java.util.*;
class LL{
    List<LinkedList<Integer>> ids=new LinkedList<>();
}
class Employee {
    public int id;
    public int importance;
    public List<Integer> subordinates;
};
public class Leetcode {

    public static List<List<Integer>> findSubsequences(int[] nums) {
        List<List<Integer>> result=new ArrayList<>();
        Map<Integer,Integer> map=new HashMap<>();
        for(int i=0;i<nums.length;i++){
            int len=result.size();
            for(int j=0;j<len;j++){
                if(map.get(nums[i])!=null){
                    if(result.get(j).get(result.get(j).size()-1)==nums[i]){
                        List<Integer> list=new ArrayList<>(result.get(j));
                        list.add(nums[i]);
                        result.add(list);
                    }
                }else {
                    if(result.get(j).get(result.get(j).size()-1)<nums[i]){
                        List<Integer> list=new ArrayList<>(result.get(j));
                        list.add(nums[i]);
                        result.add(list);
                    }
                }

            }

            List<Integer> list=new ArrayList<Integer>();
            list.add(nums[i]);
            result.add(list);
            map.put(nums[i],0);

        }

        for(int i=0;i<result.size();){
            if(result.get(i).size()==1){
                result.remove(result.get(i));
            }else {
                i++;
            }
        }
        return result;
    }
    public static void main(String[] args) {
        int [] []nums=new int[][]{};
//        System.out.println(numDecodings("111111111111111111111111111111111111111111111"));
        System.out.println(robot("URR",nums,3,2));
    }
    public int getImportance(List<Employee> employees, int id) {
        if(employees.size()<=0){
            return 0;
        }
        Map<Integer,Employee> map=new HashMap<>();
        for(int i=0;i<employees.size();i++){
            map.put(employees.get(i).id,employees.get(i));
        }
        Employee e1= map.get(id);
        int sum=0;
        LinkedList<Integer>queue=new LinkedList<>();

        queue.addLast(id);
        while(queue.isEmpty()){
            Integer e_id=queue.removeFirst();
            Employee e2=map.get(e_id);
            for(int i=0;i<e2.subordinates.size();i++){
                queue.addLast(e2.subordinates.get(i));
            }
            sum+=e2.importance;
        }
        return sum;
    }
    public static   boolean wordBreak(String s, List<String> wordDict) {
        int len=s.length();
        boolean [][]dp=new boolean [len][len];
        for(int i=0;i<len;i++){
            for(int j=i;j<=len;j++){
                if(word(s.substring(i,j),wordDict)&&satisfy(dp,i-1)){
                    dp[i][j==0?j:j-1]=true;
                }else{
                    dp[i][j==0?j:j-1]=false;
                }
            }
        }
        for(int i=0;i<len;i++){
            if(dp[len-1][i]||dp[i][len-1]){
                return true;
            }
        }
        return false;
    }
    public static  boolean satisfy(boolean [][]dp,int index){
        if(index<0){
            return true;
        }
        for(int i=0;i<dp.length;i++){
            if(dp[i][index]){
                return true;
            }
        }
        return false;
    }
    public static boolean word(String s, List<String> wordDict){
        for(int i=0;i<wordDict.size();i++){
            if(wordDict.get(i).equals(s)){
                return true;
            }
        }
        return false;
    }
    public static int numDecodings(String s) {
        if(s.equals("0")){
            return 0;
        }
        return recursive(s,0);
    }

    public static int recursive(String s, int index) {
        if(index>=s.length()-1){
            return 1;
        }
        if(s.charAt(index)=='0'){
            return 0;
        }
        int l=0;
        if(s.charAt(index+1)>'0'){
            l=recursive(s,index+1);
        }
        int r=0;
        if(index<(s.length()-1)&&s.charAt(index)<'3'){
            r=recursive(s,index+2);
        }
        return l+r;
    }
    public static boolean buddyStrings(String a, String b) {
        int l=-1,r=-1;int i=0;
        for(i=0;i<a.length();i++){
            if(a.charAt(i)!=b.charAt(i)){
                l=i;
                break;
            }
        }
        for(i++;i<a.length();i++){
            if(a.charAt(i)!=b.charAt(i)){
                r=i;
                break;
            }
        }
        if(l==-1&&r==-1){
            return false;
        }else if (l!=-1&&r!=-1){
            String a1=a.substring(0,l)+a.charAt(r)+""+a.substring(l+1,r)+a.charAt(l)+""+a.substring(r+1);
            if(a1.equals(b)){
                return true;
            }
        }

        return false;
    }

    public static boolean robot(String command, int[][] obstacles, int x, int y) {
        int len =command.length();
        int a=0,b=0;
        int arr[][]=new int[x][y];
        for(int i=0;i<obstacles.length;i++){
            arr[obstacles[i][0]][obstacles[i][1]]=1;
        }
        for(int i=0;i<len;i++,i%=len){
            if(a==x-1&&b==y-1){
                return true;
            }
            if(command.charAt(i)=='U'){
                if(b+1>=y||arr[a][b+1]==1){
                    return false;
                }
                b++;
            }else if(command.charAt(i)=='R'){
                if(a+1>=x||arr[a+1][b]==1){
                    return false;
                }
                a++;
            }
        }
        return false;
    }
}
