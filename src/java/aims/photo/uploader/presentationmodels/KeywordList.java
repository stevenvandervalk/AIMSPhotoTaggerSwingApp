/**
 * Created by IntelliJ IDEA.
 * User: gcoleman
 * Date: 17/12/2007
 * Time: 17:56:00
 * To change this template use File | Settings | File Templates.
 */
package aims.photo.uploader.presentationmodels;

import aims.app.generic.db.DatabaseList;
import aims.app.generic.dbgui.DatabaseGuiList;

import java.util.HashSet;
import java.util.Set;


public class KeywordList extends DatabaseGuiList {
    private static final String SQL = "from ";
    private KeywordListListener listener;



    public KeywordList() {
        super(DatabaseGuiList.DATABASE_NONE, DatabaseGuiList.GUI_SWING, KeywordPM.class);
    }

    public KeywordPM getCurrent() {
        return (KeywordPM) super.getCurrent();
    }

    public KeywordPM getLast() {
        return (KeywordPM) super.getLast();
    }

    public int add() {
//        KeywordPM k = new KeywordPM();
//        return super.addObject(k);
        return DatabaseList.DID_NOTHING;
    }

    public int add(KeywordPM k ) {

//        KeywordPM k = new KeywordPM();
//        k.setKeyword(s);
        if (getDatabaseList().getList().contains(k)){
          return DatabaseList.DID_NOTHING;
        } else {
            return super.addObject(k);

        }
    }

    public void setList(Set<KeywordPM> s) {
        clear();
        if (s!= null) {
            for (KeywordPM k : s) {
                add(k);
            }
            
        }
    }

    public Set<KeywordPM> getList() {
        Set<KeywordPM> set = new HashSet<KeywordPM>();
        boolean ok = first();
        while (ok) {
            set.add(getCurrent());
            ok = next();
        }

        return set;
    }

    public String getDelimitedString() {

        String out = "";
        String comma = "";
        boolean ok = first();
        while (ok) {
            out = out + comma + getCurrent().getShortName();
            comma = ", ";
            ok = next();
        }
        return out;

    }

    public void delete() {
        String k = getCurrent().getKeyword();
        super.delete();
        if (listener!= null) {
            listener.deleteKeyword(k);
        }
    }

    public void setListener(KeywordListListener listener) {
        this.listener = listener;
    }
}
