package installer.utils;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@SuppressWarnings("unused")
public class UncompressZipFile {

    private List<ZipEntry> zipEntryList = new LinkedList<ZipEntry>();
    private Iterator<ZipEntry> iterator;
    private InputStream zipFile;
    private File outputDirectory;
    private ZipInputStream zin;

    public UncompressZipFile(InputStream zipFile) throws IOException{
        this.zipFile = zipFile;
        // Initialize this class
        init();
    }

    public UncompressZipFile(InputStream zipFile, File outputDirectory) throws IOException{
        this.zipFile = zipFile;
        this.outputDirectory = outputDirectory;
        // Initialize this class
        init();
    }

    public void setZipFile(InputStream zipFile){
        this.zipFile = zipFile;
    }

    public void setOutputDirectory(File outputDirectory){
        this.outputDirectory = outputDirectory;
    }


    /**
     * Read all the files in this zip
     */

    private void init() throws IOException {
        zin = new ZipInputStream(zipFile);
        ZipEntry entry;
        // Store all the files
        while ((entry = zin.getNextEntry()) != null) {
            zipEntryList.add(entry);
        }
        // Retrieve iterator
        iterator = zipEntryList.iterator();
        //zin.close();
    }


    /**
     *  Count the number of files in the zip directory
     *  @return The number of files in the zip file. -1 if the zip file is null.
     */

    public synchronized int countFiles(){
        return zipEntryList.size();
    }


    /**
     *  List the files in the zip directory
     *  @return List the files in the zip file.
     */

    public synchronized List<String> listFiles(){
        List<String> list = new LinkedList<String>();
        // Retrieve the name of the zip entries
        for(ZipEntry zipEntry : zipEntryList){
            list.add(zipEntry.getName());
        }
        return list;
    }

    /***
     * Unzip the next file or directory
     * @return Name of the unzip file. Null if there are no more files.
     */

    public synchronized String unzipNextEntry() throws IOException {
        String fileName = null;
        // Do not proceed if there's not file to unzip
        if(iterator.hasNext()){
            ZipEntry entry = iterator.next();
            if (entry != null) {
                // Update the name of the file
                fileName = entry.getName();
                // If the file is a directory then create it
                if (entry.isDirectory()) {
                    mkdirs(outputDirectory,fileName);
                }
                else{
                    String dir = dirpart(fileName);
                    if (dir != null) {
                        mkdirs(outputDirectory,dir);
                    }
                    extractFile(zin, outputDirectory, fileName);
                }
            }
        }
        return fileName;
    }

    /*
     * Extract the zipped file name in the output directory
     * in - Zip file
     * outputDirectory - Directory to place the zipped file
     * name - name of the file to extract
     */
    private static void extractFile(ZipInputStream in, File outputDirectory, String name) throws IOException {
        final int  BUFFER_SIZE = 4096;
        byte[] buffer = new byte[BUFFER_SIZE];
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(outputDirectory,name)));
        int count;
        while ((count = in.read(buffer)) != -1) {
            out.write(buffer, 0, count);
        }
        out.close();
    }

    /*
     * Make Directory in the given path
     * outputDirectory - Directory path where the file will be created
     * path - directory to be created
     */
    private static void mkdirs(File outputDirectory,String path) {
        File d = new File(outputDirectory, path);
        if (!d.exists()) {
            d.mkdirs();
        }
    }

    /*
     * Find out if the name belongs to a directory or a file
     * If it's part of a directory return the name of the directory
     * If the name belongs to a file return null
     */
    private static String dirpart(String name) {
        int s = name.lastIndexOf( File.separatorChar );
        return s == -1 ? null : name.substring( 0, s );
    }
}
