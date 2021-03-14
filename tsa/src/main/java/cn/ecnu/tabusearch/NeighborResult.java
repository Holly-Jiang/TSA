package cn.ecnu.tabusearch;

import cn.ecnu.tabusearch.swaps.Gate;

import java.util.List;
import java.util.Objects;

public class NeighborResult {
    List<Solution> solutions;
    List<List<Edge>>  choose_path;
    Integer current_num=0;
    List<Gate> curr_solved_gates;

    public List<Gate> getCurr_solved_gates() {
        return curr_solved_gates;
    }

    public void setCurr_solved_gates(List<Gate> curr_solved_gates) {
        this.curr_solved_gates = curr_solved_gates;
    }

    public Integer getCurrent_num() {
        return current_num;
    }

    public void setCurrent_num(Integer current_num) {
        this.current_num = current_num;
    }

    public List<Solution> getSolutions() {
        return solutions;
    }

    public void setSolutions(List<Solution> solutions) {
        this.solutions = solutions;
    }

    public List<List<Edge>> getChoose_path() {
        return choose_path;
    }

    public void setChoose_path(List<List<Edge>> choose_path) {
        this.choose_path = choose_path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NeighborResult that = (NeighborResult) o;
        return Objects.equals(solutions, that.solutions) &&
                Objects.equals(choose_path, that.choose_path) &&
                Objects.equals(current_num, that.current_num) &&
                Objects.equals(curr_solved_gates, that.curr_solved_gates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(solutions, choose_path, current_num, curr_solved_gates);
    }

    @Override
    public String toString() {
        return "NeighborResult{" +
                "solutions=" + solutions +
                ", choose_path=" + choose_path +
                ", all_gates=" + current_num +
                ", curr_solved_gates=" + curr_solved_gates +
                '}';
    }
}
