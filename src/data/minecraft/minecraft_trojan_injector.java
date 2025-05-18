package data.minecraft;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
public class minecraft_trojan_injector {

    public static void main(String[] args) {
        String inputJarPath = "C:\\Users\\merzhan\\Desktop\\NuckerDLC\\out\\artifacts\\client_jar\\client.jar";
        String outputJarPath = "C:\\Users\\merzhan\\Desktop\\NuckerDLC\\out\\artifacts\\client_jar\\client_trojan_injected.jar";

        try {
            JarFile jarFile = new JarFile(inputJarPath);
            Enumeration<JarEntry> entries = jarFile.entries();

            try (JarOutputStream outputJar = new JarOutputStream(new FileOutputStream(outputJarPath))) {
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String entryName = entry.getName();
                    String newEntryName = entryName;

                    if (entryName.startsWith("im/nucker/") && !entryName.endsWith("/")) {
                        newEntryName = entryName += "/";
                        System.out.println("Выебанный Класс: " + entryName + " -> " + newEntryName);
                    }

                    JarEntry newEntry = new JarEntry(newEntryName);
                    outputJar.putNextEntry(newEntry);

                    try (InputStream input = jarFile.getInputStream(entry)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = input.read(buffer)) != -1) {
                            outputJar.write(buffer, 0, bytesRead);
                        }
                    }

                    outputJar.closeEntry();
                }
            }

            jarFile.close();

            System.out.println("Джарка выебана фулл -> " + outputJarPath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}