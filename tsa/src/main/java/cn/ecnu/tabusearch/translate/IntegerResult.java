package cn.ecnu.tabusearch.translate;

import java.util.Objects;

public class IntegerResult {
        private Integer total_qubit;
        private  Integer total_instruction;

        public Integer getTotal_qubit() {
            return total_qubit;
        }

        public void setTotal_qubit(Integer total_qubit) {
            this.total_qubit = total_qubit;
        }

        public Integer getTotal_instruction() {
            return total_instruction;
        }

        public void setTotal_instruction(Integer total_instruction) {
            this.total_instruction = total_instruction;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            IntegerResult that = (IntegerResult) o;
            return Objects.equals(total_qubit, that.total_qubit) &&
                    Objects.equals(total_instruction, that.total_instruction);
        }

        @Override
        public int hashCode() {
            return Objects.hash(total_qubit, total_instruction);
        }

        @Override
        public String toString() {
            return "IntegerResult{" +
                    "total_qubit=" + total_qubit +
                    ", total_instruction=" + total_instruction +
                    '}';
        }
    }

