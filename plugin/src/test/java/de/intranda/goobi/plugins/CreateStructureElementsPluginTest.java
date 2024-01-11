package de.intranda.goobi.plugins;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.goobi.beans.Process;
import org.goobi.beans.Project;
import org.goobi.beans.Step;
import org.goobi.beans.User;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import de.sub.goobi.config.ConfigurationHelper;
import de.sub.goobi.helper.VariableReplacer;
import de.sub.goobi.helper.enums.StepStatus;
import de.sub.goobi.metadaten.MetadatenHelper;
import de.sub.goobi.persistence.managers.MetadataManager;
import de.sub.goobi.persistence.managers.ProcessManager;
import ugh.dl.Prefs;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ MetadatenHelper.class, VariableReplacer.class, ConfigurationHelper.class, ProcessManager.class, MetadataManager.class })
@PowerMockIgnore({ "javax.management.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "javax.net.ssl.*", "jdk.internal.reflect.*" })
public class CreateStructureElementsPluginTest {

    private static String resourcesFolder;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File processDirectory;
    private File metadataDirectory;
    private Process process;
    private Step step;
    private Prefs prefs;

    @BeforeClass
    public static void setUpClass() throws Exception {
        resourcesFolder = "src/test/resources/"; // for junit tests in eclipse

        if (!Files.exists(Paths.get(resourcesFolder))) {
            resourcesFolder = "target/test-classes/"; // to run mvn test from cli or in jenkins
        }

        String log4jFile = resourcesFolder + "log4j2.xml"; // for junit tests in eclipse

        System.setProperty("log4j.configurationFile", log4jFile);
    }

    @Test
    public void testConstructor() throws Exception {
        CreateStructureElementsStepPlugin plugin = new CreateStructureElementsStepPlugin();
        assertNotNull(plugin);
    }

    @Test
    public void testTitle() {
        CreateStructureElementsStepPlugin plugin = new CreateStructureElementsStepPlugin();
        assertNotNull(plugin);
        assertEquals("intranda_metadata_createStructureElements", plugin.getTitle());
    }

    @Test
    public void testModalId() {
        CreateStructureElementsStepPlugin plugin = new CreateStructureElementsStepPlugin();
        assertNotNull(plugin);
        assertEquals("creationModal", plugin.getModalId());
    }

    @Test
    public void testPagePath() {
        CreateStructureElementsStepPlugin plugin = new CreateStructureElementsStepPlugin();
        assertNotNull(plugin);
        assertEquals("/uii/plugin_metadata_createStructureElements.xhtml", plugin.getPagePath());
    }

    public Process getProcess() {
        Project project = new Project();
        project.setTitel("SampleProject");

        Process process = new Process();
        process.setTitel("00469418X");
        process.setProjekt(project);
        process.setId(1);
        List<Step> steps = new ArrayList<>();
        step = new Step();
        step.setReihenfolge(1);
        step.setProzess(process);
        step.setTitel("test step");
        step.setBearbeitungsstatusEnum(StepStatus.OPEN);
        User user = new User();
        user.setVorname("Firstname");
        user.setNachname("Lastname");
        step.setBearbeitungsbenutzer(user);
        steps.add(step);

        process.setSchritte(steps);

        try {
            createProcessDirectory(processDirectory);
        } catch (IOException e) {
        }

        return process;
    }

    private void createProcessDirectory(File processDirectory) throws IOException {

        // image folder
        File imageDirectory = new File(processDirectory.getAbsolutePath(), "images");
        imageDirectory.mkdir();
        // master folder
        File masterDirectory = new File(imageDirectory.getAbsolutePath(), "00469418X_master");
        masterDirectory.mkdir();

        // media folder
        File mediaDirectory = new File(imageDirectory.getAbsolutePath(), "00469418X_media");
        mediaDirectory.mkdir();

        // TODO add some file
    }
}
