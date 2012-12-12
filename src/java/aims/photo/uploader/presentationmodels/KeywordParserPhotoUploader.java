package aims.photo.uploader.presentationmodels;

import aims.app.reefmonitoring.model.GenusSpecies;
import aims.app.reefmonitoring.model.KeywordParserAbstract;
import aims.photo.uploader.Utils.KeywordLookup;
import aims.photo.uploader.Utils.TaxaLookup;

/**
 * Created by IntelliJ IDEA.
 * User: gcoleman
 * Date: 14/01/2009
 * Time: 13:20:58
 * To change this template use File | Settings | File Templates.
 */
public class KeywordParserPhotoUploader extends KeywordParserAbstract {


    private PhotoUploaderPM pm;

    public KeywordParserPhotoUploader(PhotoUploaderPM pm) {
        this.pm = pm;
    }

    protected String isKeyword(String s) {
        return KeywordLookup.getInstance().isKeyword(s);
    }

    protected GenusSpecies isSpecies(String genus, String species) {
        return TaxaLookup.getInstance().isSpecies(genus, species);
    }

    protected String isTaxa(String s) {
        String taxa = TaxaLookup.getInstance().isTaxa(s);
        return taxa;
    }

    protected void addTaxa(String genus, String species) {
        pm.AddKeyword(new KeywordPM(TaxaLookup.getInstance().fullTaxaInfo(genus, species)));
    }

    protected void addKeyword(String s) {
        pm.AddKeyword(new KeywordPM("KEYWORDS!" + s));
    }

    protected String getCaption() {
        return pm.getCaption();
    }
}
