package installer.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Creates and Extracts Zip directories
 */
@SuppressWarnings("unused")
// TODO: Make this Utilities class into two classes 'ZipCreator' and 'ZipExtractor'
// TODO: There are files that should not be zipped such the Git and IntelliJ files
public class ZipUtils {
    // Variables
    private static final int  BUFFER_SIZE = 4096;

    // ----------------------- ZIPPING FILE ----------------------- //
    /***
     * Create zipfile to output directory with complete directory structure
     * @param directoryToZip Directory to zip
     */
    public static String createZip(File directoryToZip){
        return createZip(directoryToZip,null);
    }

    /***
     * Create zip file in the output directory named as zipName
     * @param directoryToZip Directory to zip
     * @param zipName Name of the zip file
     * @return Name of the zip file created
     */
    public static String createZip(File directoryToZip, String zipName){
        String fileName = null;
        try{
            List<File> fileList = new ArrayList<File>();
            getAllFiles(directoryToZip, fileList);
            fileName = writeZipFile(directoryToZip, fileList, zipName);
        } catch(Exception e){
            e.printStackTrace();
        }
        return fileName;
    }

    /*
     * Retrieve all files to be zipped
     */
    private static void getAllFiles(File dir, List<File> fileList) throws IOException{
        File[] files = dir.listFiles();
        if(files != null){
            for (File file : files) {
                fileList.add(file);
                if (file.isDirectory()) {
                    getAllFiles(file, fileList);
                }
            }
        }
    }

    /*
     * Write the files to a zip
     */
    private static String writeZipFile(File directoryToZip, List<File> fileList, String zipName) throws IOException{
        // If the zip name is null then provide the name of the directory
        if(zipName == null){
            zipName = directoryToZip.getName();
        }
        // Store the file name
        String fileName = zipName + ".zip";
        // Create the zip file
        FileOutputStream fos = new FileOutputStream(fileName);
        ZipOutputStream zos = new ZipOutputStream(fos);
        for (File file : fileList) {
            if (!file.isDirectory()) { // we only zip files, not directories
                addToZip(directoryToZip, file, zos);
            }
        }
        zos.close();
        fos.close();
        // Return the full name of the file
        return fileName;
    }

    /*
     * Add files to the zip
     */
    private static void addToZip(File directoryToZip, File file, ZipOutputStream zos) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        // we want the zipEntry's path to be a relative path that is relative
        // to the directory being zipped, so chop off the rest of the path
        String zipFilePath = file.getCanonicalPath().substring(directoryToZip.getCanonicalPath().length() + 1,
                file.getCanonicalPath().length());
        ZipEntry zipEntry = new ZipEntry(zipFilePath);
        zos.putNextEntry(zipEntry);
        byte[] bytes = new byte[BUFFER_SIZE];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zos.write(bytes, 0, length);
        }
        zos.closeEntry();
        fis.close();
    }

    // ----------------------- UNZIPPING FILE ----------------------- //
    /***
     * Extract zipfile to outdir with complete directory structure
     * @param zipfile Input .zip file
     * @param outputDirectory Output directory
     */
    public static boolean extract(File zipfile, File outputDirectory) {
        boolean isSuccessful = false;
        try {
            ZipInputStream zin = new ZipInputStream(new FileInputStream(zipfile));
            ZipEntry entry;
            String name, dir;
            while ((entry = zin.getNextEntry()) != null) {
                name = entry.getName();
                if (entry.isDirectory()) {
                    mkdirs(outputDirectory,name);
                    continue;
                }
                /* this part is necessary because file entry can come before
                * directory entry where is file located
                * i.e.:
                *   /foo/foo.txt
                *   /foo/
                */
                dir = dirpart(name);
                if (dir != null) {
                    mkdirs(outputDirectory,dir);
                }
                extractFile(zin, outputDirectory, name);
            }
            zin.close();
            // After the extraction is done - update the flag
            isSuccessful = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isSuccessful;
    }

    /*
     * Extract the zipped file name in the output directory
     * in - Zip file
     * outputDirectory - Directory to place the zipped file
     * name - name of the file to extract
     */
    private static void extractFile(ZipInputStream in, File outputDirectory, String name) throws IOException {
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
