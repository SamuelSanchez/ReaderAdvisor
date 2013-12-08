package installer.utils;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@SuppressWarnings("unused")
public class UncompressZipFile {

    private List<String> zipEntryList = new LinkedList<String>();
    private File outputDirectory;
    private ZipInputStream zin;

    public UncompressZipFile(InputStream zipFile) throws IOException{
        // Clone the input stream and retrieve two clones
        List<InputStream> inputStreamList = InstallerUtils.cloneInputStream(zipFile);
        // First clone to unzip the files
        zin = new ZipInputStream(inputStreamList.get(0));
        // Second clone to retrieve the count information of this zip
        init(inputStreamList.get(1));
    }

    public UncompressZipFile(InputStream zipFile, File outputDirectory) throws IOException{
        // Clone the input stream and retrieve two clones
        List<InputStream> inputStreamList = InstallerUtils.cloneInputStream(zipFile);
        // First clone to unzip the files
        zin = new ZipInputStream(inputStreamList.get(0));
        this.outputDirectory = outputDirectory;
        // Second clone to retrieve the count information of this zip
        init(inputStreamList.get(1));
    }

    public void setOutputDirectory(File outputDirectory){
        this.outputDirectory = outputDirectory;
    }


    /**
     * Read all the files in this zip
     */

    private void init(InputStream inputStream) throws IOException {
        ZipInputStream _zin = new ZipInputStream(inputStream);
        ZipEntry entry;
        // Store all the files
        while ((entry = _zin.getNextEntry()) != null) {
            zipEntryList.add(entry.getName());
        }
        _zin.close();
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
        return zipEntryList;
    }

    /***
     * Unzip the next file or directory
     * @return Name of the unzip file. Null if there are no more files.
     */

    public synchronized String unzipNextEntry() throws IOException {
        String fileName = null;
        ZipEntry entry;
        // Do not proceed if there's not file to unzip
        if((entry = zin.getNextEntry()) != null){
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
        return fileName;
    }

    /*
     * Extract the zipped file name in the output directory
     * in - Zip file
     * outputDirectory - Directory to place the zipped file
     * name - name of the file to extract
     */
    private static void extractFile(ZipInputStream in, File outputDirectory, String name) throws IOException {
        final int  BUFFER_SIZE = 1024;
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
