/*
 * NewsTerp Engine - We report.  You decipher.
 * copyright (c) 2007 Colin Bayer, Jack Hebert
 *
 * CSE 472 Spring 2007 final project
 */



import java.io.*;
import java.util.*;


public class NewsRepoReader {

    private ArrayList<String> newsFiles;
    private int fileIndex;
    private BufferedReader reader;


    public NewsRepoReader(String filePath) {
	this.newsFiles = new ArrayList<String>();
	this.AddFiles(filePath);
	this.fileIndex = 0;
	try{
	    if(this.newsFiles.size() > 0) {
		this.reader = new BufferedReader(new FileReader(
			this.newsFiles.get(this.fileIndex)));
	    } else {
		this.reader = null;
	    }
	} catch(Exception e) {
	    System.out.println(e.toString());
	}
	System.out.println("Found: "+this.newsFiles.size()+" files.");
    }

    private void AddFiles(String path) {
	File input = new File(path);
	if(! input.isDirectory()) {
	    this.newsFiles.add(path);
	} else {
	    String[] children = input.list();
	    for(int i=0; i<children.length; i++) {
		String name = children[i];
		if(!name.startsWith("."))
		    this.AddFiles(path+"/"+name);
	    }
	}
    }

    public NewsRepoArticle GetNextArticle(){
	// read the next line of the current file
	// if at the end of file, move to the next file
	// if at end of list of files, return null;
	try {
	    String line = this.reader.readLine();
	    while(line == null) {
		if(this.fileIndex < this.newsFiles.size()) {
		    this.fileIndex += 1;
		    this.reader = new BufferedReader(new FileReader(
								    this.newsFiles.get(this.fileIndex)));
		} else {
		    return null;
		}
		line = this.reader.readLine();
	    }
	    return new NewsRepoArticle(line);
	} catch(Exception e) {
	    System.out.println(e.toString());
	}
	return null;
    }

    public int GetNumberOfArticle() {
	return this.newsFiles.size();
    }

    
    /*
    public static void main(String[]  args){
    	String path = "../fetched-pages/html/";
    	NewsRepoReader rpo = new NewsRepoReader(path);
	NewsRepoArticle art = rpo.GetNextArticle();
	System.out.println(art.getUrl());
	System.out.println(art.getArticle());
	System.out.println(art.getLine());
    }
    */

}
