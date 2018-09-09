package base.droidtool.dtlib;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import base.droidtool.adapters.ListCheckBoxAdapter;
import base.droidtool.model.ListCheckBox;


/**
 * Created by imrose on 5/26/2018.
 */

public class CheckList {

    Context c;
    RecyclerView listCheckBoxRecycler;
    ListCheckBoxAdapter listCheckBoxAdapter;
    ArrayList<ListCheckBox> checkBoxArrayList;

    public CheckList(Context _c) {
        c = _c;
    }

    public CheckList set(ArrayList<ListCheckBox> itemList) {
        checkBoxArrayList = itemList;
        return this;
    }

    public String getName(){
        return listCheckBoxRecycler.toString();
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        listCheckBoxRecycler = recyclerView;
        listCheckBoxRecycler.setLayoutManager(new LinearLayoutManager(c, LinearLayoutManager.VERTICAL, false));
        listCheckBoxAdapter = new ListCheckBoxAdapter(c, checkBoxArrayList);
        listCheckBoxRecycler.setAdapter(listCheckBoxAdapter);
    }

    public void setRecyclerView(RecyclerView recyclerView, int columnCount) {
        listCheckBoxRecycler = recyclerView;
        listCheckBoxRecycler.setLayoutManager(new GridLayoutManager(c, columnCount, LinearLayoutManager.VERTICAL, false));
        listCheckBoxAdapter = new ListCheckBoxAdapter(c, checkBoxArrayList);
        listCheckBoxRecycler.setAdapter(listCheckBoxAdapter);
    }

    public String getSelectedTextString(int index) {
        int val = 0;
        for (int i = 0; i < checkBoxArrayList.size(); i++) {
            ListCheckBox current = checkBoxArrayList.get(i);
            if (current.isCheckState()) val++;
        }
        return checkBoxArrayList.get(index).getCheckboxText() + " (মোট " + val + " বিষয়)";
    }

    public String getSelectedTextString() {
        String val = "";
        for (int i = 0; i < checkBoxArrayList.size(); i++) {
            ListCheckBox current = checkBoxArrayList.get(i);
            if (current.isCheckState()) {
                val = val + current.getCheckboxText() + ", ";
            }
        }
        if (val.isEmpty()) return val;
        else return val.substring(0, val.length() - 1);
    }

    public String getSelectedIndexString() {
        String val = "";
        for (int i = 0; i < checkBoxArrayList.size(); i++) {
            ListCheckBox current = checkBoxArrayList.get(i);
            if (current.isCheckState()) {
                val = val + i + ",";
            }
        }
        if (val.isEmpty()) return val;
        else return val.substring(0, val.length() - 1);
    }

    public ArrayList<ListCheckBox> setSelectedIndexString(String val) {
        String[] separated = val.split(",");
        for (int i = 0; i < checkBoxArrayList.size(); i++) {
            checkBoxArrayList.get(i).setCheckState(false);
        }
        ArrayList<ListCheckBox> retVal = checkBoxArrayList;
        if (!val.isEmpty()) {
            for (int i = 0; i < separated.length; i++) {
                ListCheckBox current = checkBoxArrayList.get(Integer.parseInt(separated[i]));
                current.setCheckState(true);
                retVal.set(Integer.parseInt(separated[i]), current);
            }
        }
        return retVal;
    }

    public ArrayList<ListCheckBox> setSelectedIndexString(ArrayList<ListCheckBox> checkBoxArrayList, String val) {
        String[] separated = val.split(",");
        for (int i = 0; i < checkBoxArrayList.size(); i++) {
            checkBoxArrayList.get(i).setCheckState(false);
        }
        ArrayList<ListCheckBox> retVal = checkBoxArrayList;
        if (!val.isEmpty()) {
            for (int i = 0; i < separated.length; i++) {
                ListCheckBox current = checkBoxArrayList.get(Integer.parseInt(separated[i]));
                current.setCheckState(true);
                retVal.set(Integer.parseInt(separated[i]), current);
            }
        }
        return retVal;
    }

    public String getSelectedTextString(ArrayList<ListCheckBox> checkBoxArrayList) {
        String val = "";
        for (int i = 0; i < checkBoxArrayList.size(); i++) {
            ListCheckBox current = checkBoxArrayList.get(i);
            if (current.isCheckState()) {
                val = val + current.getCheckboxText() + ", ";
            }
        }
        if (val.isEmpty()) return val;
        else return val.substring(0, val.length() - 1);
    }

}
