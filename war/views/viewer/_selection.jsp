<%@ page import="edu.columbia.e6998.cloudexchange.aws.AWSCodes"%>
<%@ page import="edu.columbia.e6998.cloudexchange.aws.AWSCodes.*"%>
<%
	pageContext.setAttribute("regions", AWSCodes.Region.values());
	pageContext.setAttribute("zones", AWSCodes.Zones.values());
	pageContext.setAttribute("zone", AWSCodes.Zone.values());
	pageContext.setAttribute("os", AWSCodes.OS.values());
	pageContext.setAttribute("instancetype",AWSCodes.InstanceType.values());
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page isELIgnored="false"%>

<ul id="regions">
	<c:forEach var="r" items="${regions}">
		<c:choose>

			<c:when test="${r == defaults[0]}">
				<li id="${r}" class="regions selected">
			</c:when>

			<c:otherwise>
				<li id="${r}" class="regions">
			</c:otherwise>

		</c:choose>
		
		${r.view_name}
		</li>

	</c:forEach>
</ul>

<ul id="zones">
	<c:set var="i" value="0" />

	<c:forEach var="z" items="${zones}">

		<c:forEach var="av_zone" items="${z.zones}">

			<c:choose>

				<c:when test="${zone[i] == defaults[1]}">
					<li id="${zone[i]}" class="zones ${z} selected"
						style="display: inline;">
				</c:when>
				<c:when test="${z == defaults[0]}">
					<li id="${zone[i]}" class="zones ${z}" style="display: inline;">
				</c:when>
				<c:otherwise>
					<li id="${zone[i]}" class="zones ${z}" style="display: none;">
				</c:otherwise>

			</c:choose>
				
					${av_zone}
					</li>

			<c:set var="i" value="${i+1}" />
		</c:forEach>
	</c:forEach>
</ul>


<ul id="os">
	<c:forEach var="o" items="${os}">
		<c:choose>
			<c:when test="${o == defaults[2]}">
				<li id="${o}" class="os selected">
			</c:when>
			<c:otherwise>
				<li id="${o}" class="os">
			</c:otherwise>
		</c:choose>
		${o.description}
	</li>
	</c:forEach>
</ul>

<select id="instancetype">
	<c:forEach var="i" items="${instancetype}">
		<c:choose>
			<c:when test="${i == defaults[3] }">
				<option selected value="${i}" class="instances">${i.description}</option>
			</c:when>
			<c:otherwise>
				<option value="${i}" class="instances">${i.description}</option>
			</c:otherwise>
		</c:choose>
	</c:forEach>

</select>