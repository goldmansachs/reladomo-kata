<?xml version='1.0'?>

<!--
  Copyright 2016 Goldman Sachs.
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
  -->

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:import href="../../target/docbook-lib/docbook/fo/docbook.xsl" />
    <xsl:import href="foHighlight.xsl" />

    <xsl:param name="html.stylesheet" select="'tourdoc.css'" />
    <!--
    <xsl:param name="section.autolabel" select="1" />
    <xsl:param name="chapter.autolabel" select="1" />
    <xsl:param name="appendix.autolabel" select="1" />
    <xsl:param name="section.label.includes.component.label" select="1" />
    -->
    <xsl:param name="highlight.source" select="1" />
    <xsl:param name="body.start.indent" select="0" />
    <xsl:param name="linenumbering.everyNth" select="1" />

    <xsl:param name="draft.mode" select="'no'"></xsl:param>
    <xsl:param name="shade.verbatim" select="1"></xsl:param>
    <xsl:param name="fop.extensions" select="0"></xsl:param>
    <xsl:param name="fop1.extensions" select="1"></xsl:param>

    <!-- Page related Settings -->
    <xsl:param name="page.margin.inner">1.5cm</xsl:param>
    <xsl:param name="page.margin.outer">1.5cm</xsl:param>
    <xsl:param name="title.margin.left">0pt</xsl:param>
    <xsl:param name="body.end.indent">0pt</xsl:param>

    <!-- Custom font settings -->
    <xsl:param name="title.font.family">sans-serif,SimHei</xsl:param>
    <xsl:param name="body.font.family">serif,SimSun</xsl:param>
    <xsl:param name="sans.font.family">sans-serif,SimHei</xsl:param>
    <xsl:param name="dingbat.font.family">serif,SimSun</xsl:param>
    <xsl:param name="monospace.font.family">monospace,FangSong,SimSun</xsl:param>

    <!-- Admonitions and callouts settings -->
    <xsl:param name="admon.textlabel" select="0" />
    <xsl:param name="admon.graphics" select="1" />
    <xsl:param name="admon.graphics.path">images/</xsl:param>
    <xsl:param name="admon.graphics.extension">.png</xsl:param>
    <xsl:param name="callout.graphics" select="1" />
    <xsl:param name="callout.graphics.extension">.png</xsl:param>

    <xsl:param name="variablelist.as.blocks" select="1" />

    <!-- Uncomment this to enable auto-numbering of sections -->
    <!-- xsl:param name="section.autolabel" select="1" / -->

    <xsl:attribute-set name="section.title.level1.properties">
        <xsl:attribute name="font-size">14pt</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="section.title.level2.properties">
        <xsl:attribute name="font-size">12pt</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="section.title.level3.properties">
        <xsl:attribute name="font-size">10pt</xsl:attribute>
    </xsl:attribute-set>

    <!-- Breaking long lines -->
    <xsl:param name="hyphenate.verbatim">0</xsl:param>
    <xsl:attribute-set name="monospace.verbatim.properties"
                       use-attribute-sets="verbatim.properties monospace.properties">
        <xsl:attribute name="wrap-option">wrap</xsl:attribute>
        <xsl:attribute name="hyphenation-character">&#x25BA;</xsl:attribute>
    </xsl:attribute-set>

    <!-- Colourize links in output -->
    <xsl:attribute-set name="xref.properties">
        <xsl:attribute name="color">
            <xsl:choose>
                <xsl:when test="self::ulink">blue</xsl:when>
                <xsl:when test="self::xref">blue</xsl:when>
                <xsl:when test="self::uri">blue</xsl:when>
                <xsl:otherwise>blue</xsl:otherwise>
            </xsl:choose>
        </xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="sidebar.properties" use-attribute-sets="formal.object.properties">
        <xsl:attribute name="border-style">solid</xsl:attribute>
        <xsl:attribute name="border-width">.1mm</xsl:attribute>
        <xsl:attribute name="background-color">#EEEEEE</xsl:attribute>
    </xsl:attribute-set>


    <xsl:attribute-set name="monospace.verbatim.properties" use-attribute-sets="verbatim.properties monospace.properties">
        <xsl:attribute name="text-align">start</xsl:attribute>
        <xsl:attribute name="wrap-option">wrap</xsl:attribute>
        <xsl:attribute name="keep-together.within-column">always</xsl:attribute>
        <xsl:attribute name="font-size">90%</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="shade.verbatim.style">
        <xsl:attribute name="background-color">#E0E0E0</xsl:attribute>
        <xsl:attribute name="border-width">0.5pt</xsl:attribute>
        <xsl:attribute name="border-style">solid</xsl:attribute>
        <xsl:attribute name="border-color">#575757</xsl:attribute>
        <xsl:attribute name="padding">3pt</xsl:attribute>
    </xsl:attribute-set>

    <!-- customize toc to exclude listing of tables, figures, examples -->
    <xsl:param name="generate.toc">
        appendix  toc,title
        article/appendix nop
        article toc,title
        book  toc,title
        chapter  toc,title
        part  toc,title
        preface   toc,title
        sect1 toc
        sect2 toc
        sect3 toc
        sect4 toc
    </xsl:param>

    <!-- format figure titles -->
    <!-- http://www.sagehill.net/docbookxsl/TitleFontSizes.html -->
    <xsl:attribute-set name="formal.title.properties"
                       use-attribute-sets="normal.para.spacing">
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="font-size">10pt</xsl:attribute>
        <xsl:attribute name="hyphenate">false</xsl:attribute>
        <xsl:attribute name="space-after.minimum">0.4em</xsl:attribute>
        <xsl:attribute name="space-after.optimum">0.6em</xsl:attribute>
        <xsl:attribute name="space-after.maximum">0.8em</xsl:attribute>
    </xsl:attribute-set>

</xsl:stylesheet>