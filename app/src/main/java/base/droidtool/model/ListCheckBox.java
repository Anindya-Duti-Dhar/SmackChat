package base.droidtool.model;



public class ListCheckBox {

    private String CheckboxText;
    private boolean checkState;

    public String getCheckboxText() {
        return CheckboxText;
    }

    public void setCheckboxText(String checkboxText) {
        CheckboxText = checkboxText;
    }

    public boolean isCheckState() {
        return checkState;
    }

    public void setCheckState(boolean checkState) {
        this.checkState = checkState;
    }


    public ListCheckBox(String checkboxText, boolean checkState){
        this.CheckboxText = checkboxText;
        this.checkState = checkState;

    }

}
