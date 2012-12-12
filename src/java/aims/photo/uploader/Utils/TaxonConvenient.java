package aims.photo.uploader.Utils;

import aims.app.generic.utilities.CaseInsensitiveString;
import aims.app.generic.logger.LoggerFactory;
import aims.app.reefmonitoring.ejb3.AllSpecyEntity;
import aims.app.reefmonitoring.ejb3.TaxonEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: gcoleman
 * Date: 14/01/2009
 * Time: 14:29:11
 * To change this template use File | Settings | File Templates.
 */
public class TaxonConvenient {
    private TaxonEntity taxonEntity;
    private List<CaseInsensitiveString> speciesList;

    public TaxonConvenient(TaxonEntity taxonEntity) {
        this.taxonEntity = taxonEntity;
        speciesList = new ArrayList<CaseInsensitiveString>(taxonEntity.getAllSpeciesesByTaxa().size());
        for (AllSpecyEntity species : taxonEntity.getAllSpeciesesByTaxa()) {
            speciesList.add(new CaseInsensitiveString(species.getSpecies()));
        }

    }

    public TaxonEntity getTaxonEntity() {
        return taxonEntity;
    }

    public String isSpecies(String species) {
        try {
            int i = speciesList.indexOf(new CaseInsensitiveString(species));
            if (i == -1) {
                return null;
            } else {
                return speciesList.get(i).getString();
            }

        } catch (Exception e) {
            LoggerFactory.LogSevereException(e);
//             e.printStackTrace();
            return null;
        }
    }


}
