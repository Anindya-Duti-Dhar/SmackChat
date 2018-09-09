package base.droidtool.model;


public class CheckBoxDoubleValue {

    private String valueCode;
    private String valueText;
    private boolean isChecked;

    public String getValueCode() {
        return valueCode;
    }

    public void setValueCode(String valueCode) {
        this.valueCode = valueCode;
    }

    public String getValueText() {
        return valueText;
    }

    public void setValueText(String valueText) {
        this.valueText = valueText;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public CheckBoxDoubleValue(String valueCode, String valueText, boolean isChecked){
        this.valueCode = valueCode;
        this.valueText = valueText;
        this.isChecked = isChecked;

    }

}
