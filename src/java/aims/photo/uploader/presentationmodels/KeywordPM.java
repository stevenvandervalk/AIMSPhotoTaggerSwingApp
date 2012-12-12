package aims.photo.uploader.presentationmodels;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: gcoleman
 * Date: 16/12/2007
 * Time: 16:31:28
 * To change this template use File | Settings | File Templates.
 */
public class KeywordPM implements Serializable {
    String keyword;
    String shortName;

    public KeywordPM() {
    }

    public KeywordPM(String shortName, String keyword) {
        this.shortName = shortName;
        this.keyword = keyword;
    }

    public KeywordPM(String keyword) {

        this.shortName = makeShortName(keyword);
        this.keyword = keyword;
    }

    public static String makeShortName (String longName) {
        int i = longName.lastIndexOf('!');
        return longName.substring(i+1);
    }


    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public boolean equals (Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof KeywordPM)) {
            return false;
        }
        return this.getKeyword().equals(((KeywordPM)o).getKeyword());
    }

    public String toString() {
        return getShortName();
    }


    
}