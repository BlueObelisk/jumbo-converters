<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:cml="http://www.xml-cml.org/schema">
  
  <xsl:param name="inputParameters"/>
  
  <xml:output method="text"/>

  <xsl:variable name="atoms" select="//cml:atom"/>
  <!-- 
  <xsl:variable name="parameters" select="document($inputParameters)//cml:parameterList/cml:parameter"/>
  <xsl:variable name="basis" select="$parameters[@dictRef='cc:basis']"/>
  <xsl:variable name="calculation" select="$parameters[@dictRef='cc:calculation']/cml:scalar"/>
  <xsl:variable name="dftparam" select="$parameters[@dictRef='cc:dft']"/>
  <xsl:variable name="dftcalc" select="$calculation[.='dft']"/>
-->  
  
  <xsl:template match="/">
  <xsl:message>ATOMS<xsl:value-of select="count($atoms)"/></xsl:message>
  <!--  
  <xsl:message>INPUT<xsl:value-of select="$inputParameters"/></xsl:message>
  <xsl:message>PARAMETERS<xsl:value-of select="$parameters"/></xsl:message>
    <xsl:call-template name="writeHeader"/>
    <xsl:call-template name="writeGeometry"/>
    <xsl:call-template name="writeBasis"/>
    <xsl:call-template name="writeDft"/>
    <xsl:call-template name="writeCommands"/>
  -->
  </xsl:template>

  
  <xsl:template name="writeHeader">
    <xsl:text>start </xsl:text><xsl:value-of select="cml:cml/cml:module/@id"/><xsl:text>
</xsl:text>
    <xsl:text>title "</xsl:text><xsl:value-of select="cml:cml/cml:module/@title"/><xsl:text>"

</xsl:text>
  </xsl:template>
  
  <xsl:template name="writeGeometry">
<xsl:text>geometry </xsl:text>
<xsl:text>units an </xsl:text>
<xsl:text>noautoz nocenter noautosym
</xsl:text>
    <xsl:for-each select="$atoms">
<xsl:text>    </xsl:text>
     <xsl:value-of select="@elementType"/>
     <xsl:text> </xsl:text><xsl:value-of select="@x3"/>
     <xsl:text> </xsl:text><xsl:value-of select="@y3"/>
     <xsl:text> </xsl:text><xsl:value-of select="@z3"/>
<xsl:text>
</xsl:text>
    </xsl:for-each>
<xsl:text>end
</xsl:text>
  </xsl:template>

  <xsl:template name="writeBasis">
    <xsl:text>
basis
</xsl:text>
    <xsl:for-each select="$basis">
      <xsl:text>  </xsl:text><xsl:value-of select="cml:atomType"/><xsl:text> library </xsl:text><xsl:value-of select="cml:scalar"/>
      <xsl:text>
</xsl:text>
    </xsl:for-each>
    <xsl:text>end 
</xsl:text>
  </xsl:template>

  <xsl:template name="writeDft">
    <xsl:if test="count($dftcalc)=1">
      <xsl:text>
dft
</xsl:text>
    <xsl:if test="$dftparam">
      <xsl:text>  </xsl:text>
      <xsl:value-of select="$dftparam/cml:scalar[@dictRef='cc:dft.qualifier']"/><xsl:text> </xsl:text>
      <xsl:value-of select="$dftparam/cml:scalar[@dictRef='cc:dft.functional']"/>
      <xsl:text>
</xsl:text>
     </xsl:if>


Configuration config = new Configuration();
config.setClassForTemplateLoading();

Map<String,Object> model = new HaspMap...
model.put("job", myobject);

Template template = config.getTemplate("input.ftl");
template.process(model);

<#if module.basis??>${module.basis}</#if>
${module.basis!""}

<#list job.module['init'].atoms as atom>
${atom.element}  ${atom.x}  ${atom.y}  ${atom.z}
</#list>

      <xsl:text>end
</xsl:text>
    </xsl:if>
  </xsl:template>
  
  <xsl:template name="writeCommands">
    <xsl:text>
</xsl:text>
    <xsl:if test="count($dftcalc)=1 and $calculation[.='fukui']">
      <xsl:text>set dft:condfukui 1
</xsl:text>
    </xsl:if>
    <xsl:if test="count($dftcalc)=1">
      <xsl:text>task dft
</xsl:text>
    </xsl:if>
  </xsl:template>
  
  
</xsl:stylesheet>