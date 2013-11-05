package readerAdvisor.file.xml;

/**
 * Created with IntelliJ IDEA.
 * User: Eduardo
 * Date: 11/4/13
 * Time: 11:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class PropertyElement<E> {
    private String parent;
    private String name;
    private E value;

    public PropertyElement(){ }

    public PropertyElement(String parent){
        this.parent = parent;
    }

    public PropertyElement(String name, E value){
        this.name = name;
        this.value = value;
    }

    // Setters
    public void setParent(String parent){ this.parent = parent; }
    public void setName(String name){ this.name = name; }
    public void setValue(E value){ this.value = value; }
    // Getters
    public String getParent(){ return parent; }
    public String getName(){ return name; }
    public E getValue(){ return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PropertyElement)) return false;

        PropertyElement that = (PropertyElement) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (parent != null ? !parent.equals(that.parent) : that.parent != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = parent != null ? parent.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
