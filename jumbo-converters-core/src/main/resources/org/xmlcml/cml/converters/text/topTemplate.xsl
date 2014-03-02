<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:output method="html"/>

<xsl:template match="/">
  <html>
    <head>
      <style type="text/css">
      .comment {
        background-color: #eeee77;
      }
  </style>
    </head>
    <xsl:apply-templates select=".//template"/>
  </html>
</xsl:template>

<xsl:template match="template">
  <div>
    <h1 class="title"><i><xsl:value-of select="@id"/></i>: <xsl:value-of select="@name"/></h1>
  </div>
  <table border="1">
    <tr><td>repeat</td><td><xsl:value-of select="@repeat | @repeatCount"/></td></tr>
    <tr><td>pattern</td><td><xsl:value-of select="@pattern"/></td></tr>
    <tr><td>offset</td><td><xsl:value-of select="@offset"/></td></tr>
    <tr><td>newline</td><td><xsl:value-of select="@multiple | @newline"/></td></tr>
    <tr><td>endPattern</td><td><xsl:value-of select="@endPattern"/></td></tr>
    <tr><td>endOffset</td><td><xsl:value-of select="@endOffset"/></td></tr>
  </table>
  <xsl:for-each select="comment">
    <div class="comment">
      <pre>
        <xsl:value-of select="."/>
      </pre>
    </div>
  </xsl:for-each>
  <xsl:apply-templates select="transform"/>
</xsl:template>

<xsl:template match="transform">
  <table border="2">
   <tr><td>ID</td><td><xsl:value-of select="@id"/></td></tr>
   <tr><td>parent</td><td><xsl:value-of select="@parent"/></td></tr>
   <tr><td>name/xpath</td><td><xsl:value-of select="@name"/></td></tr>
  </table>
</xsl:template>

</xsl:stylesheet>