package cn.ecnu.superposition.translate;

import java.util.Objects;

public class BooleanResult {

        private Boolean flag=false;
        private Integer index=0;
        private Integer total_qubits;

        public Integer getTotal_qubits() {
            return total_qubits;
        }

        public void setTotal_qubits(Integer total_qubits) {
            this.total_qubits = total_qubits;
        }

        public Boolean getFlag() {
            return flag;
        }

        public void setFlag(Boolean flag) {
            this.flag = flag;
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        @Override
        public String toString() {
            return "BooleanResult{" +
                    "flag=" + flag +
                    ", index=" + index +
                    ", total_qubits=" + total_qubits +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BooleanResult that = (BooleanResult) o;
            return Objects.equals(flag, that.flag) &&
                    Objects.equals(index, that.index) &&
                    Objects.equals(total_qubits, that.total_qubits);
        }

        @Override
        public int hashCode() {
            return Objects.hash(flag, index, total_qubits);
        }
    }

