
<!-- habilitar si esta prendido el status report -->
<div id="progressBox">
<%
org.test.streaming.Conf conf = (org.test.streaming.Conf)getServletContext().getAttribute("conf");
if(conf.isStatusReportEnabled()) {
%> 
<script type="text/javascript" src="jquery/jquery-2.0.3.min.js"></script>
<script type="text/javascript" src="jquery/bootstrap.min.js"></script>
<script type="text/javascript" src="jquery/planDrawer.js"></script>
<link rel="stylesheet" type="text/css" href="css/style.css" media="all" />
<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css" media="all" />
<link rel="stylesheet" type="text/css" href="css/bootstrap-theme.min.css" media="all" />

<h2>Plan: demo</h2>

	<div id="outerBox">
		<div id="pullerBox"></div>
		<div id="pushersBox"></div>
	</div>

	<script type="text/javascript">
		$(function() {
			drawPlan(
// 		"dummyPlanId"
		);
		});
	</script>

<%		
} else {
%>
Progress report disabled
<%
}
%>
</div>