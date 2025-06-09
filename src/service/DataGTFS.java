package service;

import java.util.Comparator;
import java.util.List;

public record DataGTFS(List<DataRow> dataList) {

    public void add(DataRow rowMap) {
        this.dataList.add(rowMap);
    }

    public void sort(Comparator<DataRow> comparator) {
        this.dataList.sort(comparator);
    }

    public int size() {
        return this.dataList.size();
    }
    
    public void clear() {
    	this.dataList.clear();
    }
}
