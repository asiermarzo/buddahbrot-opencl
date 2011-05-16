/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.infinityk.experimental.buddah;

import java.awt.Component;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Asier
 */
public class FileUtils {
  private static File lastChooserPath;
  private static File lastIndicatedPath;

  public static String selectFile(Component component, String name,FileFilter filter, File pathToUse){
        JFileChooser chooser = new JFileChooser();
        if(filter != null){
            chooser.setFileFilter(filter);
        }
        boolean useIndicated = pathToUse != null;
        if (useIndicated && lastIndicatedPath == null){
            lastIndicatedPath = pathToUse;
        }
        chooser.setCurrentDirectory(useIndicated?lastIndicatedPath:lastChooserPath);
	int result = chooser.showDialog(component, name);
        if ( result == JFileChooser.APPROVE_OPTION){
            try{
                if(useIndicated){
                    lastIndicatedPath = chooser.getCurrentDirectory();
                }else{
                    lastChooserPath = chooser.getCurrentDirectory();
                }
                return chooser.getSelectedFile().getAbsolutePath();
            }catch(Exception e){e.printStackTrace();}
        }

        return null;
    }


  public static String[] selectFiles(Component component,String name,FileFilter filter,File pathToUse){
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        if(filter != null){
            chooser.setFileFilter(filter);
        }
        boolean useIndicated = pathToUse != null;
        if (useIndicated && lastIndicatedPath == null){
            lastIndicatedPath = pathToUse;
        }

        chooser.setCurrentDirectory(useIndicated?lastIndicatedPath:lastChooserPath);
	int result = chooser.showDialog(component, name);
        if ( result == JFileChooser.APPROVE_OPTION){
            try{
                if(useIndicated){
                    lastIndicatedPath = chooser.getCurrentDirectory();
                }else{
                    lastChooserPath = chooser.getCurrentDirectory();
                }

                File[] files = chooser.getSelectedFiles();
                String[] forReturn = new String[files.length];
                for(int i = 0; i < files.length; i++){
                    forReturn[i] = files[i].getAbsolutePath();
                }
                return forReturn;
            }catch(Exception e){e.printStackTrace();}
        }

        return null;
    }

    public static String selectDirectory(Component component,String name,File pathToUse){

        JFileChooser chooser = new JFileChooser();
        boolean useIndicated = pathToUse != null;
        if (useIndicated && lastIndicatedPath == null){
            lastIndicatedPath = pathToUse;
        }

        chooser.setCurrentDirectory(useIndicated?lastIndicatedPath:lastChooserPath);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	int result = chooser.showDialog(component, name);
        if ( result == JFileChooser.APPROVE_OPTION){
            try{
                if(useIndicated){
                    lastIndicatedPath = chooser.getCurrentDirectory();
                }else{
                    lastChooserPath = chooser.getCurrentDirectory();
                }
                return chooser.getSelectedFile().getAbsolutePath();
            }catch(Exception e){e.printStackTrace();}
        }
        return null;
    }


     
      public static String selectNonExistingFile(Component parent,String extensionWanted){
        String forReturn = null;
        final String endWith = extensionWanted;

        JFileChooser chooser = new JFileChooser(lastChooserPath);
        chooser.setFileFilter(new FileFilter(){
				public boolean accept(File file) {
					String filename = file.getName();
					return (filename.endsWith(endWith)||file.isDirectory());
				}
				public String getDescription() {
					return endWith;
				}
			});
	int result = chooser.showSaveDialog(parent);
        if ( result == JFileChooser.APPROVE_OPTION){
             try{
                lastChooserPath = chooser.getCurrentDirectory();
                forReturn = chooser.getSelectedFile().getCanonicalPath();
            }catch(Exception e){e.printStackTrace();}
        }
        if(forReturn != null){
            if(!forReturn.endsWith(extensionWanted)){
                forReturn += extensionWanted;
            }
        }
        return forReturn;
    }

    public static String getAsRelative(File prefixPath, String path) throws IOException {
        if (prefixPath == null) {
            return path;
        }
        File filePath = new File(path);
        if(!filePath.isAbsolute()) { return path; }

        String a = prefixPath.getCanonicalFile().toURI().getPath();
        String b = filePath.getCanonicalFile().toURI().getPath();
        String[] basePaths = a.split("/");
        String[] otherPaths = b.split("/");

        int lastIndex = 0;
        for (int n = 0; n < basePaths.length && n < otherPaths.length; n++) {
            lastIndex = n;
            if (!basePaths[n].equals(otherPaths[n])) {
                break;
            }
        }
        if (lastIndex < basePaths.length-1){
            return path;
        }else{
            StringBuilder sb = new StringBuilder();
            for(int m = lastIndex+1; m < otherPaths.length-1; m++){
                sb.append(otherPaths[m]);
                sb.append("/");
            }
            sb.append(otherPaths[otherPaths.length-1]);
            return sb.toString();
        }
    }

    public static String calculatePath(File prefixPath, String path){
        File f = new File(path);
        if (f.isAbsolute()) { return path; }
        else {
            return new File(prefixPath, path).getAbsolutePath();
        }
    }

    public static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        // Get the size of the file
        long length = file.length();

        if (length > Integer.MAX_VALUE) {
            // File is too large
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }

        // Close the input stream and return bytes
        is.close();
        return bytes;
    }

    public static void writeBytesInFile(File f, byte[] data) throws FileNotFoundException, IOException{
        FileOutputStream fos = new FileOutputStream(f, false);
        fos.write(data);
        fos.close();
    }

    public static File cloneFileInTemp(File f){
        FileOutputStream fos = null;
        FileInputStream fis = null;
        try{
            byte[] buffer = new byte[1024];
            int len;
            File forReturn = File.createTempFile("KDD", ".jar");
            forReturn.deleteOnExit();
            fos = new FileOutputStream(forReturn, false);
            fis = new FileInputStream(f);
            while ((len = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            return forReturn;
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try{ if(fos != null) {fos.close();}}catch(Exception e){}
            try{ if(fis != null) {fis.close();}}catch(Exception e){}
        }
        return null;
    }

    public static byte[] objectToXml(Object obj){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        XMLEncoder xml = new XMLEncoder(bos);
        xml.writeObject(obj);
        xml.close();
        return bos.toByteArray();
    }

    public static byte[] objectToXml(Object obj,ClassLoader cl){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        XMLEncoder xml = new XMLEncoder(bos);
        //HACK
        ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        if (cl != oldLoader && cl != null){
            try{
                Thread.currentThread().setContextClassLoader(cl);
                xml.writeObject(obj);
            }catch(Exception e){e.printStackTrace();}
            finally {
                Thread.currentThread().setContextClassLoader(oldLoader);
            }
        }else{
            xml.writeObject(obj);
        }

        xml.close();
        return bos.toByteArray();
    }

    public static Object xmlToObject(byte[] data){
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        XMLDecoder xml = new XMLDecoder(bis);
        return xml.readObject();
    }

    public static Object xmlToObject(byte[] data,ClassLoader cl){
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        XMLDecoder xml = new XMLDecoder(bis, null, null, cl);
        return xml.readObject();
    }

    public static String lastNameOfPath(String path){
        if(path == null) { return null; }
        int length = path.length();
        if(length == 0) { return path; }
        int index = Math.max(path.lastIndexOf("\\"), path.lastIndexOf("/"));
        if(index == length-1){
            path = path.substring(0, index);
            index = Math.max(path.lastIndexOf("\\"), path.lastIndexOf("/"));
        }

        if (index != -1){
            return path.substring(index+1);
        }else{
            return path;
        }
    }

     // <editor-fold defaultstate="collapsed" desc="filters">
        public static final FileFilter imagesFilter = new FileFilter() {

            public boolean accept(File file) {
                String filename = file.getName().toLowerCase();
                return (filename.endsWith(".psd")
                        || file.isDirectory());
            }

            public String getDescription() {
                return "PSD";
            }
        };

        // </editor-fold>

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String MD5(byte[] data)
    throws NoSuchAlgorithmException, UnsupportedEncodingException  {
        MessageDigest md;
        md = MessageDigest.getInstance("MD5");
        byte[] md5hash = new byte[32];
        md.update(data, 0, data.length);
        md5hash = md.digest();
        return convertToHex(md5hash);
    }

    public static byte[] ungzip(byte[] bytes) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        InflaterInputStream iis = new InflaterInputStream(bis, new Inflater(true));
        byte[] buffer = new byte[1024];
        int readed;
        while( (readed = iis.read(buffer)) > 0){
            bos.write(buffer, 0, readed);
        }
        return bos.toByteArray();
    }

    public static byte[] gzip(byte[] bytes) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DeflaterOutputStream dos = new DeflaterOutputStream(bos, new Deflater(Deflater.DEFAULT_COMPRESSION, true));
        dos.write(bytes);
        dos.close();
        return bos.toByteArray();
    }

}
