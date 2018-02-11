<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
				xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				xmlns:s="http://www.siri.org.uk/siri">

	<xsl:output method="text" encoding="UTF-8"/>

	<xsl:param name="lineNo"/>
	<xsl:param name="date"/>

	<xsl:template match="text()"/>

	<xsl:template
			match="s:Siri/s:ServiceDelivery/s:SituationExchangeDelivery/s:Situations/s:PtSituationElement">

		<xsl:if test="s:Affects/s:Networks/s:AffectedNetwork/s:AffectedLine/s:LineRef = $lineNo">

			<xsl:variable name="startDate" select="substring(s:ValidityPeriod/s:StartTime, 1, 11)"/>
			<xsl:variable name="endDate" select="substring(s:ValidityPeriod/s:EndTime, 1, 11)"/>
			<xsl:variable name="comparisonStartDate">
				<xsl:call-template name="compareDates">
					<xsl:with-param name="a" select="$date"/>
					<xsl:with-param name="b" select="$startDate"/>
				</xsl:call-template>
			</xsl:variable>
			<xsl:variable name="comparisonEndDate">
				<xsl:call-template name="compareDates">
					<xsl:with-param name="a" select="$date"/>
					<xsl:with-param name="b" select="$endDate"/>
				</xsl:call-template>
			</xsl:variable>

			<xsl:if test="$comparisonStartDate >= 0 and $comparisonEndDate &lt;= 0">
				<xsl:value-of select="s:Summary"/>
				<xsl:text>&#10;</xsl:text>
				<xsl:value-of select="s:Description"/>
				<xsl:text>&#10;</xsl:text>
				<xsl:value-of select="s:Detail"/>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template name="compareDates">
		<xsl:param name="a"/>
		<xsl:param name="b"/>
		<xsl:variable name="yearA" select="number(substring($a, 1, 4))"/>
		<xsl:variable name="yearB" select="number(substring($b, 1, 4))"/>
		<xsl:choose>
			<xsl:when test="$yearA = $yearB">
				<xsl:variable name="monthA" select="number(substring($a, 6, 2))"/>
				<xsl:variable name="monthB" select="number(substring($b, 6, 2))"/>
				<xsl:choose>
					<xsl:when test="$monthA = $monthB">
						<xsl:variable name="dayA" select="number(substring($a, 9, 2))"/>
						<xsl:variable name="dayB" select="number(substring($b, 9, 2))"/>
						<xsl:value-of select="$dayA - $dayB"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$monthA - $monthB"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$yearA - $yearB"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>