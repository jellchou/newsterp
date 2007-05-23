/*
 * NewsTerp Engine - We report.  You decipher.
 * copyright (c) 2007 Colin Bayer, Jack Hebert
 *
 * CSE 472 Spring 2007 final project
 */


public class NewsRepoArticle {

    private String url;
    private String articleText;

    public NewsRepoArticle(String line) {
	int index = line.indexOf(' ');
	if(index == -1) {
	    this.url = "";
	    this.articleText = "";
	} else {
	    this.url = line.substring(0, index);
	    this.articleText = line.substring(index+1);
	}
    }

    public String getUrl() {
	return this.url;
    }

    public String getArticle() {
	return this.articleText;
    }


}
