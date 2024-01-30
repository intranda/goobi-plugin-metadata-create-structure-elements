package de.intranda.goobi.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.apache.commons.lang3.StringUtils;
import org.goobi.production.plugin.interfaces.IMetadataEditorExtension;

import de.sub.goobi.config.ConfigPlugins;
import de.sub.goobi.helper.Helper;
import de.sub.goobi.metadaten.Metadaten;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import ugh.dl.DigitalDocument;
import ugh.dl.DocStruct;
import ugh.dl.DocStructType;
import ugh.dl.Metadata;
import ugh.dl.MetadataType;
import ugh.dl.Prefs;
import ugh.dl.Reference;
import ugh.exceptions.UGHException;

@PluginImplementation
@Log4j2
public class CreateStructureElementsPlugin implements IMetadataEditorExtension {

    private static final long serialVersionUID = -4426478136402888473L;

    @Getter
    // path to the modal xhtml page
    private String pagePath = "/uii/plugin_metadata_createStructureElements.xhtml"; //NOSONAR

    @Getter
    // plugin name, is used in menu, menu is ordered by this name. Can be translated
    private String title = "intranda_metadata_createStructureElements";

    @Getter
    // id if the modal to open
    private String modalId = "creationModal";

    @Getter
    @Setter
    private String selectedType;

    @Getter
    private SortedMap<String, DocStructType> docTypeMap;

    @Getter
    @Setter
    private int numberOfImages;

    @Getter
    @Setter
    private boolean generateTitleFromFilename;

    private DigitalDocument digitalDocument;
    private DocStruct logical;

    private DocStruct physical;

    private Prefs prefs;

    private Metadaten bean;

    @Override
    public void initializePlugin(Metadaten bean) {
        docTypeMap = new TreeMap<>();
        this.bean = bean;

        digitalDocument = bean.getDocument();

        physical = digitalDocument.getPhysicalDocStruct();
        logical = digitalDocument.getLogicalDocStruct();
        if (logical.getType().isAnchor()) {
            logical = logical.getAllChildren().get(0);
        }

        String docstructType = logical.getType().getName();

        // read default values from configuration
        XMLConfiguration xml = ConfigPlugins.getPluginConfig(title);
        xml.setExpressionEngine(new XPathExpressionEngine());

        String projectName = bean.getMyProzess().getProjekt().getTitel();
        SubnodeConfiguration config = null;
        try {
            // first try to find a block for the current project and docstruct
            config = xml.configurationAt("//config[project = '" + projectName + "'][doctype = '" + docstructType + "']");
        } catch (IllegalArgumentException e) {
            try {
                // if not configured, check for project and any docstruct
                config = xml.configurationAt("//config[project = '" + projectName + "'][doctype = '*']");
            } catch (IllegalArgumentException e1) {
                try {
                    // if not configured, check for the docstruct in any project
                    config = xml.configurationAt("//config[project = '*'][doctype = '" + docstructType + "']");
                } catch (IllegalArgumentException e2) {
                    try {
                        // then try to find a default configuration
                        config = xml.configurationAt("//config[project = '*'][doctype = '*']");
                    } catch (IllegalArgumentException e3) {
                        // abort, if no config found
                        log.info("No configuration file found, abort");
                        throw e;
                    }
                }
            }
        }

        String configuredDefaultType = config.getString("/defaultType", "");
        numberOfImages = config.getInt("/numberOfImagesPerElement", 1);

        log.trace("type: {}", logical.getType().getName());

        prefs = bean.getMyPrefs();
        String userLang = Helper.getMetadataLanguage();
        List<String> list = logical.getType().getAllAllowedDocStructTypes();
        for (String name : list) {
            DocStructType dst = prefs.getDocStrctTypeByName(name);
            String label = dst.getNameByLanguage(userLang);
            if (label == null) {
                label = dst.getName();
            }
            docTypeMap.put(label, dst);
            if (StringUtils.isNotBlank(configuredDefaultType) && name.equals(configuredDefaultType)) {
                selectedType = label;
            }
        }

        log.debug("found {} elements to add", docTypeMap.size());
    }

    public void generateElements() {
        if (physical.getAllChildren() == null) {
            // no pages found, abort
            return;
        }

        // preparation:

        // remove page assignments to existing sub elements
        if (logical.getAllChildren() != null) {
            for (DocStruct ds : logical.getAllChildren()) {
                List<Reference> refs = new ArrayList<>(ds.getAllToReferences());
                for (Reference ref : refs) {
                    ds.removeReferenceTo(ref.getTarget());
                }
            }
        }

        // clear old sub elements
        while (logical.getAllChildren() != null && !logical.getAllChildren().isEmpty()) {
            logical.removeChild(logical.getAllChildren().get(0));
        }

        // read pagination
        List<DocStruct> pagesList = physical.getAllChildren();
        List<String> imageNameList = new ArrayList<>(pagesList.size());

        // get image name list from pagination
        for (DocStruct page : pagesList) {
            String filename = page.getImageName();
            imageNameList.add(filename);
        }

        // generation:

        // run through all images

        DocStruct lastElement = null;

        DocStructType elementType = docTypeMap.get(selectedType);

        MetadataType titleType = prefs.getMetadataTypeByName("TitleDocMain");

        for (int pageNumber = 0; pageNumber < pagesList.size(); pageNumber++) {
            // check if new element must be created
            if (pageNumber % numberOfImages == 0) {
                try {
                    lastElement = digitalDocument.createDocStruct(elementType);
                    logical.addChild(lastElement);

                    // generate title from image name
                    if (generateTitleFromFilename) {
                        String filename = imageNameList.get(pageNumber);
                        Metadata titleMd = new Metadata(titleType);
                        titleMd.setValue(filename.substring(0, filename.lastIndexOf(".")));
                        lastElement.addMetadata(titleMd);
                    }
                } catch (UGHException e) {
                    log.error(e);
                }
            }

            // assign image to the last element
            lastElement.addReferenceTo(pagesList.get(pageNumber), "logical_physical");
        }

        // finally save and update left area
        bean.Reload();
    }

}
