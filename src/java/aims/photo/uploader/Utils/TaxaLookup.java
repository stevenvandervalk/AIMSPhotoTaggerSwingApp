package aims.photo.uploader.Utils;

import aims.app.generic.logger.LoggerFactory;
import aims.app.generic.utilities.CaseInsensitiveString;
import aims.app.generic.utilities.Utils;
import aims.app.reefmonitoring.ejb3.TaxonEntity;
import aims.app.reefmonitoring.model.GenusSpecies;

import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: gcoleman
 * Date: 2/07/2008
 * Time: 14:59:16
 * Last Updated: 14/12/2012
 * By: Timothy Hart
 * <p/>
 * <code>TaxaLookup</code> is used to retrieve data about <code>TaxonEntity</code> objects located in the specified bin file.
 * Once the data is retrieved a list of enitities is built while ensuring that all relevant data about an entity is maintained from file reading.
 * <p/>
 * <code>TaxaLookup</code> includes functionality to ensure that each entities heirachy in a taxonomic tree is present and fully expressed.
 * i.e. any relevant super taxa and sub taxa including any <code>AllSpecyEntity</code>.
 * <p/>
 * <code>TaxaLookup</code> uses class loading methods to obtain the file.
 *
 * @author Timothy Hart
 */
public class TaxaLookup {
    private static TaxaLookup ourInstance = new TaxaLookup();

    public static TaxaLookup getInstance() {
        return ourInstance;
    }

    private TaxaLookup() {
    }

    private ArrayList<TaxonEntity> list;
    private List<CaseInsensitiveString> caseInsensitiveList;
    private Map<String, TaxonConvenient> nameToTaxon;

    public void populate(String binFileName) {

        try {
            readFromXML(binFileName);
            LoggerFactory.LogInfo("Taxa List obtained from the local file.");

        } catch (Exception e) {
            throw new RuntimeException("ERROR!!! There was a problem loading the files. Taxonomy may not be present in the keyword list.", e);
        }


        nameToTaxon = new HashMap<String, TaxonConvenient>(list.size());
        caseInsensitiveList = new ArrayList<CaseInsensitiveString>(list.size());
        makeHashTable(list);


    }

    private void makeHashTable(Collection<TaxonEntity> taxonList) {
        if (taxonList != null) {
            for (TaxonEntity t : taxonList) {
                caseInsensitiveList.add(new CaseInsensitiveString(t.getTaxa()));
                nameToTaxon.put(t.getTaxa(), new TaxonConvenient(t));
                makeHashTable(t.getTaxonsByTaxa());

            }

        }
    }

    public GenusSpecies isSpecies(String genus, String species) {
        TaxonConvenient genusC = nameToTaxon.get(genus);
        if (genusC != null) {
            String species1 = genusC.isSpecies(species);
            if (species1 == null) {
                return null;
            } else {
                return new GenusSpecies(genusC.getTaxonEntity().getTaxa(), species1);

            }
        } else {
            return null;
        }
    }

    public String isTaxa(String s) {
        try {
            int i = caseInsensitiveList.indexOf(new CaseInsensitiveString(s));
            if (i == -1) {
                return null;
            } else {
                return caseInsensitiveList.get(i).getString();
            }

        } catch (Exception e) {
            LoggerFactory.LogSevereException(e);
            return null;
        }
    }

    public List<TaxonEntity> getList() {
        return list;
    }

    public void writeToXML() {
        ObjectOutputStream e = null;

        File dir = new File("c:/aims");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            e = new ObjectOutputStream(
                    new BufferedOutputStream(
                            new FileOutputStream("c:/aims/taxon.bin")));
            e.writeObject(list);
            e.close();

        } catch (Exception e2) {
            LoggerFactory.LogSevereException(e2);
        }
    }

    private void readFromXML(String binFileName) {
        try {
            ObjectInputStream d = null;
            d = new ObjectInputStream(
                    new BufferedInputStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("lookup_bins/" + binFileName)));
            list = (ArrayList) d.readObject();
            d.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String fullTaxaInfo(String taxa, String species) {
        TaxonEntity taxon = nameToTaxon.get(taxa).getTaxonEntity();
        String taxaInfo = fullTaxaInfo(taxon);
        if (!Utils.isNullOrEmpty(species)) {
            taxaInfo = taxaInfo + "!" + species;
        }

        return taxaInfo;

    }


    public static final String inBrackets(String s) {
        if (Utils.isNullOrEmpty(s)) {
            return "";
        } else {
            return "(" + s + ")";
        }
    }

    public String fullTaxaInfo(TaxonEntity taxon) {
        TaxonEntity superTaxa = taxon.getTaxonBySuperTaxa();
        if (superTaxa == null) {
            return "TAXONOMY!" + taxon.getTaxa() + inBrackets(taxon.getCommonNames());
        } else {
            return fullTaxaInfo(superTaxa) + "!" + taxon.getTaxa() + inBrackets(taxon.getCommonNames());
        }

    }

    public static void main(String[] args) {
        TaxaLookup.getInstance().populate("taxons");

// LoggerFactory.LogInfo(KeywordLookup.getInstance().getList().toString());
        System.exit(0);

    }


}