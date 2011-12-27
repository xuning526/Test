<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Frameset//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="Content-language" content="en" />
	<meta http-equiv="expires" content="-1" />
	<meta http-equiv="pragma" content="no-cache" />
	<meta name="generator" content="testlink" />
	<meta name="author" content="Martin Havlat" />
	<meta name="copyright" content="GNU" />
	<meta name="robots" content="NOFOLLOW" />
	<base href="http://acg.q.aragoncg.com/" />
	<title>Quality Inner Frame</title>
	<style media="all" type="text/css">@import "http://acg.q.aragoncg.com/gui/themes/theme_m1/css/testlink.css";</style>
    	<script type="text/javascript" src="http://acg.platform.aragoncg.com/media/js/cookie.js"></script>
</head>

<body>
<script>
    var oid = get_url_param('project');
    selectProject(oid, 'quality');
</script>
    <div class="quality-nav">
        <a href="/">All Projects</a>&gt;&gt;Krugle - Development    </div>
    <div class="quality-head">
        <ul>
            <li class="active">
            <script>
            var obj = get_url_param('project');
            document.write("<a href=lib/general/frmWorkArea.php?feature=showMetrics&project="+obj+">Overview</a>")
            </script>
   
            </li>
            <li ><script>
            var obj = get_url_param('project');
            document.write("<a href=lib/general/frmWorkArea.php?feature=editTc&project="+obj+">Test Plans</a>")</script></li>
            <li ><script>
            var obj = get_url_param('project');
            document.write("<a href=lib/general/plan.php?project="+obj+">Test Suites</a>")</script></li>
            <li >
            <script>
            var obj = get_url_param('project');
            document.write("<a href=lib/plan/buildView.php?project="+obj+">Test Builds</a>");
            </script>
            </li>
        </ul>
    </div>
    <div id="quality-frames">
        <iframe frameborder="no" width="30%" src="lib/results/resultsNavigator.php" name="treeframe" scrolling="auto"></iframe>
        <iframe frameborder="no" width="68%" src="lib/results/charts.php" name="workframe" scrolling="auto"></iframe>
    </div>
</body>

</html>