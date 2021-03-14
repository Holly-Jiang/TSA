package cn.ecnu.tabusearch.translate;

import java.util.ArrayList;
import java.util.List;

public class Qubit {
        private String name;
        private Integer id;
        private List<Instruction> instructions=new ArrayList<>();
        private int sign = 0;
        private int mark = 0;

        public Qubit(Integer id) {
            this.id = id;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

//    public static Qubit getQubitById(Integer id){
//        for (int i=0;i<allQubits.size();i++){
//            if (allQubits.get(i).getId()==id){
//                return allQubits.get(i);
//            }
//        }
//        return null;
//    }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Instruction> getInstructions() {
            return instructions;
        }

        public void setInstructions(List<Instruction> instructions) {
            this.instructions = instructions;
        }

        public int getSign() {
            return sign;
        }

        public void setSign(int sign) {
            this.sign = sign;
        }

        public int getMark() {
            return mark;
        }

        public void setMark(int mark) {
            this.mark = mark;
        }

        @Override
        public String toString() {
            return "Qubit{" +
                    "name='" + name + '\'' +
                    ", id=" + id +
                    ", instructions=" + instructions +
                    ", sign=" + sign +
                    ", mark=" + mark +
                    '}';
        }

        public void print() {
            System.out.print(this.getName()+": ");
            for (Instruction id : this.getInstructions()) {
                System.out.print(id.getLine() + " ");
            }
            System.out.println();
        }

        public void insert(Instruction n) {
            this.instructions.add(n);
        }

//
//    @Override
//    public int compareTo(Qubit o) {
//        if (this.equals(o)&&this.hashCode()==o.hashCode()){
//            return 0;
//        }else if (!this.equals(o)&&this.hashCode()>o.hashCode()){
//            return 1;
//        }else{
//            return -1;
//        }
//    }
    }
