package de.intranda.goobi.plugins;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.goobi.production.plugin.interfaces.IMetadataEditorExtension;

import de.sub.goobi.helper.Helper;
import de.sub.goobi.metadaten.Metadaten;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import ugh.dl.DocStruct;
import ugh.dl.DocStructType;
import ugh.dl.Prefs;

@PluginImplementation
@Log4j2
public class CreateStructureElementsStepPlugin implements IMetadataEditorExtension {

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

    private Metadaten bean;

    @Getter @Setter
    private DocStructType selectedType;

    @Getter
    private SortedMap<String, DocStructType> docTypeMap;


    @Override
    public void initializePlugin(Metadaten bean) {
        this.bean = bean;
        docTypeMap = new TreeMap<>();

        // TODO get from config
        String configuredDefaultType = "";

        DocStruct logical =  bean.getDocument().getLogicalDocStruct();
        if (logical.getType().isAnchor()) {
            logical = logical.getAllChildren().get(0);
        }

        log.trace("type: {}", logical.getType().getName());

        Prefs prefs = bean.getMyPrefs();
        String userLang = Helper.getMetadataLanguage();
        List<String> list=logical.getType().getAllAllowedDocStructTypes();
        for (String name : list) {
            DocStructType dst = prefs.getDocStrctTypeByName(name);
            String label = dst.getNameByLanguage(userLang);
            if (label == null) {
                label = dst.getName();
            }
            docTypeMap.put(label, dst);


        }

        if (StringUtils.isNotBlank(configuredDefaultType)) {
            selectedType = docTypeMap.get(configuredDefaultType);
        }
        log.debug("found {} elements to add", docTypeMap.size());
    }



}
