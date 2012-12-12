package aims.photo.uploader.tablemodels;

import aims.app.generic.logger.LoggerFactory;

import aims.app.reefmonitoring.ejb3.*;
import aims.photo.uploader.Utils.KeywordLookup;
import aims.photo.uploader.Utils.LocationLookup;
import aims.photo.uploader.Utils.TaxaLookup;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: gcoleman
 * Date: 16/01/2008
 * Time: 11:35:20
 * To change this template use File | Settings | File Templates.
 */
public class KeywordTree {
    private DefaultMutableTreeNode rootNode;
    private DefaultMutableTreeNode keywordRoot;


    public static String getStringPath (DefaultMutableTreeNode node) {
        Object path[] = node.getUserObjectPath();
        String out = "";
        String delimiter = "";

        for (int i=1; i<path.length; i++) {
            Object category = path[i];
            out = out + delimiter + category.toString();
            delimiter = "!";
        }
        return out;
    }

    public KeywordTree() {
        rootNode = new DefaultMutableTreeNode("Categories");
        addProjects();


    }

    public void addProjects() {
        DefaultMutableTreeNode projectRoot = new DefaultMutableTreeNode("PROJECT");
        projectRoot.add(new DefaultMutableTreeNode("RM"));
        projectRoot.add(new DefaultMutableTreeNode("CLIMATE-CHANGE"));
        rootNode.add(projectRoot);
    }

    public void addKeywords() {

        keywordRoot = buildKeywordNode();
        rootNode.add(keywordRoot);

    }

    public void addTaxa() {
        rootNode.add(buildTaxonNode());
        
    }

    public void addLocation() {
        rootNode.add(buildLocationNode());

    }
    private DefaultMutableTreeNode buildKeywordNode() {
        LoggerFactory.LogInfo("buildKeywordNode");
        DefaultMutableTreeNode k = new DefaultMutableTreeNode("KEYWORDS");
        KeywordLookup.getInstance().populate();

        for (Keywords keyword : KeywordLookup.getInstance().getList()) {
            try {
                k.add(new DefaultMutableTreeNode(keyword.getKeyword()));
//                LoggerFactory.LogInfo(keyword.getKeyword());
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        return k;
    }



    private DefaultMutableTreeNode buildLocationNode() {
        LoggerFactory.LogInfo("buildLocationNode");
        DefaultMutableTreeNode locationRoot = new DefaultMutableTreeNode("LOCATION");
        LocationLookup locationLookup = LocationLookup.getInstance();
        locationLookup.populate();
        DefaultMutableTreeNode locationNode = new DefaultMutableTreeNode("DUMMY");


        List<RmSectorEntity> sectorEntities =  LocationLookup.getInstance().getList();
        Collections.sort(sectorEntities);
        for (RmSectorEntity sector: sectorEntities) {
            try {

                if (sector.getLocation()!=null && !sector.getLocation().equals(locationNode.getUserObject())) {
                    locationNode = new  DefaultMutableTreeNode(sector.getLocation());
                    locationRoot.add(locationNode);
                }

                DefaultMutableTreeNode sectorNode = new DefaultMutableTreeNode(sector.getSectorName());
                locationNode.add(sectorNode);
                List<ReefGeolocale> reefGeolocaleCollection = sector.getReefGeolocalesByASector();
                Collections.sort(reefGeolocaleCollection);
                for (ReefGeolocale reef : reefGeolocaleCollection) {
                    //ReefPM pReef = new ReefPM(reef);
//                    sectorNode.add(new DefaultMutableTreeNode(reef.getReefName() + "!" + pReef.getFormattedReefId()));
                    sectorNode.add(new DefaultMutableTreeNode(reef.getReefName()));
                }

            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        
        return locationRoot;
    }

    public DefaultMutableTreeNode getRootNode() {
        return rootNode;
    }

    public void setRootNode(DefaultMutableTreeNode rootNode) {
        this.rootNode = rootNode;
    }


    private DefaultMutableTreeNode buildTaxonNode() {
        LoggerFactory.LogInfo("buildTaxaNode");
        DefaultMutableTreeNode taxonomyNode = new DefaultMutableTreeNode("TAXONOMY");
        TaxaLookup.getInstance().populate("taxons.bin");
        for (TaxonEntity taxa: TaxaLookup.getInstance().getList()) {
            addTaxa(taxonomyNode, taxa);
        }

        TaxaLookup.getInstance().populate("echinoderm.bin");
        for (TaxonEntity taxa: TaxaLookup.getInstance().getList()) {
            addTaxa(taxonomyNode, taxa);
        }

        TaxaLookup.getInstance().populate("decapoda.bin");
        for (TaxonEntity taxa: TaxaLookup.getInstance().getList()) {
            addTaxa(taxonomyNode, taxa);
        }

        TaxaLookup.getInstance().populate("test.bin");
        for (TaxonEntity taxa: TaxaLookup.getInstance().getList()) {
            addTaxa(taxonomyNode, taxa);
        }


            //TaxaLookup.getInstance().writeToXML();
        return taxonomyNode;
    }

//    private DefaultMutableTreeNode buildEchinodermNode(){
//        DefaultMutableTreeNode j = new DefaultMutableTreeNode("ECHINODERM");
//        EchinodermLookup.getInstance().populate();
//        for (TaxonEntity taxa: TaxaLookup.getInstance().getList()) {
//            addTaxa(j, taxa);
//        }
//        return j;
//    }


    private void addTaxa (DefaultMutableTreeNode parent , TaxonEntity taxon) {
        if (!taxon.getTaxaLevel().equals("GROUP") ) {
            DefaultMutableTreeNode thisNode = new DefaultMutableTreeNode(taxaNameAndCommonName(taxon));
            parent.add(thisNode);

            for (TaxonEntity  child: taxon.getTaxonsByTaxa()) {
                addTaxa(thisNode, child);
            }

            for (AllSpecyEntity species: taxon.getAllSpeciesesByTaxa()) {
                if (!species.getSpecies().equals("spp")) {
                    addSpecies(thisNode, species);

                }
            }
            
        }

    }

    private void addSpecies (DefaultMutableTreeNode parent , AllSpecyEntity species) {
        DefaultMutableTreeNode thisNode = new DefaultMutableTreeNode(taxaNameAndCommonName(species));
        parent.add(thisNode);


    }


    private String taxaNameAndCommonName(AllSpecyEntity species) {
        String commonNames = species.getCommonNames();
        if (commonNames==null) {
            return species.getSpecies();
        }
        return species.getSpecies() + "(" + commonNames + ")";
    }


    private String taxaNameAndCommonName(TaxonEntity taxon) {
        String commonNames = taxon.getCommonNames();
        if (commonNames==null) {
            return taxon.getTaxa() ;
        }
        if (commonNames.equals("Other")) {
            return taxon.getTaxa() ;
        }
        return taxon.getTaxa() + "(" + commonNames + ")";
    }

    

    public String addKeyword(String keyword) {
        DefaultMutableTreeNode child = new DefaultMutableTreeNode(keyword);
        keywordRoot.add(child);
        return getStringPath (child);


    }

    public DefaultMutableTreeNode getKeywordRoot(){
        return rootNode;
    }



}
