package de.intranda.goobi.plugins;

import org.goobi.production.plugin.interfaces.IMetadataEditorExtension;

import de.sub.goobi.metadaten.Metadaten;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.xeoh.plugins.base.annotations.PluginImplementation;

@PluginImplementation
@Log4j2
public class CreateStructureElementsStepPlugin implements IMetadataEditorExtension {

    private static final long serialVersionUID = -4426478136402888473L;

    @Getter
    private String pagePath = "/uii/plugin_metadata_createStructureElements.xhtml"; //NOSONAR

    @Getter
    private String title = "intranda_metadata_createStructureElements";

    @Getter
    private String modalId = "createStructureElements";


    @Override
    public void initializePlugin(Metadaten bean) {

    }



}
