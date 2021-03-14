package cn.ecnu.superposition.translate;

import java.io.BufferedReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Translate {
        public static final Integer N = 999999;
        public static final Integer INFINITY = 9999;
        public static final Integer Visited = 1;
        private Level level=new Level(-1);

        public void insert_codes_qubit(Instruction a, Set<Qubit> Q, Qubit qubits[]) {
            for (Integer id : a.getQubits()) {
                Qubit p=null;
                for (int i=0;i<qubits.length;i++){
                    if (qubits[i].getId()==id){
                        p= qubits[i];
                    }
                }
                if (p == null) {
                    System.out.println("没有id为：" + id + "的qubit");
                    continue;
                }
                Q.add(p);
            }
        }

//    public Boolean check_code_overlap_set(Instruction a, Set<Qubit> Q) {  //检查一个ins和一个set是否重合
//        for (Integer id : a.getQubits()) {
//            Qubit q=Qubit.getQubitById(id);
//            if (q == null) {
//                System.out.println("没有id为：" + id + "的qubit");
//                continue;
//            }
//            if (Q.contains(q)) return true;
//        }
//        return false;
//    }

        public int find_next_level(Set<Qubit> qubits_set, int l) {  //找到level l之后和集合qubits_set重合的最近的level
            Iterator<Qubit> it = qubits_set.iterator();
            int min = INFINITY;
            while (it.hasNext()) {
                Qubit q = it.next();
                for (int i=0;i< q.getInstructions().size();i++) {
                    Instruction instruction=q.getInstructions().get(i);
                    if (instruction.getLine() > l&&instruction.getLine() < min) {
                        min = instruction.getLine();
                        break;
                    }
                }
            }
            return min;
        }
        public IntegerResult Lexer(Instruction codes[], Qubit qubits[], int total_qubit, int total_instruction, BufferedReader bf) {
            int j = 1/*line number*/;
            IntegerResult integerResult=new IntegerResult();
            integerResult.setTotal_instruction(total_instruction);
            integerResult.setTotal_qubit(total_qubit);
            int u = 0;
            Boolean flag=true;
            while (flag){
                BooleanResult booleanResult=codes[j].input(u, j, qubits,bf);
                if (booleanResult==null){
                    return integerResult;
                }
                flag=booleanResult.getFlag();
                j=booleanResult.getIndex();
                u=booleanResult.getTotal_qubits();
            };

            total_instruction = j - 1;
            total_qubit = u;
            integerResult.setTotal_instruction(total_instruction);
            integerResult.setTotal_qubit(total_qubit);
            return integerResult;
        }
        public  int translate(Level tower[], BufferedReader bf) {
            Instruction[] codes=new Instruction[N];
            for (int i=0;i<N;i++){
                codes[i]=new Instruction();
            }
            Qubit qubits[]=new Qubit[N];
            for (int i=0;i<N;i++){
                qubits[i]=new Qubit(i);
            }

            int total_qubits=0, total_ins=0;
            IntegerResult integerResult=Lexer(codes, qubits, total_qubits, total_ins, bf);
            total_qubits=integerResult.getTotal_qubit();
            total_ins=integerResult.getTotal_instruction();
            System.out.println("分层处理后：\n\n");
            int total_level =level.levelize(tower, codes, qubits);
            int i;
            System.out.println("\n代码变换后："+total_level);
            for (i = 1; i <= total_level; i++) {
                //tower[i].print();
                if (tower[i].is_operation == false) continue;
                Instruction p = tower[i].head.getNext();
                while (p != null) {  //一次while处理一条指令的合并

                    if (p.judgeQubits(qubits) == true) {  //如果指令的所有比特都已经处于叠加态，则不需要优化合并
                        p = p.getNext();
                        continue;
                    }
                    // p->print();
                    p.setQubitsState(Visited,qubits);  //将p的所有比特置位

                    if (p.getMark() == Visited) {
                        p = p.getNext();
                        continue;
                    }
                    Instruction  ins_unit[] =new Instruction[total_ins] ;  // ins_unit装着所有可以并到一起的ins的指针
                    for (int j=0;j<total_ins;j++){
                        ins_unit[j]=p;
                    }
                    int unit_begin = 1;               // ins_unit数组下一个加入的ins存放的下标
                    p .setMark(Visited);
                    Set<Qubit>qubits_set=new HashSet<>();
                    insert_codes_qubit( p, qubits_set,qubits);
                    p = p.getNext();
                    int cur_level = i;
                    while (qubits_set.size() < total_qubits) {
                        Integer tmp_level =
                                find_next_level(qubits_set, cur_level);  //找到和当前set重合的下一个level
                        if (tmp_level-INFINITY==0) {
                            break;
                        }
                        cur_level = tmp_level;

                        Instruction next_instruction = tower[cur_level].find_overlap_instruction(qubits_set,qubits);
                        next_instruction.setMark(Visited);
                        ins_unit[unit_begin] = next_instruction;
                        unit_begin++;
                        insert_codes_qubit(next_instruction, qubits_set,qubits);
                    }
                    cur_level = ins_unit[unit_begin - 1].getLine();
                    unit_begin--;
                    for (int u = unit_begin - 1; u >= 0; u--) {
                        Instruction ins = ins_unit[u];
                        int l = ins.getLine();
                        if (l == cur_level - unit_begin + u) continue;
                        tower[l].del(ins);
                        tower[cur_level - unit_begin + u].insert(ins);
                        ins.setLine(cur_level - unit_begin + u);
                    }

                }
            }
            return total_level;
        }


    }
