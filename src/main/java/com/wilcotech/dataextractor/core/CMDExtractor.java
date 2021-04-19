package com.wilcotech.dataextractor.core;

/**
 * Created by Wilson
 * on Sat, 17/04/2021.
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

    public class CMDExtractor {
        public static void main(String[] args) throws Exception {
            ProcessBuilder builder = new ProcessBuilder(
                    "cmd.exe", "/c", "cd \"C:\\Users\\wilson\\Documents\\research project\\Eclipse Birt\\birt\" && " +
                    " git whatchanged --author=\"cvs2svn\" --diff-filter=A --no-commit-id --name-only --all");
            builder.redirectErrorStream(true);

            Process p = builder.start();
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            int  n =1000;
            //Stack<Character> st = new Stack<>();
            //String ans =st.stream().map(String::valueOf).collect(Collectors.joining());
            Set<String> files = new HashSet<>();
            while (true) {
                line = r.readLine();
                if (line == null) { break; }
                if(line.endsWith(".java"))
                    files.add(line);
            }
            Map<String,Set<String>> content = new HashMap<>();
            Set<String> con;
            Path path;
            BufferedReader bf;
            String lin;
            for(String file: files){
                path = Paths.get("C:\\Users\\wilson\\Documents\\research project\\Eclipse Birt\\birt\\"+file);
                bf = Files.newBufferedReader(path);
                lin = bf.readLine();
            /*
            Stream<String> lines = Files.lines(path);
            String data = lines.collect(Collectors.joining("\n"));
            lines.close();
             */
                con = new HashSet<>();
                //System.out.println("ye ys yes yes yes ys 1");
                while(lin != null){
                    //System.out.println("ye ys yes yes yes ys 2 "+lin);
                    if(!lin.contains("import")){
                        lin = bf.readLine();
                        continue;
                    }
                    if(lin.contains(" class "))break;
                    int start = lin.indexOf("import")+6;
                    con.add(lin.substring(start));
                    lin = bf.readLine();
                }
                //System.out.println("3 3 3 3");
                content.put(file,con);
            }
            int max = 1;
            String key;
            Set<String> keys = content.keySet();
            Iterator<String> it = keys.iterator();
            while(it.hasNext()){
                key = it.next();
                if(max++ == 101)break;
                System.out.printf("key-> %s\n Content -> %s\n\n",key,
                        content.get(key).stream().collect(Collectors.joining("|")));
            }
        }
    }
