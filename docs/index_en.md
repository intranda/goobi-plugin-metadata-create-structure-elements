---
title: Metadata extension for the creation of structural elements per image
identifier: intranda_metadata_createStructureElements
description: Metadata extension for the creation of structural elements per image
published: true
---

## Introduction
This documentation describes the installation, configuration and use of the plug-in for creating structural elements per image within the metadata editor.

## Installation
The following files must be installed in order to use the plug-in:

```bash
/opt/digiverso/goobi/plugins/metadata/plugin-metadataeditor-create-structure-elements-base.jar
/opt/digiverso/goobi/plugins/GUI/plugin-metadataeditor-create-structure-elements-gui.jar
/opt/digiverso/goobi/config/plugin_intranda_metadata_createStructureElements.xml
```


## Overview and functionality

This plug-in is a so-called `metadata editor plugin`. It can be selected in the metadata editor in the menu item for plug-ins under the name `Generate structure elements`.

![Open the plugin](screen_open_en.png)

When it is selected, a pop-up opens in which the desired type of structural elements to be generated can be selected. All structural elements that are permitted in the rule set for the publication type in question are automatically available here.

You can also define how many images should be assigned to the respective structure element before the next structure element is created and whether a title should be created for the structure element. If this option is activated, the file name without extension is entered as the title for each structure element, provided that the main title is permitted in the selected type.

![Popup](screen_popup_en.png)

The generation of the structure elements will overwrite all existing elements.

## Configuration
The plug-in is configured in the file `plugin_intranda_metadata_createStructureElements.xml` as shown here:

{{CONFIG_CONTENT}}

The configuration can be restricted to projects or to specific publication types. The fields `<project>` and `<doctype>` can be used for this purpose. In `<defaultType>` you can define which structural element should already be preselected in the list. If the element defined here does not exist in the list of the current publication type or is empty, no element is preselected.
In `<numberOfImagesPerElement>` a value for the number of images per structure element can also be preset. This must be a positive, whole number. Both values can be changed by the user in the interface.