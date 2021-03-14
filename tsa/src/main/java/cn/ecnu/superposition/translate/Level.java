package cn.ecnu.superposition.translate;

import java.util.Objects;
import java.util.Set;

public class Level {
        public static final Integer N = 999999;
        private Integer id;
        public Instruction level_head = new Instruction();
        public Instruction head = level_head;
        public int line = 0;
        public int length = 0;
        public Boolean is_operation = true;
        public Instruction tail = head;

        public Level(Integer id) {
            this.id = id;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Instruction getLevel_head() {
            return level_head;
        }

        public void setLevel_head(Instruction level_head) {
            this.level_head = level_head;
        }

        public Instruction getHead() {
            return head;
        }

        public void setHead(Instruction head) {
            this.head = head;
        }

        public int getLine() {
            return line;
        }

        public void setLine(int line) {
            this.line = line;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public Boolean getIs_operation() {
            return is_operation;
        }

        public void setIs_operation(Boolean is_operation) {
            this.is_operation = is_operation;
        }

        public Instruction getTail() {
            return tail;
        }

        public void setTail(Instruction tail) {
            this.tail = tail;
        }

        @Override
        public String toString() {
            return "Level{" +
                    "id=" + id +
                    ", level_head=" + level_head +
                    ", head=" + head +
                    ", line=" + line +
                    ", length=" + length +
                    ", is_operation=" + is_operation +
                    ", tail=" + tail +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Level level = (Level) o;
            return line == level.line &&
                    length == level.length &&
                    Objects.equals(id, level.id) &&
                    Objects.equals(level_head, level.level_head) &&
                    Objects.equals(head, level.head) &&
                    Objects.equals(is_operation, level.is_operation) &&
                    Objects.equals(tail, level.tail);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, level_head, head, line, length, is_operation, tail);
        }

        public void insert(Instruction a) {
            tail.setNext(a);
            a.setNext(null);
            tail = a;
        }

        public void print() {
            Instruction p = head.getNext();
            System.out.print(line + " : ");
            while (p != null) {
                p.print();
                System.out.print(" ");
                p=p.getNext();
            }
        }

        public Boolean overlap(Instruction a, Instruction b,Qubit[]qubits) {  //两条指令有重叠返回true，否则false
            if (!a.getIs_operation() || !b.getIs_operation()) return true;
            for (Integer id : a.getQubits()) {
                Qubit p=null;
                for (int i=0;i<qubits.length;i++){
                    if (qubits[i].getId()==id){
                        p= qubits[i];
                    }
                }
                if (p==null){
                    System.out.println("没有id为："+id+"的qubit");
                    return false;
                }
                for (Integer id1 : b.getQubits()){
                    Qubit q=null;
                    for (int i=0;i<qubits.length;i++){
                        if (qubits[i].getId()==id1){
                            q= qubits[i];
                        }
                    }
                    if (q==null){
                        System.out.println("没有id为："+id+"的qubit");
                        return false;
                    }
                    if (p == q) return true;
                }

            }
            return false;
        }

        public int levelize(Level tower[], Instruction codes[], Qubit qubits[]) {
            int lastline, i;
            int newline = 1;
            for (i = 2, lastline = 1; codes[lastline].getLine() != 0; i++) {
                int j;
                tower[newline].setIs_operation(codes[lastline].getIs_operation());
                for (j = lastline; j < i; j++) {
                    if (overlap(codes[i], codes[j],qubits)) break;
                }
                if (j < i || codes[i].getLine() == 0) {
                    for (j = lastline; j < i; j++) {
                        tower[newline].insert(codes[j]);
                        codes[j].setLine(newline);
                        //qubit的关联id也要变化

                    }
                    lastline = i;
                    tower[newline].line = newline;
                    newline++;
                }
            }

            return newline - 1;
        }

        public void del(Instruction a) {
            Instruction p = head;
            while (p.getNext() != null) {
                if (p.getNext() == a){
                    break;
                }
                p = p.getNext();
            }
            if (p.getNext() == null) {
                return;
            }
            if (p.getNext() == tail){
                tail = p;
            }
            p.setNext(p.getNext().getNext());
        }
        public  Instruction find_overlap_instruction(Set<Qubit> Q, Qubit []qubits) {
            Instruction p = head.getNext();
            while (p!=null) {
                for (Integer id : p.getQubits()) {
                    Qubit q=null;
                    for (int i=0;i<qubits.length;i++){
                        if (qubits[i].getId()==id){
                            q= qubits[i];
                        }
                    }
                    if (q==null){
                        System.out.println("没有id为："+id+"的qubit");
                    }
                    if (Q.contains(q)) return p;
                }
                p = p.getNext();
            }
            return null;
        }

    }
