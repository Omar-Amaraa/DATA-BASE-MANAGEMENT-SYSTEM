package org.example;

import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;


public class Record {
    List<Object> ListofValues  ;

    public Record(List<Object> listofValues) {
        ListofValues = new ArrayList<Object>();
    }

    public List<Object> getListofValues() {
        return ListofValues;
    }

    public void setListofValues(List<Object> listofValues) {
        ListofValues = listofValues;
    }
}
