package readerAdvisor.file;

/**
 * Created with IntelliJ IDEA.
 * User: Eduardo
 * Date: 5/4/13
 * Time: 9:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileSummary {
    private String fileName;
    private StringBuffer fileData;
    private int maximumWidth;
    private static final int WIDTH = 6;

    public FileSummary(){
    }

    public void setFileName(String fileName){
        this.fileName = fileName;
    }

    public void setFileData(StringBuffer fileData){
        this.fileData = fileData;
    }

    public void setMaximumWidth(int maximumWidth){
        this.maximumWidth = maximumWidth;
    }

    public String getFileName(){
        return this.fileName;
    }

    public StringBuffer getFileData(){
        return this.fileData;
    }

    public int getMaximumWidth(){
        return (this.maximumWidth * WIDTH);
    }
}
