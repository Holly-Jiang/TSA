package cn.ecnu.superposition.translate;

import java.util.Objects;

public class StringResult {
        private String str;
        private String subStr;
        private Integer start;
        private Integer end;
        private Integer pos;

        public String getStr() {
            return str;
        }

        public void setStr(String str) {
            this.str = str;
        }

        public String getSubStr() {
            return subStr;
        }

        public void setSubStr(String subStr) {
            this.subStr = subStr;
        }

        public Integer getStart() {
            return start;
        }

        public void setStart(Integer start) {
            this.start = start;
        }

        public Integer getEnd() {
            return end;
        }

        public void setEnd(Integer end) {
            this.end = end;
        }

        public Integer getPos() {
            return pos;
        }

        public void setPos(Integer pos) {
            this.pos = pos;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StringResult result = (StringResult) o;
            return Objects.equals(str, result.str) &&
                    Objects.equals(subStr, result.subStr) &&
                    Objects.equals(start, result.start) &&
                    Objects.equals(end, result.end) &&
                    Objects.equals(pos, result.pos);
        }

        @Override
        public int hashCode() {
            return Objects.hash(str, subStr, start, end, pos);
        }

        @Override
        public String toString() {
            return "ResultUtil{" +
                    "str='" + str + '\'' +
                    ", subStr='" + subStr + '\'' +
                    ", start=" + start +
                    ", end=" + end +
                    ", pos=" + pos +
                    '}';
        }
    }
