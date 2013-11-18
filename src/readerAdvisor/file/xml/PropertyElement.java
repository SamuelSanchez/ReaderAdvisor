package readerAdvisor.file.xml;

@SuppressWarnings("unused")
public class PropertyElement {
    private String parent;
    private String name;
    private String value;

    public PropertyElement(){ }

    public PropertyElement(String parent){
        this.parent = parent;
    }

    // Setters
    public void setParent(String parent){ this.parent = parent; }
    public void setName(String name){ this.name = name; }
    public void setValue(String value){ this.value = value; }
    // Getters
    public String getParent(){ return parent; }
    public String getName(){ return name; }
    public String getValue(){ return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PropertyElement)) return false;

        PropertyElement that = (PropertyElement) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (parent != null ? !parent.equals(that.parent) : that.parent != null) return false;

        return true;
    }

    public String toString(){
        return name;
    }

    @Override
    public int hashCode() {
        int result = parent != null ? parent.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
