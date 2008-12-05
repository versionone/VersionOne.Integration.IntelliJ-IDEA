<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version='1.0'>

	<xsl:import href="../Common/docbook/xhtml/docbook.xsl"/>
	
	<xsl:param name="html.stylesheet">../Common/v1integration.css</xsl:param>
	
	<xsl:param name="generate.toc" select="''"/>
	
	<xsl:param name="html.cellspacing">0</xsl:param>
	<xsl:param name="html.cellpadding">0</xsl:param>
	<xsl:param name="default.table.frame">none</xsl:param>
	<xsl:param name="id.warnings" select="0"/>
	<xsl:param name="ulink.target">_self</xsl:param>
	
</xsl:stylesheet>