package cn.ecnu.tabusearch.translate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Instruction {

        public static final Integer Operation = 0;
        public static final Integer Measure = 1;
        public static final Integer Decline = 2;
        private Boolean is_operation = false;
        private Boolean is_void = false;
        private List<Integer> qubits = new ArrayList<>();
        private List<String> prefix = new ArrayList<>();
        private String code = "";
        private int line = 0;
        private int mark = 0;
        private Instruction next = null;

        public static Integer getOperation() {
            return Operation;
        }

        public static Integer getMeasure() {
            return Measure;
        }

        public static Integer getDecline() {
            return Decline;
        }

        public Boolean getIs_operation() {
            return is_operation;
        }

        public void setIs_operation(Boolean is_operation) {
            this.is_operation = is_operation;
        }

        public Boolean getIs_void() {
            return is_void;
        }

        public void setIs_void(Boolean is_void) {
            this.is_void = is_void;
        }

        public List<Integer> getQubits() {
            return qubits;
        }

        public void setQubits(List<Integer> qubits) {
            this.qubits = qubits;
        }

        public List<String> getPrefix() {
            return prefix;
        }

        public void setPrefix(List<String> prefix) {
            this.prefix = prefix;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public int getLine() {
            return line;
        }

        public void setLine(int line) {
            this.line = line;
        }

        public int getMark() {
            return mark;
        }

        public void setMark(int mark) {
            this.mark = mark;
        }

        public Instruction getNext() {
            return next;
        }

        public void setNext(Instruction next) {
            this.next = next;
        }

        @Override
        public String toString() {
            return "Instruction{" +
                    ", is_operation=" + is_operation +
                    ", is_void=" + is_void +
                    ", qubits=" + qubits +
                    ", prefix=" + prefix +
                    ", code='" + code + '\'' +
                    ", line=" + line +
                    ", mark=" + mark +
                    ", next=" + next +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Instruction that = (Instruction) o;
            return line == that.line &&
                    mark == that.mark &&
                    Objects.equals(is_operation, that.is_operation) &&
                    Objects.equals(is_void, that.is_void) &&
                    Objects.equals(qubits, that.qubits) &&
                    Objects.equals(prefix, that.prefix) &&
                    Objects.equals(code, that.code) &&
                    Objects.equals(next, that.next);
        }

        @Override
        public int hashCode() {
            return Objects.hash( is_operation, is_void, qubits, prefix, code, line, mark, next);
        }

        public void print(PrintWriter writer) {
            //os << line << ": ";
            printCode(writer);
        }
        public void print() {
            //os << line << ": ";
            printCode();
        }
        public void printCode(PrintWriter writer) {
            if (is_void) {
                return;
            }
            writer.print(code);
            writer.print("\n");
            writer.flush();
        }
        public void printCode( ) {
            if (is_void) {
                return;
            }
            System.out.println(code);
        }
        public Boolean isEnd() {
            return code.equals( "end");
        }

        public Boolean isEmpty() {
            for (int i = 0; i < code.length(); i++) {
                if (code.charAt(i) != ' ') {
                    return false;
                }
            }
            return true;
        }

        // u: 当前已使用qubit数
        // line: 行号
        // qubits: qubit列表
        public BooleanResult input(int u, int line, Qubit[] qubits, BufferedReader in) {
            BooleanResult result=new BooleanResult();
            result.setIndex(line);
            result.setTotal_qubits(u);
            try {
                if ((code = in.readLine()) == null || isEnd()) {
                    result.setFlag(false);
                    return result;
                }
                if (isEmpty()) {
                    result.setFlag(true);
                    return result;
                }
                this.setLine(line);
                line++;
                result.setIndex(line);
                Integer pos = 0;
                StringResult strResult = getSubStr(pos);
                String prefix = strResult.getSubStr();
                pos = strResult.getPos();
                if (judge(prefix) != Operation) {  //判断是否为门操作
                    this.setIs_operation(false);
                    result.setFlag(true);
                    return result;
                }
                this.setIs_operation(true);
                while (pos < code.length()) {
                    StringResult result1 = getSubStr(pos);
                    String qub = result1.getSubStr();
                    pos = result1.getPos();
                    int tmp = hashi(qubits, qub);
                    if (qubits[tmp].getSign() == 0) {
                        qubits[tmp].setName(qub);
                        qubits[tmp].setSign(1);
                        u++;
                        result.setTotal_qubits(u);
                    }
                    qubits[tmp].insert(this);
                    insertQubit(qubits[tmp]);
                }
                result.setFlag(true);
                return result;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        public void insertQubit(Qubit q) {
            this.getQubits().add(q.getId());
        }

        private int hashi(Qubit[] qubits, String name) {
            int i, sum;
            for (i = 0; qubits[i].getSign() != 0; i++) {
                if (name.equals(qubits[i].getName())) {
                    return i;
                }
            }
            return i;
        }

        private Integer judge(String prefix) {
            if (prefix.equals("qreg") || prefix.equals("creg") ||
                    prefix.equals("OPENQASM") || prefix.equals("include")) {
                return Decline;
            } else if (prefix.equals("measure")) {
                return Measure;
            } else {
                return Operation;
            }

        }

        private StringResult getSubStr(Integer pos) {
            Integer start = pos;
            StringResult result = new StringResult();
            result.setStart(pos);
            result.setStr(code);
            while (pos < code.length() && code.charAt(pos) != ' ' && code.charAt(pos) != ',' && code.charAt(pos) != ';') {
                pos++;
            }
            Integer end = pos;
            do {
                pos++;
            } while (pos < code.length() && code.charAt(pos) == ' ');
            result.setPos(pos);
            result.setEnd(end);
            result.setSubStr(code.substring(start, end));
            return result;
        }

        //判断是否所有比特的mark位都不为1，是则返回true
        public Boolean judgeQubits(Qubit[]qubits1) {
            for (Integer id : qubits) {
                Qubit p=null;
                for (int i=0;i<qubits1.length;i++){
                    if (qubits1[i].getId()==id){
                        p= qubits1[i];
                    }
                }
                if (p == null) {
                    System.out.println("没有id为：" + id + "的qubit");
                    continue;
                }
                if (p.getMark() == 0)
                    return false;
            }
            return true;
        }

        public void setQubitsState(int state, Qubit[]qubits1) {
            for (Integer id : qubits) {
                Qubit p=null;
                for (int i=0;i<qubits1.length;i++){
                    if (qubits1[i].getId()==id){
                        p= qubits1[i];
                    }
                }
                if (p == null) {
                    System.out.println("没有id为：" + id + "的qubit");
                    continue;
                }
                p.setMark(state);
            }
        }

    }

